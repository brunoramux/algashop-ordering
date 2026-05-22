package com.algaworks.algashop.ordering.application.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.service.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.exception.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.exception.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.service.ShoppingService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject.ShoppingCartItemId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService {

	private final ShoppingCarts shoppingCarts;
	private final ProductCatalogService productCatalogService;
	private final ShoppingService shoppingService;

	@Transactional
	public void addItem(ShoppingCartItemInput input) {
		Objects.requireNonNull(input);
		ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
		ProductId productId = new ProductId(input.getProductId());

		ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
				.orElseThrow(ShoppingCartNotFoundException::new);

		Product product = productCatalogService.ofId(productId)
				.orElseThrow(ProductNotFoundException::new);

		shoppingCart.addItem(product, new Quantity(input.getQuantity()));

		shoppingCarts.add(shoppingCart);
	}

	@Transactional
	public UUID createNew(UUID rawCustomerId) {
		Objects.requireNonNull(rawCustomerId);
		ShoppingCart shoppingCart = shoppingService.startShopping(new CustomerId(rawCustomerId));
		shoppingCarts.add(shoppingCart);
		return shoppingCart.id().value();
	}

	@Transactional
	public void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId) {
		Objects.requireNonNull(rawShoppingCartId);
		Objects.requireNonNull(rawShoppingCartItemId);
		ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
		ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
				.orElseThrow(ShoppingCartNotFoundException::new);
		shoppingCart.removeItem(new ShoppingCartItemId(rawShoppingCartItemId));
		shoppingCarts.add(shoppingCart);
	}

	@Transactional
	public void empty(UUID rawShoppingCartId) {
		Objects.requireNonNull(rawShoppingCartId);
		ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
		ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
				.orElseThrow(ShoppingCartNotFoundException::new);
		shoppingCart.empty();
		shoppingCarts.add(shoppingCart);
	}

	@Transactional
	public void delete(UUID rawShoppingCartId) {
		Objects.requireNonNull(rawShoppingCartId);
		ShoppingCartId shoppingCartId = new ShoppingCartId(rawShoppingCartId);
		ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
				.orElseThrow(ShoppingCartNotFoundException::new);
		shoppingCarts.remove(shoppingCart);
	}

}