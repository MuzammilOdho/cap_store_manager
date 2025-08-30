package com.noor.store.repository;

import com.noor.store.model.MiscExpense;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MiscExpenseRepository extends JpaRepository<MiscExpense, Long> {

    @Query("""
           select e from MiscExpense e
           where e.expenseDate between :start and :end
           order by e.expenseDate desc, e.id desc
           """)
    List<MiscExpense> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           select coalesce(sum(e.amount), 0)
           from MiscExpense e
           where e.expenseDate between :start and :end
           """)
    BigDecimal sumByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           select coalesce(sum(e.amount), 0)
           from MiscExpense e
           where e.expenseDate between :start and :end
           """)
    BigDecimal sumExpensesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
