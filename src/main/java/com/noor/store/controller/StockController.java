package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.StockMovementDTO;
import com.noor.store.dto.StockAdjustmentDTO;
import com.noor.store.service.StockMovementService;
import com.noor.store.service.StockAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockMovementService movementService;
    private final StockAdjustmentService adjustmentService;

    @PostMapping("/movements")
    public ResponseEntity<ApiResponse<StockMovementDTO.Response>> createMovement(@Valid @RequestBody StockMovementDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(movementService.createMovement(req), "Movement recorded"));
    }

    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<Page<StockMovementDTO.Response>>> listMovements(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(movementService.list(pageable), "Movements"));
    }

    @PostMapping("/adjustments")
    public ResponseEntity<ApiResponse<StockAdjustmentDTO.Response>> createAdjustment(@Valid @RequestBody StockAdjustmentDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(adjustmentService.create(req), "Adjustment created"));
    }
}
