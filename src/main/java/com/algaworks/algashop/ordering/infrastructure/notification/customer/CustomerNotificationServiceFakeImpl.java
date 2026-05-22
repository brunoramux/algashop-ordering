package com.algaworks.algashop.ordering.infrastructure.notification.customer;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationService {

    @Override
    public void notifyNewRegistration(NotifyNewRegistrationInput input) {
        log.info("Notificando cliente {} sobre o cadastro com o email {}", input.firstName(), input.email());
    }

}
