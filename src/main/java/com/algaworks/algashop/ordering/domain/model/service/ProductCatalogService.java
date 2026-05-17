package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

// SERÁ IMPLEMENTADO COMO CONSULTA A OUTRO MICROSSERVIÇO (CATÁLOGO), POR ISSO NO DOMÍNIO DE ORDERING É APENAS UMA INTERFACE
public interface ProductCatalogService {
    Optional<Product> ofId(ProductId productId);
}
