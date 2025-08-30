package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.ReportDTO;
import com.noor.store.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportDTO.Dashboard>> dashboard(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(service.dashboard(start, end), "Report"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<java.util.List<ReportDTO.CategoryReport>>> categoryReport(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success(service.categoryReport(start, end), "Category report"));
    }
}
