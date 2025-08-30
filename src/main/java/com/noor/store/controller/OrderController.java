package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.OrderDTO;
import com.noor.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO.OrderResponse>> create(@Valid @RequestBody OrderDTO.OrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(service.create(req), "Order created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO.OrderResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.get(id), "Order"));
    }

    /**
     * List orders with optional filters:
     * /api/v1/orders?orderType=SALE&buyerId=1&start=2025-08-01&end=2025-08-31&page=0&size=20&sort=orderDate,desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDTO.OrderResponse>>> list(
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) java.time.LocalDate start,
            @RequestParam(required = false) java.time.LocalDate end,
            Pageable pageable) {

        com.noor.store.model.OrderType type = orderType == null ? null : com.noor.store.model.OrderType.valueOf(orderType);
        com.noor.store.model.OrderStatus st = status == null ? null : com.noor.store.model.OrderStatus.valueOf(status);

        return ResponseEntity.ok(ApiResponse.success(service.filter(type, start, end, st, buyerId, supplierId, pageable), "Orders"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Order soft-deleted"));
    }
}
