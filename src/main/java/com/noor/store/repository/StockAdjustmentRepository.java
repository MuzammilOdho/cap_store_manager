package com.noor.store.repository;

import com.noor.store.model.StockAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    Page<StockAdjustment> findAll(Pageable pageable);
}
