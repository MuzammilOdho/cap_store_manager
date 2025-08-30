package com.noor.store.service;

import com.noor.store.dto.DashboardDTO;
import com.noor.store.model.MovementType;
import com.noor.store.repository.CategoryStockMovementRepository;
import com.noor.store.repository.OrderRepository;
import com.noor.store.repository.PaymentRepository;
import com.noor.store.repository.MiscExpenseRepository;
import com.noor.store.repository.OrderItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final CategoryStockMovementRepository stockMovementRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final MiscExpenseRepository miscExpenseRepository;
    private final OrderItemRepository orderItemRepository;

    private static final java.time.format.DateTimeFormatter YM = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");

    public DashboardDTO.DashboardResponse getDashboard(String month, LocalDate start, LocalDate end, int recentLimit) {
        LocalDate monthStart;
        LocalDate monthEnd;
        if (month != null && !month.isBlank()) {
            YearMonth ym = YearMonth.parse(month, YM);
            monthStart = ym.atDay(1);
            monthEnd = ym.atEndOfMonth();
        } else if (start != null && end != null) {
            monthStart = start;
            monthEnd = end;
        } else {
            YearMonth now = YearMonth.now();
            monthStart = now.atDay(1);
            monthEnd = now.atEndOfMonth();
        }

        List<MovementType> inbound = List.of(MovementType.PURCHASE, MovementType.MANUAL_ADD);

        BigDecimal totalWorthPurchase = stockMovementRepository.calculateTotalStockWorth(inbound);
        if (totalWorthPurchase == null) totalWorthPurchase = BigDecimal.ZERO;

        // sale price worth (approx) - reuse calculateTotalStockWorth by using unitPrice instead of unitCost not present, so left as 0 for now
        BigDecimal totalWorthSale = BigDecimal.ZERO;

        var raw = stockMovementRepository.findCurrentStockByCategory(inbound);
        var byCategory = raw.stream().map(r -> new DashboardDTO.CategoryStock(r.getCategoryId(), r.getCategoryName(), r.getQuantity().intValue())).collect(Collectors.toList());

        DashboardDTO.StockSummary stockSummary = new DashboardDTO.StockSummary(totalWorthPurchase, totalWorthSale, byCategory);

        BigDecimal receivables = defaultZero(orderRepository.totalReceivables());
        BigDecimal payables = defaultZero(orderRepository.totalPayables());

        LocalDate today = LocalDate.now();

        BigDecimal overdueReceiv = orderRepository.findFiltered(null, null, today.minusYears(100), null, null, null, PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .filter(o -> o.getOrderType() == com.noor.store.model.OrderType.SALE)
                .filter(o -> o.getRemainingAmount() != null && o.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(o -> o.getDueDate() != null && o.getDueDate().isBefore(today))
                .map(o -> o.getRemainingAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overduePay = orderRepository.findFiltered(null, null, today.minusYears(100), null, null, null, PageRequest.of(0, Integer.MAX_VALUE)).stream()
                .filter(o -> o.getOrderType() == com.noor.store.model.OrderType.PURCHASE)
                .filter(o -> o.getRemainingAmount() != null && o.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(o -> o.getDueDate() != null && o.getDueDate().isBefore(today))
                .map(o -> o.getRemainingAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardDTO.FinanceSummary financeSummary = new DashboardDTO.FinanceSummary(receivables, payables, overdueReceiv, overduePay);

        BigDecimal totalSpend = defaultZero(orderRepository.sumTotalAmountByTypeBetween(com.noor.store.model.OrderType.PURCHASE, monthStart, monthEnd));
        BigDecimal totalGain = defaultZero(orderRepository.sumTotalAmountByTypeBetween(com.noor.store.model.OrderType.SALE, monthStart, monthEnd));

        BigDecimal cogs = defaultZero(orderItemRepository.sumCogsForSalesBetween(monthStart, monthEnd));
        BigDecimal actualGain = defaultZero(paymentRepository.sumPaymentsReceivedBetween(monthStart, monthEnd));
        BigDecimal actualSpend = defaultZero(paymentRepository.sumPaymentsToSuppliersBetween(monthStart, monthEnd));
        BigDecimal miscExpenses = defaultZero(miscExpenseRepository.sumExpensesBetween(monthStart, monthEnd));

        DashboardDTO.MonthlySummary monthlySummary = new DashboardDTO.MonthlySummary(monthStart.format(YM), totalSpend, totalGain, actualSpend.add(miscExpenses), actualGain);

        // Profit using accrual (revenue - COGS - miscExpenses)
        BigDecimal totalProfit = totalGain.subtract(cogs).subtract(miscExpenses);
        BigDecimal actualProfit = actualGain.subtract(actualSpend).subtract(miscExpenses);

        // trend last 6 months
        List<DashboardDTO.MonthlyProfit> trend = new ArrayList<>();
        YearMonth ymStart = YearMonth.from(monthStart).minusMonths(5);
        for (int i = 0; i < 6; i++) {
            YearMonth ym = ymStart.plusMonths(i);
            LocalDate s = ym.atDay(1);
            LocalDate e = ym.atEndOfMonth();
            BigDecimal ts = defaultZero(orderRepository.sumTotalAmountByTypeBetween(com.noor.store.model.OrderType.PURCHASE, s, e));
            BigDecimal tg = defaultZero(orderRepository.sumTotalAmountByTypeBetween(com.noor.store.model.OrderType.SALE, s, e));
            BigDecimal ag = defaultZero(paymentRepository.sumPaymentsReceivedBetween(s, e));
            BigDecimal as = defaultZero(paymentRepository.sumPaymentsToSuppliersBetween(s, e));
            BigDecimal me = defaultZero(miscExpenseRepository.sumExpensesBetween(s, e));
            BigDecimal monthCogs = defaultZero(orderItemRepository.sumCogsForSalesBetween(s, e));
            BigDecimal monthProfit = tg.subtract(monthCogs).subtract(me);
            BigDecimal monthActualProfit = ag.subtract(as).subtract(me);
            trend.add(new DashboardDTO.MonthlyProfit(ym.format(YM), monthProfit, monthActualProfit));
        }

        DashboardDTO.ProfitSummary profitSummary = new DashboardDTO.ProfitSummary(totalProfit, actualProfit, trend);

        var recentPayments = paymentRepository.findRecent(PageRequest.of(0, Math.max(1, recentLimit))).stream().map(p ->
                new DashboardDTO.RecentPayment(p.getId(), p.getOrder().getId(), p.getOrder().getOrderNumber(), p.getOrder().getBuyer()!=null ? p.getOrder().getBuyer().getName() : p.getOrder().getSupplier()!=null ? p.getOrder().getSupplier().getName() : null,
                        p.getOrder().getOrderType() == com.noor.store.model.OrderType.SALE ? "RECEIVED":"PAID", p.getAmount(), p.getPaymentDate())
        ).collect(Collectors.toList());

        return new DashboardDTO.DashboardResponse(stockSummary, financeSummary, monthlySummary, profitSummary, recentPayments);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
