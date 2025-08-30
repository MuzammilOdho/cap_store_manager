package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.CategoryDTO;
import com.noor.store.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO.Response>> create(@Valid @RequestBody CategoryDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(req), "Category created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryDTO.Response>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.list(pageable), "Categories"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO.Response>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id), "Category"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO.Request req) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, req), "Category updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted"));
    }
}
