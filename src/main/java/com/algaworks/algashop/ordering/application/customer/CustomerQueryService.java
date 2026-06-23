package com.algaworks.algashop.ordering.application.customer;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CustomerQueryService {
    CustomerOutput findById(UUID customerId);
    Page<CustomerSummaryOutput> filter(CustomerFilter filter);

    // Versões equivalentes utilizando SQL nativa
    CustomerOutput findByIdNative(UUID customerId);
    Page<CustomerSummaryOutput> filterNative(CustomerFilter filter);
}