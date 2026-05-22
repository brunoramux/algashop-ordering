package com.algaworks.algashop.ordering.application.customer;

import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.service.CustomerLoyaltyPointsService;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.exception.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {

	private final CustomerLoyaltyPointsService customerLoyaltyPointsService;
	private final Orders orders;
	private final Customers customers;

	@Transactional
	public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId) {
		CustomerId customerId = new CustomerId(rawCustomerId);
		OrderId orderId = new OrderId(rawOrderId);

		Order order = orders.ofId(orderId)
				.orElseThrow(OrderNotFoundException::new);
		Customer customer = customers.ofId(customerId)
				.orElseThrow(CustomerNotFoundException::new);
		
		customerLoyaltyPointsService.addPoints(customer, order);
		
		customers.add(customer);
	}

}