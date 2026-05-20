package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.TSIDGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT {

    private final OrderPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository repository, CustomerPersistenceEntityRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    public void setup(){
        CustomerPersistenceEntity newCustomerPersistenceEntity = CustomerPersistenceEntityTestDataBuilder.aCustomer().build();
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

    @Test
    public void shouldCount(){
        long count = repository.count();
        Assertions.assertThat(count).isZero();
    }

    @Test
    void shouldGenerateAuditingInformation(){
        long orderId = TSIDGenerator.generateTSID().toLong();
        OrderPersistenceEntity order = OrderPersistenceEntity.builder()
                .id(orderId)
                .customer(customerPersistenceEntity)
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .build();

        OrderPersistenceEntity orderPersistenceEntity = repository.saveAndFlush(order);

        Assertions.assertThat(orderPersistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(orderPersistenceEntity.getLastModifiedAt()).isNotNull();
    }



}