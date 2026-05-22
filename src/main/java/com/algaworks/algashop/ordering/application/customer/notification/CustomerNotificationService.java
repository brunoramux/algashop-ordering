package com.algaworks.algashop.ordering.application.customer.notification;

import java.util.UUID;

public interface CustomerNotificationService {
    void notifyNewRegistration(NotifyNewRegistrationInput input);

    public record NotifyNewRegistrationInput(UUID customerId, String firstName, String email){}
}
