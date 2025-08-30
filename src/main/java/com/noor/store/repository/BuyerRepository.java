package com.noor.store.repository;

import com.noor.store.model.Buyer;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    boolean existsByNameIgnoreCase(String name);
    Page<Buyer> findByNameContainingIgnoreCase(String q, Pageable pageable);
    List<Buyer> findByNameContainingIgnoreCase(String q);
}
