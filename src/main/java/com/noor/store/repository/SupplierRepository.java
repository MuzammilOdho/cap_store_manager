package com.noor.store.repository;

import com.noor.store.model.Supplier;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByNameIgnoreCase(String name);
    Page<Supplier> findByNameContainingIgnoreCase(String q, Pageable pageable);
    List<Supplier> findByNameContainingIgnoreCase(String q);
}
