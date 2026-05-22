package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.TSIDGenerator;
import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository repository, CustomerPersistenceEntityRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @BeforeAll
    public void setup(){
        CustomerPersistenceEntity newCustomerPersistenceEntity = CustomerPersistenceEntityTestDataBuilder
                .aCustomer()
                .id(UUIDGenerator.generateTimeBasedUUID())
                .build();
        customerPersistenceEntity = customerRepository.saveAndFlush(newCustomerPersistenceEntity);
    }

    @Test
    public void shouldPersistOrder() {
        OrderPersistenceEntity order = OrderPersistenceEntityTestDataBuilder.existingOrder()
                .customer(customerPersistenceEntity)
                .build();

        repository.saveAndFlush(order);

        OrderPersistenceEntity orderPersisted = repository.findById(order.getId()).orElseThrow();
        Assertions.assertThat(repository.existsById(order.getId())).isTrue();
        Assertions.assertThat(orderPersisted.getId()).isEqualTo(order.getId());
        Assertions.assertThat(orderPersisted.getItems()).isNotNull();

    }
}