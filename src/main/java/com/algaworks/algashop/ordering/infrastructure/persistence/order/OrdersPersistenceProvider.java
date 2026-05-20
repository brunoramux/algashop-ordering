package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        return repository.existsById(orderId.value().toLong());
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Order aggregateRoot) {

        long orderId = aggregateRoot.id().value().toLong();

        repository.findById(orderId).ifPresentOrElse(
                orderPersistenceEntity -> update(orderPersistenceEntity, aggregateRoot),
                () -> insert(aggregateRoot)
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

    @Override
    public List<Order> placedByCustomerInYear(CustomerId customerId, Year year) {

        List<OrderPersistenceEntity> entity = repository.placedByCustomerInYear(customerId.value(), year.getValue());
        return entity.stream().map(disassembler::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public long selectQuantityByCustomerInYear(CustomerId customerId, Year year) {
        return repository.selectQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldForCustomer(CustomerId customerId) {
        return new Money(repository.totalSoldForCustomer(customerId.value()));
    }

    @SneakyThrows
    private void updateVersion(Order aggregateRoot, OrderPersistenceEntity orderToPersist) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, orderToPersist.getVersion());
        version.setAccessible(false);
    }


}
