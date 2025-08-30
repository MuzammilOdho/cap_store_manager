package com.noor.store.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardDTO {
    public record StockSummary(BigDecimal totalWorthPurchase, BigDecimal totalWorthAtSalePrice, List<CategoryStock> byCategory) {}
    public record CategoryStock(Long categoryId, String categoryName, Integer quantity) {}

    public record FinanceSummary(BigDecimal receivables, BigDecimal payables, BigDecimal overdueReceivables, BigDecimal overduePayables) {}

    public record MonthlySummary(String month, BigDecimal totalSpend, BigDecimal totalGain, BigDecimal actualSpend, BigDecimal actualGain) {}

    public record MonthlyProfit(String month, BigDecimal totalProfit, BigDecimal actualProfit) {}

    public record ProfitSummary(BigDecimal totalProfit, BigDecimal actualProfit, List<MonthlyProfit> trend) {}

    public record RecentPayment(Long id, Long orderId, String orderNumber, String counterpartyName, String type, BigDecimal amount, LocalDate paymentDate) {}

    public record DashboardResponse(StockSummary stock, FinanceSummary finance, MonthlySummary monthly, ProfitSummary profit, List<RecentPayment> recentPayments) {}
}
