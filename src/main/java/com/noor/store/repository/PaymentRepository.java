package com.noor.store.repository;

import com.noor.store.model.Payment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderId(Long orderId);

    Page<Payment> findByOrderId(Long orderId, Pageable pageable);

    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.paymentDate between :start and :end
             and p.paymentType = com.noor.store.model.PaymentType.SALE_PAYMENT
           """)
    BigDecimal sumPaymentsReceivedBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.paymentDate between :start and :end
             and p.paymentType = com.noor.store.model.PaymentType.PURCHASE_PAYMENT
           """)
    BigDecimal sumPaymentsToSuppliersBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           select p from Payment p
           order by p.paymentDate desc, p.id desc
           """)
    Page<Payment> findRecent(Pageable pageable);

    @Query("""
           select p from Payment p
           where p.order.id = :orderId
           order by p.paymentDate asc, p.id asc
           """)
    List<Payment> findAllByOrderIdOrdered(@Param("orderId") Long orderId);
}
