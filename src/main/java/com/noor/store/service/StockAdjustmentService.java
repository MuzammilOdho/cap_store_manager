package com.noor.store.service;

import com.noor.store.dto.StockAdjustmentDTO;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.StockAdjustmentMapper;
import com.noor.store.model.Category;
import com.noor.store.model.StockAdjustment;
import com.noor.store.repository.CategoryRepository;
import com.noor.store.repository.StockAdjustmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class StockAdjustmentService {

    private final StockAdjustmentRepository adjustmentRepository;
    private final CategoryRepository categoryRepository;
    @Qualifier("stockAdjustmentMapperImpl")
    private final StockAdjustmentMapper adjustmentMapper;

    public StockAdjustmentDTO.Response create(StockAdjustmentDTO.Request req) {
        Category c = categoryRepository.findById(req.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.categoryId()));
        StockAdjustment entity = adjustmentMapper.toEntity(req);
        if (entity.getAdjustmentType() == StockAdjustment.AdjustmentType.MANUAL_ADD) {
            c.setCurrentStock(c.getCurrentStock() + entity.getQuantityChanged());
        } else {
            int newStock = c.getCurrentStock() - entity.getQuantityChanged();
            if (newStock < 0) throw new IllegalStateException("Insufficient stock for adjustment");
            c.setCurrentStock(newStock);
        }
        categoryRepository.save(c);
        StockAdjustment saved = adjustmentRepository.save(entity);
        return adjustmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<StockAdjustment> list(Pageable pageable) {
        return adjustmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public StockAdjustment getById(Long id) {
        return adjustmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Adjustment not found: " + id));
    }
}
