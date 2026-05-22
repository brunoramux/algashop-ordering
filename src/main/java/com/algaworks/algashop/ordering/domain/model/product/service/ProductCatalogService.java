package com.algaworks.algashop.ordering.domain.model.product.service;

import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;

import java.util.Optional;

// SERÁ IMPLEMENTADO COMO CONSULTA A OUTRO MICROSSERVIÇO (CATÁLOGO), POR ISSO NO DOMÍNIO DE ORDERING É APENAS UMA INTERFACE
public interface ProductCatalogService {
    Optional<Product> ofId(ProductId productId);
}
