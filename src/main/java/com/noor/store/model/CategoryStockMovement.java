package com.noor.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "category_stock_movements", indexes = {@Index(columnList = "category_id"), @Index(columnList = "movement_date")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // inbound/outbound types
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 30, nullable = false)
    private MovementType movementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // original quantity for the movement (for inbound: how much arrived; for sale: how much sold)
    @Column(nullable = false)
    private Integer quantity;

    // For inbound: the unit cost to compute stock worth
    @Column(name = "unit_cost", precision = 19, scale = 2)
    private BigDecimal unitCost;

    // For sale events might record sale unit price
    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    // remainingQuantity: for inbound movements only; how much still available to consume (FIFO)
    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    // link to order item (optional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "movement_date")
    private LocalDate movementDate;

    @Column(length = 2000)
    private String notes;
}
