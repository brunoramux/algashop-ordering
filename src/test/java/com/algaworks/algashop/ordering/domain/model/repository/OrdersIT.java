package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
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

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
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

    @Autowired
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

    @Test
    public void shouldListExistingOrdersByYear() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.PLACED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.PLACED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.CANCELLED)
                        .build()
                );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.DRAFT)
                        .build()
                );

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        List<Order> listedOrders = orders.placedByCustomerInYear(customerId, Year.now());
        Assertions.assertThat(listedOrders).isNotEmpty();
        Assertions.assertThat(listedOrders.size()).isEqualTo(2);

        listedOrders = orders.placedByCustomerInYear(customerId, Year.now().minusYears(1));
        Assertions.assertThat(listedOrders).isEmpty();

        listedOrders = orders.placedByCustomerInYear(new CustomerId(), Year.now());
        Assertions.assertThat(listedOrders).isEmpty();

    }

    @Test
    void shouldCountPlacedOrdersByYear() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.PLACED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.PLACED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.CANCELLED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.DRAFT)
                        .build()
        );

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        long ordersCount = orders.selectQuantityByCustomerInYear(customerId, Year.now());

        Assertions.assertThat(ordersCount).isEqualTo(2);
    }

    @Test
    void shouldGetTotalOrdersAmountForCustomerId() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);
        orders.add(
                OrderTestDataBuilder.anOrder()
                        .withItems(true)
                        .orderStatus(OrderStatus.PAID)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.PLACED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.CANCELLED)
                        .build()
        );

        orders.add(
                OrderTestDataBuilder.anOrder()
                        .orderStatus(OrderStatus.DRAFT)
                        .build()
        );

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Money money = orders.totalSoldForCustomer(customerId);


        Assertions.assertThat(money.value()).isEqualTo(new BigDecimal("16010.00"));
    }
}