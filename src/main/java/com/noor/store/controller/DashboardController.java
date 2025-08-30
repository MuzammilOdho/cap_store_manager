package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.DashboardDTO;
import com.noor.store.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO.DashboardResponse>> get(@RequestParam(required = false) String month,
                                                                           @RequestParam(required = false) LocalDate start,
                                                                           @RequestParam(required = false) LocalDate end,
                                                                           @RequestParam(required = false, defaultValue = "15") int recentLimit) {
        return ResponseEntity.ok(ApiResponse.success(service.getDashboard(month, start, end, recentLimit), "Dashboard"));
    }
}
