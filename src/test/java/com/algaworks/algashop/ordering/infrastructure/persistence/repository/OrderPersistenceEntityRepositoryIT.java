package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.utility.TSIDGenerator;
import com.algaworks.algashop.ordering.domain.model.utility.UUIDGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
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

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository repository) {
        this.repository = repository;
    }

    @Test
    public void shouldPersistOrder() {
        long orderId = TSIDGenerator.generateTSID().toLong();
        OrderPersistenceEntity order = OrderPersistenceEntity.builder()
                .id(orderId)
                .customerId(UUIDGenerator.generateTimeBasedUUID())
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .build();

        repository.saveAndFlush(order);
        Assertions.assertThat(repository.existsById(orderId)).isTrue();
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
                .customerId(UUIDGenerator.generateTimeBasedUUID())
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