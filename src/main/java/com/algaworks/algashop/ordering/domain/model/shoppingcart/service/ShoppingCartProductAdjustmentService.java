package com.algaworks.algashop.ordering.domain.model.shoppingcart.service;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;

public interface ShoppingCartProductAdjustmentService {
    void adjustPrice(ProductId productId, Money updatedPrice);
    void changeAvailability(ProductId productId, boolean available);
}
