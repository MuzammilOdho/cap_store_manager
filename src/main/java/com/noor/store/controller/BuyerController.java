package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.BuyerDTO;
import com.noor.store.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/buyers")
@RequiredArgsConstructor
public class BuyerController {
    private final BuyerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<BuyerDTO.Response>> create(@Valid @RequestBody BuyerDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(req), "Buyer created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BuyerDTO.Response>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.list(pageable), "Buyers"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuyerDTO.Response>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id), "Buyer"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuyerDTO.Response>> update(@PathVariable Long id, @Valid @RequestBody BuyerDTO.Request req) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, req), "Buyer updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Buyer deleted"));
    }
}
