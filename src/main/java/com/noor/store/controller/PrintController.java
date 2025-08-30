package com.noor.store.controller;

import com.noor.store.api.ApiResponse;
import com.noor.store.dto.PrintableDTOs;
import com.noor.store.service.PrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/print")
@RequiredArgsConstructor
public class PrintController {
    private final PrintService service;

    @GetMapping("/order/{id}/json")
    public ResponseEntity<ApiResponse<PrintableDTOs.PrintableOrder>> orderJson(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getPrintableOrder(id), "Printable order"));
    }

    @GetMapping("/payment/{id}/json")
    public ResponseEntity<ApiResponse<PrintableDTOs.PrintablePayment>> paymentJson(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getPrintablePayment(id), "Printable payment"));
    }

    @GetMapping("/order/{id}/html")
    public ResponseEntity<String> orderHtml(@PathVariable Long id) {
        var dto = service.getPrintableOrder(id);
        String html = buildOrderHtml(dto);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8").body(html);
    }

    @GetMapping("/payment/{id}/html")
    public ResponseEntity<String> paymentHtml(@PathVariable Long id) {
        var dto = service.getPrintablePayment(id);
        String html = buildPaymentHtml(dto);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8").body(html);
    }

    // Simple HTML builders (copy/paste friendly)
    private String buildOrderHtml(PrintableDTOs.PrintableOrder dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><title>Order</title>");
        sb.append("<style>body{font-family:Arial,Helvetica,sans-serif}table{width:100%;border-collapse:collapse}th,td{border:1px solid #ddd;padding:6px}</style>");
        sb.append("</head><body>");
        sb.append("<h2>Order Receipt</h2>");
        sb.append("<p><strong>Order:</strong> ").append(dto.orderNumber()).append("</p>");
        sb.append("<table><thead><tr><th>#</th><th>Item</th><th>Qty</th><th>Unit</th><th>Line</th></tr></thead><tbody>");
        int i=1;
        for (var it : dto.items()) {
            sb.append("<tr><td>").append(i++).append("</td><td>").append(it.categoryName()).append("</td><td>").append(it.quantity()).append("</td><td>").append(it.unitPrice()).append("</td><td>").append(it.lineTotal()).append("</td></tr>");
        }
        sb.append("</tbody></table>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildPaymentHtml(PrintableDTOs.PrintablePayment dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='utf-8'><title>Payment</title></head><body>");
        sb.append("<h2>Payment Receipt</h2>");
        sb.append("<p>Payment ID: ").append(dto.id()).append("</p>");
        sb.append("<p>Amount: ").append(dto.amount()).append("</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
