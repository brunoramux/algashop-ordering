package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.order.service.CheckoutService;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.service.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.exception.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.exception.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.repository.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

	private final Orders orders;
	private final Customers customers;
	private final ShoppingCarts shoppingCarts;
	private final CheckoutService checkoutService;

	private final BillingInputDisassembler billingInputDisassembler;
	private final ShippingInputDisassembler shippingInputDisassembler;

	private final ShippingCostService shippingCostService;
	private final OriginAddressService originAddressService;
	private final ProductCatalogService productCatalogService;

	@Transactional
	public String checkout(CheckoutInput input) {
		Objects.requireNonNull(input);
		PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());


		ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
		ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
				.orElseThrow(ShoppingCartNotFoundException::new);

		Customer customer = customers.ofId(shoppingCart.customerId())
				.orElseThrow(CustomerNotFoundException::new);

		var shippingCalculationResult = calculateShippingCost(input.getShipping());

		Order order = checkoutService.checkout(shoppingCart,
				customer,
				billingInputDisassembler.toDomainModel(input.getBilling()),
				shippingInputDisassembler.toDomainModel(input.getShipping(), shippingCalculationResult),
				paymentMethod);

		orders.add(order);
		shoppingCarts.add(shoppingCart);

		return order.id().toString();
	}

	private ShippingCostService.CalculationResult calculateShippingCost(ShippingInput shipping) {
		ZipCode origin = originAddressService.originAddress().zipCode();
		ZipCode destination = new ZipCode(shipping.getAddress().getZipCode());
		return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
	}

	private Product findProduct(ProductId productId) {
		return productCatalogService.ofId(productId)
				.orElseThrow(ProductNotFoundException::new);
	}

}