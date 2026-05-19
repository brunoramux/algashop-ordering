package com.algaworks.algashop.ordering.domain.model.product;

import java.util.Optional;

// SERÁ IMPLEMENTADO COMO CONSULTA A OUTRO MICROSSERVIÇO (CATÁLOGO), POR ISSO NO DOMÍNIO DE ORDERING É APENAS UMA INTERFACE
public interface ProductCatalogService {
    Optional<Product> ofId(ProductId productId);
}
