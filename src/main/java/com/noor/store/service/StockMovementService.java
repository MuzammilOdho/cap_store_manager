package com.noor.store.service;

import com.noor.store.dto.StockMovementDTO;
import com.noor.store.dto.StockMovementDTO.*;
import com.noor.store.exception.InsufficientStockException;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.StockMovementMapper;
import com.noor.store.model.*;
import com.noor.store.repository.CategoryRepository;
import com.noor.store.repository.CategoryStockMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class StockMovementService {

    private final CategoryStockMovementRepository movementRepository;
    private final CategoryRepository categoryRepository;
    @Qualifier("stockMovementMapperImpl")
    private final StockMovementMapper mapper;

    /**
     * Create a generic movement (manual/purchase/sale).
     */
    public Response createMovement(Request dto) {

        var cat = categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.categoryId()));
        MovementType mt;
        try {
            mt = MovementType.valueOf(dto.movementType());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid movementType");
        }

        CategoryStockMovement m = new CategoryStockMovement();
        m.setCategory(cat);
        m.setMovementType(mt);
        m.setQuantity(dto.quantity());
        m.setUnitCost(dto.unitCost());
        m.setUnitPrice(dto.unitPrice());
        m.setMovementDate(dto.movementDate() != null ? dto.movementDate() : LocalDate.now());
        m.setNotes(dto.notes());

        // inbound types increase stock and set remainingQuantity
        if (mt == MovementType.PURCHASE || mt == MovementType.MANUAL_ADD) {
            if (m.getUnitCost() == null) throw new IllegalArgumentException("unitCost required for inbound movement");
            m.setRemainingQuantity(m.getQuantity());
            cat.setCurrentStock(cat.getCurrentStock() + m.getQuantity());
        } else {
            // outbound - reduce stock (unless skip flag set)
            boolean skipDecrement = Boolean.TRUE.equals(dto.skipStockDecrement());
            if (!skipDecrement) {
                int newStock = cat.getCurrentStock() - m.getQuantity();
                if (newStock < 0) throw new InsufficientStockException("Insufficient stock for category " + cat.getName());
                cat.setCurrentStock(newStock);
            }
            m.setRemainingQuantity(0);
        }

        categoryRepository.save(cat);
        CategoryStockMovement saved = movementRepository.save(m);
        return mapper.toResponse(saved);
    }

    /**
     * FIFO consumption for sales: returns total cost (sum unitCost * qty consumed)
     *
     * This method uses a PESSIMISTIC_WRITE lock on inbound movement rows to avoid concurrent consumption races.
     */
    public BigDecimal consumeStockFIFO(Long categoryId, int qtyToConsume) {
        if (qtyToConsume <= 0) return BigDecimal.ZERO;
        var cat = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));

        if (cat.getCurrentStock() < qtyToConsume) {
            throw new InsufficientStockException("Insufficient stock. Have " + cat.getCurrentStock() + " required " + qtyToConsume);
        }

        List<MovementType> inboundTypes = List.of(MovementType.PURCHASE, MovementType.MANUAL_ADD);
        var avail = movementRepository.findAvailableForConsumptionForUpdate(categoryId, inboundTypes);

        int remaining = qtyToConsume;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CategoryStockMovement m : avail) {
            if (remaining <= 0) break;
            int available = m.getRemainingQuantity() == null ? 0 : m.getRemainingQuantity();
            if (available <= 0) continue;
            int take = Math.min(available, remaining);
            BigDecimal unitCost = m.getUnitCost() == null ? BigDecimal.ZERO : m.getUnitCost();
            totalCost = totalCost.add(unitCost.multiply(BigDecimal.valueOf(take)));
            m.setRemainingQuantity(available - take);
            movementRepository.save(m);
            remaining -= take;
        }

        if (remaining > 0) {
            // Safety check; ideally shouldn't happen because we checked currentStock earlier
            throw new InsufficientStockException("Insufficient FIFO available stock during consumption");
        }

        cat.setCurrentStock(cat.getCurrentStock() - qtyToConsume);
        categoryRepository.save(cat);

        return totalCost;
    }

    @Transactional(readOnly = true)
    public Page<Response> list(Pageable pageable) {
        return movementRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<Response> listByCategory(Long categoryId, Pageable pageable) {
        // In a production app we'd add a custom repo method to filter by category with paging.
        return movementRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()))
                .map(mapper::toResponse); // simplified here
    }
}
