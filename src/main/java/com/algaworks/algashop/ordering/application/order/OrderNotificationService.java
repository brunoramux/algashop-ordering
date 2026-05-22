package com.algaworks.algashop.ordering.application.order;

import io.hypersistence.tsid.TSID;

public interface OrderNotificationService {

    void notifyOrderPlaced(TSID orderId);

}
