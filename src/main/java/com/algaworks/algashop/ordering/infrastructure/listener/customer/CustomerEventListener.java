package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.application.customer.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationService;
import com.algaworks.algashop.ordering.domain.model.customer.event.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.event.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.order.event.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerNotificationService customerNotificationService;
    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @EventListener
    public void listen(CustomerRegisteredEvent event){
        log.info("CustomerRegisteredEvent received");
        customerNotificationService.notifyNewRegistration(
                new CustomerNotificationService.NotifyNewRegistrationInput(
                        event.customerId().value(), event.fullName().firstName(), event.email().value()
                ));
    }

    @EventListener
    public void listen(CustomerArchivedEvent event){
        log.info("CustomerArchivedEvent received");
    }

    @EventListener
    public void listen(OrderReadyEvent event){
        customerLoyaltyPointsApplicationService.addLoyaltyPoints(event.customerId().value(),
                event.orderId().toString());
        log.info("OrderReadyEvent received. Points added to Loyalty Points");
    }

}
