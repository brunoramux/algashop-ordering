package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;


@DataJpaTest
@Import({ OrderPersistenceEntityDisassembler.class,  OrderPersistenceEntityAssembler.class, OrdersPersistenceProvider.class })
class OrdersIT {

    private Orders orders;

    @Autowired
    public OrdersIT(Orders orders) {
        this.orders = orders;
    }

    @Test
    public void shouldPersistAndFind(){
        Order order = OrderTestDataBuilder.anOrder()
                .orderStatus(OrderStatus.DRAFT)
                .build();
        orders.add(order);

        Optional<Order> possibleOrder = orders.ofId(order.id());

        Assertions.assertThat(possibleOrder).isPresent();

        Order savedOrder = possibleOrder.get();

        Assertions.assertThat(savedOrder).satisfies(
                s -> Assertions.assertThat(s.id()).isEqualTo(order.id()),
                s -> Assertions.assertThat(s.customerId()).isEqualTo(order.customerId()),
                s -> Assertions.assertThat(s.totalAmount()).isEqualTo(order.totalAmount())
        );
    }

    @Test
    void shouldUpdateExistingOrder(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).build();
        order.place();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldNotAllowStaleUpdates(){
        Order order = OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).build();
        orders.add(order);

        Order order1 = orders.ofId(order.id()).orElseThrow();
        Order order2 = orders.ofId(order.id()).orElseThrow();

        order1.markAsPaid();
        orders.add(order1);

        // Mesma Order acabou de ser alterada no Banco de Dados, portanto a instância local está desatualizada (STALE)
        order2.cancel();

        // Alteração não deve ser permitida
        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(order2));

    }
}