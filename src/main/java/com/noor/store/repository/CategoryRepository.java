package com.noor.store.repository;

import com.noor.store.model.Category;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    Page<Category> findByNameContainingIgnoreCase(String q, Pageable pageable);
    List<Category> findByNameContainingIgnoreCase(String q);
    Optional<Category> findByNameIgnoreCase(String name);
}
