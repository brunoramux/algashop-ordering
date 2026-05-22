package com.algaworks.algashop.ordering.domain.model.product.exception;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessages;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;

public class ProductOutOfStockException extends DomainException {


    public ProductOutOfStockException(ProductId id) {
        super(String.format(ErrorMessages.ERROR_PRODUCT_OUT_OF_STOCK, id));
    }


}
