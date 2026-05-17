package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import java.util.Optional;

// SHOPPING CART PODE EXCLUIR OBJETOS, PORTANTO EXTENDE DE REMOVE CAPABLE REPOSITORY
// REMOVE CAPABLE REPOSITORY EXTENDE DE REPOSITORY BASE
public interface ShoppingCarts extends RemoveCapableRepository<ShoppingCart, ShoppingCartId> {
    Optional<ShoppingCart> ofCustomer(CustomerId customerId);
}
