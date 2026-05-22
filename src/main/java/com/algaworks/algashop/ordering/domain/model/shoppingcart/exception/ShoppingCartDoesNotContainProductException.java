package com.algaworks.algashop.ordering.domain.model.shoppingcart.exception;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.valueobject.ShoppingCartId;

public class ShoppingCartDoesNotContainProductException extends DomainException {


    public ShoppingCartDoesNotContainProductException(ShoppingCartId id, ProductId productId) {
        super(String.format(ErrorMessages.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, id, productId));
    }
}
