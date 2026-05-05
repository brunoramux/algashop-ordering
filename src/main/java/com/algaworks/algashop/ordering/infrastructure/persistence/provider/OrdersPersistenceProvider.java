package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository repository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        Optional<OrderPersistenceEntity> persistenceEntity = repository.findById(orderId.value().toLong());

        return persistenceEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return false;
    }

    @Override
    public void add(Order aggregateRoot) {

        long orderId = aggregateRoot.id().value().toLong();

        repository.findById(orderId).ifPresentOrElse(
                (orderPersistenceEntity) -> {
                    update(orderPersistenceEntity, aggregateRoot);
                },
                () -> {
                    insert(aggregateRoot);
                }
        );
    }

    void update(OrderPersistenceEntity orderPersistenceEntity, Order aggregateRoot) {
        OrderPersistenceEntity orderToPersist = assembler.merge(orderPersistenceEntity, aggregateRoot);
        entityManager.detach(orderToPersist);
        orderToPersist = repository.saveAndFlush(orderToPersist);
        updateVersion(aggregateRoot, orderToPersist);
    }

    void insert(Order aggregateRoot) {
        OrderPersistenceEntity orderToPersist = assembler.fromDomain(aggregateRoot);
        repository.saveAndFlush(orderToPersist);
        updateVersion(aggregateRoot, orderToPersist);
    }

    @SneakyThrows
    private void updateVersion(Order aggregateRoot, OrderPersistenceEntity orderToPersist) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, orderToPersist.getVersion());
        version.setAccessible(false);
    }

    @Override
    public int count() {
        return 0;
    }
}
