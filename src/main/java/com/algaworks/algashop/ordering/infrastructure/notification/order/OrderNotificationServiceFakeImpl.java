package com.algaworks.algashop.ordering.infrastructure.notification.order;

import com.algaworks.algashop.ordering.application.order.OrderNotificationService;
import io.hypersistence.tsid.TSID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderNotificationServiceFakeImpl implements OrderNotificationService {
    @Override
    public void notifyOrderPlaced(TSID orderId) {
        log.info("Order placed: {}. Sending notification to Email", orderId);
    }
}
