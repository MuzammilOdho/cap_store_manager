package com.noor.store.service;

import com.noor.store.dto.BuyerDTO;
import com.noor.store.exception.DuplicateResourceException;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.BuyerMapper;
import com.noor.store.model.Buyer;
import com.noor.store.repository.BuyerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BuyerService {
    private final BuyerRepository repository;
    private final BuyerMapper mapper;

    public BuyerDTO.Response create(BuyerDTO.Request req) {
        if (repository.existsByNameIgnoreCase(req.name())) throw new DuplicateResourceException("Buyer exists: " + req.name());
        Buyer saved = repository.save(mapper.toEntity(req));
        return mapper.toResponse(saved);
    }

    public BuyerDTO.Response update(Long id, BuyerDTO.Request req) {
        Buyer b = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + id));
        mapper.update(b, req);
        return mapper.toResponse(repository.save(b));
    }

    @Transactional(readOnly = true)
    public BuyerDTO.Response get(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<BuyerDTO.Response> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public void delete(Long id) {
        Buyer b = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Buyer not found: " + id));
        repository.delete(b);
    }

    @Transactional(readOnly = true)
    public List<BuyerDTO.Response> search(String q) {
        return repository.findByNameContainingIgnoreCase(q).stream().map(mapper::toResponse).toList();
    }
}
