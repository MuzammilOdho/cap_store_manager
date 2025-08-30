package com.noor.store.repository;

import com.noor.store.model.OrderItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
           select coalesce(sum(oi.costOfGoodsSold), 0)
           from OrderItem oi
           join oi.order o
           where o.orderType = com.noor.store.model.OrderType.SALE
             and o.orderDate between :start and :end
           """)
    BigDecimal sumCogsForSalesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
