package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;


@DataJpaTest
@Import({ OrderPersistenceEntityDisassembler.class,
        OrderPersistenceEntityAssembler.class,
        OrdersPersistenceProvider.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        })
class OrdersIT {

    private Orders orders;
    private Customers customers;

    @Autowired
    public OrdersIT(Orders orders, Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @Test
    public void shouldPersistAndFind(){

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
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
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .build();
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
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .build();
        order.place();
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

    @Test
    void shouldCountExistingOrders(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Assertions.assertThat(orders.count()).isZero();

        Order order = OrderTestDataBuilder.anOrder().build();
        Order order2 = OrderTestDataBuilder.anOrder().build();

        orders.add(order);
        orders.add(order2);

        Assertions.assertThat(orders.count()).isEqualTo(2L);
    }

    @Test
    void shouldReturnIfOrderExist(){

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .build();
        orders.add(order);

        Assertions.assertThat(orders.exists(order.id())).isTrue();
        Assertions.assertThat(orders.exists(new OrderId())).isEqualTo(false);
    }
}