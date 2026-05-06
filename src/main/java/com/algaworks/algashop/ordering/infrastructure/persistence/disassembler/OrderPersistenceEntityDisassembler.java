package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
