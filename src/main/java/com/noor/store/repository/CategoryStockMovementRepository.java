package com.noor.store.repository;

import com.noor.store.model.CategoryStockMovement;
import com.noor.store.model.MovementType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

public interface CategoryStockMovementRepository extends JpaRepository<CategoryStockMovement, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m
            from CategoryStockMovement m
            where m.category.id = :categoryId
              and m.movementType in :inboundTypes
              and coalesce(m.remainingQuantity, 0) > 0
            order by m.movementDate asc, m.id asc
           """)
    List<CategoryStockMovement> findAvailableForConsumptionForUpdate(@Param("categoryId") Long categoryId,
                                                                     @Param("inboundTypes") List<MovementType> inboundTypes);

    @Query("""
           select coalesce(sum(m.remainingQuantity),0)
           from CategoryStockMovement m
           where m.category.id = :categoryId
             and m.movementType in :inboundTypes
           """)
    Integer currentStockQuantityForCategory(@Param("categoryId") Long categoryId,
                                            @Param("inboundTypes") List<MovementType> inboundTypes);

    @Query("""
           select coalesce(sum(m.unitCost * coalesce(m.remainingQuantity,0)), 0)
           from CategoryStockMovement m
           where m.movementType in :inboundTypes
           """)
    BigDecimal calculateTotalStockWorth(@Param("inboundTypes") List<MovementType> inboundTypes);

    /**
     * Returns current stock per category for the given inbound movement types.
     * Projection exposes (categoryId, categoryName, quantity) — useful for dashboards/reports
     */
    @Query("""
           select m.category.id as categoryId,
                  m.category.name as categoryName,
                  coalesce(sum(m.remainingQuantity), 0) as quantity
           from CategoryStockMovement m
           where m.movementType in :inboundTypes
           group by m.category.id, m.category.name
           """)
    List<CategoryStockQuantity> findCurrentStockByCategory(@Param("inboundTypes") List<MovementType> inboundTypes);

    interface CategoryStockQuantity {
        Long getCategoryId();
        String getCategoryName();
        Long getQuantity();
    }
}
