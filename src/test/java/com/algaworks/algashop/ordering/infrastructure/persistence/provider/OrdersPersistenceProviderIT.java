package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ OrderPersistenceEntityDisassembler.class,
        OrderPersistenceEntityAssembler.class,
        OrdersPersistenceProvider.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
})
class OrdersPersistenceProviderIT {

    private final OrdersPersistenceProvider persistenceProvider;
    private final CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider, CustomersPersistenceProvider customersPersistenceProvider) {
        this.persistenceProvider = persistenceProvider;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @Test
    void shouldAddFindAndNotFailWhenNoTransaction(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customersPersistenceProvider.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .build();
        persistenceProvider.add(order);

        Assertions.assertThatNoException().isThrownBy(
                ()-> persistenceProvider.ofId(order.id()).orElseThrow()
        );
    }

}