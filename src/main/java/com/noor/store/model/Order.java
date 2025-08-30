package com.noor.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {@Index(columnList = "order_number", name = "idx_order_number")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. PO-000001 / SO-000001
    @Column(name = "order_number", unique = true, length = 40, nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 10)
    private OrderType orderType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal remainingAmount = BigDecimal.ZERO;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private OrderStatus status = OrderStatus.UNPAID;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.orderDate == null) this.orderDate = LocalDate.now();
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;
        this.remainingAmount = this.totalAmount.subtract(this.paidAmount);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;
        this.remainingAmount = this.totalAmount.subtract(this.paidAmount);
    }
}
