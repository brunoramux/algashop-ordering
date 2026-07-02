package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.infrastructure.config.security.SecurityAnnotations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

	private final ShoppingCartManagementApplicationService managementService;
	private final ShoppingCartQueryService queryService;

	@SecurityAnnotations.CanWriteShoppingCarts
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
		UUID shoppingCartId = managementService.createNew(input.getCustomerId());
		return queryService.findById(shoppingCartId);
	}

	@SecurityAnnotations.CanReadShoppingCarts
	@GetMapping("/{shoppingCartId}")
	public ShoppingCartOutput getById(@PathVariable UUID shoppingCartId) {
		return queryService.findById(shoppingCartId);
	}

	@SecurityAnnotations.CanReadShoppingCarts
	@GetMapping("/{shoppingCartId}/items")
	public ShoppingCartItemListModel getItems(@PathVariable UUID shoppingCartId) {
		var items = queryService.findById(shoppingCartId).getItems();
		return new ShoppingCartItemListModel(items);
	}

	@SecurityAnnotations.CanWriteShoppingCarts
	@DeleteMapping("/{shoppingCartId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID shoppingCartId) {
		managementService.delete(shoppingCartId);
	}

	@SecurityAnnotations.CanWriteShoppingCarts
	@DeleteMapping("/{shoppingCartId}/items")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void empty(@PathVariable UUID shoppingCartId) {
		managementService.empty(shoppingCartId);
	}

	@SecurityAnnotations.CanWriteShoppingCarts
	@PostMapping("/{shoppingCartId}/items")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addItem(@PathVariable UUID shoppingCartId,
		   			    @RequestBody @Valid ShoppingCartItemInput input) {
		input.setShoppingCartId(shoppingCartId);
		managementService.addItem(input);
	}

	@SecurityAnnotations.CanWriteShoppingCarts
	@DeleteMapping("/{shoppingCartId}/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeItem(@PathVariable UUID shoppingCartId,
						   @PathVariable UUID itemId) {
		managementService.removeItem(shoppingCartId, itemId);
	}
}