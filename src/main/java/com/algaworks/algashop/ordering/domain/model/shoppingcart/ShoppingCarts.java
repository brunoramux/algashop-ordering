package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.RemoveCapableRepository;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.util.Optional;

// SHOPPING CART PODE EXCLUIR OBJETOS, PORTANTO EXTENDE DE REMOVE CAPABLE REPOSITORY
// REMOVE CAPABLE REPOSITORY EXTENDE DE REPOSITORY BASE
public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
    Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}
