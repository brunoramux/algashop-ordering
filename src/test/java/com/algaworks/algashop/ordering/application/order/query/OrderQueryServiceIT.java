package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.utility.PageFilter;
import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderQueryServiceIT {

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @Autowired
    private OrderQueryService orderQueryService;

    @Test
    public void shouldFindByIdAndReturnOrderDetails() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).withItems(true).build();
        orders.add(order);

        OrderDetailOutput output = orderQueryService.findById(order.id().value().toString());
        System.out.println(output);

        Assertions.assertThat(output).isNotNull();

    }

    @Test
    public void shouldFilterByPage() {
        Customer customer = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).withItems(false).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PAID).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.READY).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.CANCELED).customerId(customer.id()).build());

        Page<OrderSummaryOutput> page = orderQueryService.filter(new OrderFilter(3, 1));

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByCustomerId() {
        Customer customer1= CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).customerId(customer1.id()).build());

        Customer customer2 = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .email(new Email("tecbrunoramos@gmail.com"))
                .build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.READY).customerId(customer2.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setCustomerId(customer1.id().value());

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByOrderStatus() {
        Customer customer1 = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).customerId(customer1.id()).build());


        OrderFilter filter = new OrderFilter();
        filter.setStatus(OrderStatus.PLACED.toString());

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void shouldOrderByStatus() {
        Customer customer1 = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId(UUIDGenerator.generateTimeBasedUUID()))
                .build();
        customers.add(customer1);

        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.DRAFT).withItems(false).customerId(customer1.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PLACED).customerId(customer1.id()).build());

        Customer customer2 = CustomerTestDataBuilder.existingCustomer()
                .id(new CustomerId())
                .email(new Email("tecbrunoramos@gmail.com"))
                .build();
        customers.add(customer2);
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.PAID).customerId(customer2.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.READY).customerId(customer2.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatus(OrderStatus.CANCELED).customerId(customer2.id()).build());

        OrderFilter filter = new OrderFilter();
        filter.setSortByProperty(OrderFilter.SortType.STATUS);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<OrderSummaryOutput> page = orderQueryService.filter(filter);

        Assertions.assertThat(page.getContent().getFirst().getStatus()).isEqualTo(OrderStatus.CANCELED.toString());
    }

}