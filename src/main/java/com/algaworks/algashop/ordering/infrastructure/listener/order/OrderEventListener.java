package com.algaworks.algashop.ordering.infrastructure.listener.order;

import com.algaworks.algashop.ordering.application.order.OrderNotificationService;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderNotificationService orderNotificationService;

    @EventListener
    public void listen(OrderPlacedEvent event){
        log.info("Order {} Placed. Notify email: {}", event.orderId(), event.email());
        orderNotificationService.notifyOrderPlaced(event.orderId().value());
    }
}
