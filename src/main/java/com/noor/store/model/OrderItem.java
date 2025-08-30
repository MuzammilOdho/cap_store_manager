package com.noor.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // owning side
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // category reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "line_total", precision = 19, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    // computed cogs for sales (sum of unit costs consumed)
    @Column(name = "cogs", precision = 19, scale = 2)
    private BigDecimal costOfGoodsSold = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.lineTotal == null && this.unitPrice != null && this.quantity != null) {
            this.lineTotal = this.unitPrice.multiply(java.math.BigDecimal.valueOf(this.quantity));
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        if (this.unitPrice != null && this.quantity != null) {
            this.lineTotal = this.unitPrice.multiply(java.math.BigDecimal.valueOf(this.quantity));
        }
    }
}
