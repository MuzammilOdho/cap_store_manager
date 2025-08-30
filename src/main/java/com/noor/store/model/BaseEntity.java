package com.noor.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Centralized audit fields. All entities that require createdAt/updatedAt should extend this.
 * No other entity should define @PrePersist/@PreUpdate for the same fields to avoid duplication.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "created_at", updatable = false, nullable = false)
    protected Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    protected Instant updatedAt;

    /**
     * Optimistic locking optional: helps prevent lost updates on concurrent modifications.
     */
    @Version
    @Column(name = "version")
    protected Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
