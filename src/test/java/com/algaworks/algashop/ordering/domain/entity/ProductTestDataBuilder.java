package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

public class ProductTestDataBuilder {

    private ProductTestDataBuilder() {
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .id(new ProductId())
                .inStock(true)
                .name(new ProductName("IPhone"))
                .price(new Money("3000.00"));
    }

    public static Product.ProductBuilder aProductMacBook() {
        return Product.builder()
                .id(new ProductId())
                .inStock(true)
                .name(new ProductName("MacBook"))
                .price(new Money("10000.00"));
    }

    public static Product.ProductBuilder aProductUnavailable() {
        return Product.builder()
                .id(new ProductId())
                .inStock(false)
                .name(new ProductName("Desktop I3"))
                .price(new Money("2000.00"));
    }

}
