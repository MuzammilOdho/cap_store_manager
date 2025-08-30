package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.PaymentDTO;
import com.noor.store.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO.Response>> create(@Valid @RequestBody PaymentDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(req), "Payment recorded"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO.Response>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id), "Payment"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentDTO.Response>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.list(pageable), "Payments"));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Page<PaymentDTO.Response>>> listByOrder(@PathVariable Long orderId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.listByOrder(orderId, pageable), "Payments for order"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Payment deleted"));
    }
}
