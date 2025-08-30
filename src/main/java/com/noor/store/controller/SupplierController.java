package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.SupplierDTO;
import com.noor.store.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierDTO.Response>> create(@Valid @RequestBody SupplierDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(req), "Supplier created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplierDTO.Response>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.list(pageable), "Suppliers"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO.Response>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id), "Supplier"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody SupplierDTO.Request req) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, req), "Supplier updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Supplier deleted"));
    }
}
