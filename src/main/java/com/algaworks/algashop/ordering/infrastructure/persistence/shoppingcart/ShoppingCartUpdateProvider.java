package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.service.ShoppingCartProductAdjustmentService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShoppingCartUpdateProvider implements ShoppingCartProductAdjustmentService {

    private final ShoppingCartPersistenceEntityRepository repository;

    @Override
    @Transactional
    public void adjustPrice(ProductId productId, Money updatedPrice) {
        repository.updateItemPrice(productId.value(), updatedPrice.value());
        repository.recalculateTotalForCartsWithProduct(productId.value());
    }

    @Override
    @Transactional
    public void changeAvailability(ProductId productId, boolean available) {
        repository.updateItemAvailability(productId.value(), available);
        repository.recalculateTotalForCartsWithProduct(productId.value());
    }


}
