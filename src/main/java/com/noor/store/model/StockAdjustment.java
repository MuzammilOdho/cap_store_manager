package com.noor.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment {

    public enum AdjustmentType { MANUAL_ADD, MANUAL_REMOVE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", length = 30, nullable = false)
    private AdjustmentType adjustmentType;

    @Column(name = "quantity_changed", nullable = false)
    private Integer quantityChanged;

    @Column(length = 1000)
    private String reason;

    @Column(name = "adjustment_date")
    private LocalDateTime adjustmentDate = LocalDateTime.now();
}
