package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .version(persistenceEntity.getVersion())
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlacedAt())
                .paidAt(persistenceEntity.getPaidAt())
                .canceledAt(persistenceEntity.getCanceledAt())
                .readyAt(persistenceEntity.getReadyAt())
                .items(new HashSet<>())
                .billing(toDomainBilling(persistenceEntity.getBilling()))
                .shipping(toDomainShipping(persistenceEntity.getShipping()))
                .items(toDomainEntity(persistenceEntity.getItems()))
                .build();
    }

    private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
        return items.stream().map(
                this::toDomainEntity
        ).collect(Collectors.toSet());
    }

    private OrderItem toDomainEntity(OrderItemPersistenceEntity orderItem) {
        return OrderItem.existing()
                .id(new OrderItemId(orderItem.getId()))
                .quantity(new Quantity(orderItem.getQuantity()))
                .price(new Money(orderItem.getPrice()))
                .orderId(new OrderId(orderItem.getOrderId()))
                .productId(new ProductId(orderItem.getProductId()))
                .productName(new ProductName(orderItem.getProductName()))
                .totalAmount(new Money(orderItem.getTotalAmount()))
                .build();
    }


    private Shipping toDomainShipping(ShippingEmbeddable shipping) {
        return Shipping.builder()
                .cost(new Money(shipping.getCost()))
                .expectedDate(shipping.getExpectedDate())
                .recipient(
                        Recipient.builder()
                                .fullName(new FullName(shipping.getRecipient().getFirstName(), shipping.getRecipient().getLastName()))
                                .document(new Document(shipping.getRecipient().getDocument()))
                                .phone(new Phone(shipping.getRecipient().getPhone()))
                                .build()
                )
                .address(toDomainAddress(shipping.getAddress()))
                .build();
    }

    private Billing toDomainBilling(BillingEmbeddable billing) {
        if (billing == null) return null;

        return Billing.builder()
                .fullName(new FullName(billing.getFirstName(), billing.getLastName()))
                .phone(new Phone(billing.getPhone()))
                .email(new Email(billing.getEmail()))
                .document(new Document(billing.getDocument()))
                .address(toDomainAddress(billing.getAddress()))
                .build();
    }

    private Address toDomainAddress(AddressEmbeddable address) {
        if (address == null) return null;

        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .city(address.getCity())
                .state(address.getState())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .zipCode(new ZipCode(address.getZipCode()))
                .build();
    }

}
