package com.noor.store.service;

import com.noor.store.dto.SupplierDTO;
import com.noor.store.exception.DuplicateResourceException;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.SupplierMapper;
import com.noor.store.model.Supplier;
import com.noor.store.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class SupplierService {
    private final SupplierRepository repository;
    private final SupplierMapper mapper;

    public SupplierDTO.Response create(SupplierDTO.Request req) {
        if (repository.existsByNameIgnoreCase(req.name())) throw new DuplicateResourceException("Supplier exists: " + req.name());
        Supplier saved = repository.save(mapper.toEntity(req));
        return mapper.toResponse(saved);
    }

    public SupplierDTO.Response update(Long id, SupplierDTO.Request req) {
        Supplier s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        mapper.update(s, req);
        return mapper.toResponse(repository.save(s));
    }

    @Transactional(readOnly = true)
    public SupplierDTO.Response get(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<SupplierDTO.Response> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public void delete(Long id) {
        Supplier s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        repository.delete(s);
    }
}
