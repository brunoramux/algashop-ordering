package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(CustomerPersistenceEntity customerPersistenceEntity) {
        return Customer.existing()
                .id(new CustomerId(customerPersistenceEntity.getId()))
                .fullName(new FullName(customerPersistenceEntity.getFirstName(), customerPersistenceEntity.getLastName()))
                .birthDate(new BirthDate(customerPersistenceEntity.getBirthDate()))
                .email(new Email(customerPersistenceEntity.getEmail()))
                .phone(new Phone(customerPersistenceEntity.getPhone()))
                .document(new Document(customerPersistenceEntity.getDocument()))
                .promotionNotificationsAllowed(customerPersistenceEntity.getPromotionNotificationsAllowed())
                .archived(customerPersistenceEntity.getArchived())
                .registeredAt(customerPersistenceEntity.getRegisteredAt())
                .archivedAt(customerPersistenceEntity.getArchivedAt())
                .loyaltyPoints(new LoyaltyPoints(customerPersistenceEntity.getLoyaltyPoints()))
                .version(customerPersistenceEntity.getVersion())
                .address(toDomainAddress(customerPersistenceEntity.getAddress()))
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
