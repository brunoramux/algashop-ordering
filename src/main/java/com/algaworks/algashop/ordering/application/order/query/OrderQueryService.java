package com.algaworks.algashop.ordering.application.order.query;

import org.springframework.data.domain.Page;

public interface OrderQueryService {
    OrderDetailOutput findById(String id);
    Page<OrderSummaryOutput> filter(OrderFilter filter);

    // Versões equivalentes utilizando SQL nativa
    OrderDetailOutput findByIdNative(String id);
    Page<OrderSummaryOutput> filterNative(OrderFilter filter);
}
