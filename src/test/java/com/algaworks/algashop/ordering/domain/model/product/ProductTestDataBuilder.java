package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.UUIDGenerator;
import com.algaworks.algashop.ordering.domain.model.commons.Money;

public class ProductTestDataBuilder {
    public static ProductId DEFAULT_PRODUCT_ID = new ProductId(UUIDGenerator.generateTimeBasedUUID());

    private ProductTestDataBuilder() {
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .inStock(true)
                .name(new ProductName("IPhone"))
                .price(new Money("3000.00"));
    }

    public static Product.ProductBuilder aProductMacBook() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .inStock(true)
                .name(new ProductName("MacBook"))
                .price(new Money("10000.00"));
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(DEFAULT_PRODUCT_ID)
                .inStock(false)
                .name(new ProductName("Desktop I3"))
                .price(new Money("2000.00"));
    }

}
