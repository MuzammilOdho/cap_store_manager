package com.noor.store.service;

import com.noor.store.dto.CategoryDTO;
import com.noor.store.exception.DuplicateResourceException;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.CategoryMapper;
import com.noor.store.model.Category;
import com.noor.store.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryDTO.Response create(CategoryDTO.Request req) {
        if (repository.existsByNameIgnoreCase(req.name())) throw new DuplicateResourceException("Category exists: " + req.name());
        Category saved = repository.save(mapper.toEntity(req));
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO.Response> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoryDTO.Response get(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)));
    }

    public CategoryDTO.Response update(Long id, CategoryDTO.Request req) {
        Category c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        mapper.update(c, req);
        return mapper.toResponse(repository.save(c));
    }

    public void delete(Long id) {
        Category c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        repository.delete(c);
    }
}
