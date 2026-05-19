package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityAssembler {

    public CustomerPersistenceEntity fromDomain(Customer aggregateRoot) {
        return merge(new  CustomerPersistenceEntity(), aggregateRoot);
    }

    public CustomerPersistenceEntity merge(CustomerPersistenceEntity persistenceEntity, Customer aggregateRoot) {
        persistenceEntity.setId(aggregateRoot.id().value());
        persistenceEntity.setFirstName(aggregateRoot.fullName().firstName());
        persistenceEntity.setLastName(aggregateRoot.fullName().lastName());
        persistenceEntity.setBirthDate(aggregateRoot.birthDate().value());
        persistenceEntity.setEmail(aggregateRoot.email().value());
        persistenceEntity.setPhone(aggregateRoot.phone().value());
        persistenceEntity.setDocument(aggregateRoot.document().value());
        persistenceEntity.setPromotionNotificationsAllowed(aggregateRoot.isPromotionNotificationsAllowed());
        persistenceEntity.setArchived(aggregateRoot.isArchived());
        persistenceEntity.setRegisteredAt(aggregateRoot.registeredAt());
        persistenceEntity.setArchivedAt(aggregateRoot.archivedAt());
        persistenceEntity.setLoyaltyPoints(aggregateRoot.loyaltyPoints().value());
        persistenceEntity.setVersion(aggregateRoot.version());
        persistenceEntity.setAddress(toAddressEmbeddable(aggregateRoot.address()));

        return persistenceEntity;
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        if (address == null) {
            return null;
        }

        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .city(address.city())
                .state(address.state())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .zipCode(address.zipCode().value())
                .build();
    }



}
