package com.noor.store.repository;

import com.noor.store.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findTopByOrderByIdDesc();

    @Query("""
            select coalesce(sum(o.totalAmount), 0)
            from Order o
            where o.orderType = :type
              and o.orderDate between :start and :end
            and o.deleted = false
            """)
    BigDecimal sumTotalAmountByTypeBetween(@Param("type") OrderType type,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    @EntityGraph(attributePaths = {"items", "items.category", "buyer", "supplier"})
    @Query("""
            select o from Order o
            where (:type is null or o.orderType = :type)
              and (:start is null or o.orderDate >= :start)
              and (:end is null or o.orderDate <= :end)
              and (:status is null or o.status = :status)
              and (:buyerId is null or o.buyer.id = :buyerId)
              and (:supplierId is null or o.supplier.id = :supplierId)
              and o.deleted = false
            order by o.orderDate desc
            """)
    Page<Order> findFiltered(@Param("type") OrderType type,
                             @Param("start") LocalDate start,
                             @Param("end") LocalDate end,
                             @Param("status") OrderStatus status,
                             @Param("buyerId") Long buyerId,
                             @Param("supplierId") Long supplierId,
                             Pageable pageable);

    @Query("""
            select oi
            from OrderItem oi
            join oi.order o
            where oi.category.id = :categoryId
              and o.orderType = com.noor.store.model.OrderType.SALE
              and o.orderDate between :start and :end
            """)
    List<OrderItem> findOrderItemsByCategory(@Param("categoryId") Long categoryId,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

    @Query("""
            select coalesce(sum(o.remainingAmount), 0)
            from Order o
            where o.orderType = com.noor.store.model.OrderType.SALE
              and o.deleted = false
            """)
    BigDecimal totalReceivables();

    @Query("""
            select coalesce(sum(o.remainingAmount), 0)
            from Order o
            where o.orderType = com.noor.store.model.OrderType.PURCHASE
              and o.deleted = false
            """)
    BigDecimal totalPayables();
}
