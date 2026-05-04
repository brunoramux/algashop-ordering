package com.algaworks.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class OrderStatusTest {

    @Test
    void shouldBeAbleToChangeOrderStatus(){
        OrderStatus orderStatus = OrderStatus.DRAFT;

        boolean canChangeIndicator = orderStatus.canChangeTo(OrderStatus.PLACED);

        Assertions.assertThat(canChangeIndicator).isTrue();
    }

    @Test
    void shouldNotBeAbleToChangeOrderStatus(){
        OrderStatus orderStatus = OrderStatus.PLACED;
        boolean canChangeIndicator = orderStatus.canChangeTo(OrderStatus.DRAFT);

        Assertions.assertThat(canChangeIndicator).isFalse();
    }

}