package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@Table(name = "shopping_cart")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    private BigDecimal totalAmount;
    private Integer totalItems;
    private OffsetDateTime createdAt;

    @CreatedBy
    private UUID createdByUserId;
    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;
    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();

    @Builder
    public ShoppingCartPersistenceEntity(Long id, CustomerPersistenceEntity customer, BigDecimal totalAmount,
                                         Integer totalItems, OffsetDateTime createdAt, UUID createdByUserId,
                                         OffsetDateTime lastModifiedAt, UUID lastModifiedByUserId, Long version,
                                         Set<ShoppingCartItemPersistenceEntity> items) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.createdByUserId = createdByUserId;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.version = version;
        this.replaceItems(items);
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> items) {
        if (this.items == null) {
            this.items = new HashSet<>();
        }

        this.items.clear();

        if (items == null || items.isEmpty()) {
            return;
        }

        items.forEach(item -> item.setShoppingCart(this));
        this.items.addAll(items);
    }

    public UUID getCustomerId() {
        if (this.customer == null) {
            return null;
        }
        return customer.getId();
    }
}
