package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartPersistenceProvider implements ShoppingCarts {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final ShoppingCartPersistenceEntityAssembler assembler;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<ShoppingCart> ofId(ShoppingCartId shoppingCartId) {
        Optional<ShoppingCartPersistenceEntity> persistenceEntity = repository.findById(
                shoppingCartId.value()
        );

        return persistenceEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(ShoppingCartId shoppingCartId) {
        return repository.existsById(shoppingCartId.value());
    }

    @Override
    @Transactional
    public void add(ShoppingCart aggregateRoot) {
        UUID shoppingCartId = aggregateRoot.id().value();

        repository.findById(shoppingCartId).ifPresentOrElse(
                shoppingCartPersistenceEntity -> update(shoppingCartPersistenceEntity, aggregateRoot),
                () -> insert(aggregateRoot)
        );
    }

    void update(ShoppingCartPersistenceEntity persistenceEntity, ShoppingCart aggregateRoot) {
        if (!Objects.equals(aggregateRoot.version(), persistenceEntity.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(ShoppingCart.class, aggregateRoot.id());
        }

        persistenceEntity.setLastModifiedAt(OffsetDateTime.now());
        ShoppingCartPersistenceEntity shoppingCartToPersist = assembler.merge(persistenceEntity, aggregateRoot);
        entityManager.detach(shoppingCartToPersist);
        shoppingCartToPersist = repository.saveAndFlush(shoppingCartToPersist);
        updateVersion(aggregateRoot, shoppingCartToPersist);
    }

    void insert(ShoppingCart aggregateRoot) {
        ShoppingCartPersistenceEntity shoppingCartToPersist = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(shoppingCartToPersist);
        updateVersion(aggregateRoot, shoppingCartToPersist);
    }

    @Override
    public long count() {
        return repository.count();
    }



    @SneakyThrows
    private void updateVersion(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity shoppingCartToPersist) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, shoppingCartToPersist.getVersion());
        version.setAccessible(false);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(ShoppingCart shoppingCart) {
        repository.deleteById(shoppingCart.id().value());
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(ShoppingCartId shoppingCartId) {
        repository.deleteById(shoppingCartId.value());
    }

    @Override
    public Optional<ShoppingCart> ofCustomer(CustomerId customerId) {
        return repository.findByCustomer_Id(customerId.value())
                .map(disassembler::toDomainEntity);
    }
}
