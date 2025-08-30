package com.noor.store.service;

import com.noor.store.dto.PrintableDTOs;
import com.noor.store.dto.PrintableDTOs.PrintableOrder;
import com.noor.store.dto.PrintableDTOs.PrintableOrderItem;
import com.noor.store.dto.PrintableDTOs.PrintablePayment;
import com.noor.store.dto.OrderDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PrintService {
    private final OrderService orderService;
    private final PaymentService paymentService;

    public PrintableOrder getPrintableOrder(Long orderId) {
        var order = orderService.get(orderId);
        List<PrintableOrderItem> items = order.items() == null ? List.of() : order.items().stream().map(i ->
                new PrintableOrderItem(i.id(), i.categoryName(), i.quantity(), i.unitPrice(), i.lineTotal(), i.cogs())
        ).collect(Collectors.toList());

        return new PrintableOrder(order.id(), order.orderNumber(), order.orderType(), order.orderDate(), order.buyerName(), order.supplierName(),
                items, order.totalAmount(), order.paidAmount(), order.remainingAmount(), order.status(), order.notes());
    }

    public PrintablePayment getPrintablePayment(Long paymentId) {
        var p = paymentService.get(paymentId);
        var order = orderService.get(p.orderId());
        return new PrintablePayment(p.id(), p.orderId(), order.orderNumber(), order.buyerName(), order.supplierName(), p.amount(), order.totalAmount(), order.paidAmount(), order.remainingAmount(), p.paymentDate(), p.paymentMethod()!=null ? p.paymentMethod().name() : null, p.paymentType()!=null ? p.paymentType().name():null, p.notes());
    }
}
