package com.algaworks.algashop.ordering.domain.model.shoppingcart.repository;

import com.algaworks.algashop.ordering.domain.model.RemoveCapableRepository;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject.ShoppingCartId;

import java.util.Optional;

// SHOPPING CART PODE EXCLUIR OBJETOS, PORTANTO EXTENDE DE REMOVE CAPABLE REPOSITORY
// REMOVE CAPABLE REPOSITORY EXTENDE DE REPOSITORY BASE
public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
    Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}
