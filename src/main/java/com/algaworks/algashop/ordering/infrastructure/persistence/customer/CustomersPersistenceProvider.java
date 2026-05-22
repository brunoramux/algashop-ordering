package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {

    private final CustomerPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityAssembler assembler;
    private final CustomerPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        Optional<CustomerPersistenceEntity> persistenceEntity = repository.findById(customerId.value());
        return persistenceEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return repository.existsById(customerId.value());
    }

    @Override
    @Transactional
    public void add(Customer aggregateRoot) {
        UUID customerId = aggregateRoot.id().value();

        repository.findById(customerId).ifPresentOrElse(
                customerPersistenceEntity -> update(customerPersistenceEntity, aggregateRoot),
                () -> insert(aggregateRoot)
        );

        aggregateRoot.clearDomainEvents();
    }

    private void insert(Customer aggregateRoot) {
        CustomerPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void update(CustomerPersistenceEntity customerPersistenceEntity, Customer aggregateRoot) {
        CustomerPersistenceEntity persistenceEntity = assembler.merge(customerPersistenceEntity, aggregateRoot);
        // usando detach para evitar conflitos no versionamento do registro no banco de dados.
        entityManager.detach(persistenceEntity);
        repository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Optional<Customer> ofEmail(Email email) {
        return repository.findByEmail(email.value()).map(disassembler::toDomainEntity);
    }

    @Override
    public boolean isEmailUnique(Email email, CustomerId customerId) {
        return !repository.existsByEmailAndIdNot(email.value(), customerId.value());
    }

    @SneakyThrows
    private void updateVersion(Customer aggregateRoot, CustomerPersistenceEntity customerToPersist) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, customerToPersist.getVersion());
        version.setAccessible(false);
    }


}
