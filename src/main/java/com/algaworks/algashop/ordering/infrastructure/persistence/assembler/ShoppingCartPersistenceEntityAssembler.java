package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity persistenceEntity, ShoppingCart shoppingCart) {
        persistenceEntity.setId(shoppingCart.id().value().toLong());
        persistenceEntity.setVersion(shoppingCart.version());
        persistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        persistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        persistenceEntity.setCreatedAt(shoppingCart.createdAt());

        Set<ShoppingCartItemPersistenceEntity> mergedItems = mergeItems(shoppingCart, persistenceEntity);
        persistenceEntity.replaceItems(mergedItems);

        CustomerPersistenceEntity customerPersistenceEntity = customerRepository.getReferenceById(shoppingCart.customerId().value());
        persistenceEntity.setCustomer(customerPersistenceEntity);

        return persistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(
            ShoppingCart shoppingCart,
            ShoppingCartPersistenceEntity persistenceEntity
    ) {
        // retorna itens na Entidade de Dominio
        Set<ShoppingCartItem> newOrUpdatedItems = shoppingCart.items();

        // Checa se itens está vazio. Nesse caso, retorna SetList vazio para a entidade de persistência
        if (newOrUpdatedItems == null || newOrUpdatedItems.isEmpty()) {
            return new HashSet<>();
        }

        // retorna itens na entidade de persistência
        Set<ShoppingCartItemPersistenceEntity> existingItems = persistenceEntity.getItems();

        // Checa se itens está vazio. Nesse caso apenas retorna SetList com itens da Entidade de Dominio
        if (existingItems == null || existingItems.isEmpty()) {
            return newOrUpdatedItems.stream().map(this::fromDomain).collect(Collectors.toSet());
        }

        // cria HashMap com Hash<ShoppingCartItemId, ShoppingCart> com itens que já estão na Entidade de Persistência
        Map<Long, ShoppingCartItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemPersistenceEntity::getId, item -> item));

        // Percorre itens da entidade de Dominio, verificando quais existem na Entidade de Persistência
        return newOrUpdatedItems.stream()
                .map(shoppingCartItem -> {
                    // Verifica se item já existe shoppingCartItem (Dominio) em existingItemMap (Persistencia)
                    // Se existir, retorna o item, caso contrário cria novo item
                    ShoppingCartItemPersistenceEntity itemPersistence = existingItemMap.getOrDefault(
                            shoppingCartItem.id().value().toLong(), new ShoppingCartItemPersistenceEntity()
                    );
                    // Faz o merge das entidades
                    // apenas itens que estão no domínio ficarão na persistência. Os demais serão excluidos
                    return merge(itemPersistence, shoppingCartItem);
                }).collect(Collectors.toSet());
    }

    public ShoppingCartItemPersistenceEntity fromDomain(ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    private ShoppingCartItemPersistenceEntity merge(
            ShoppingCartItemPersistenceEntity persistenceEntity,
            ShoppingCartItem shoppingCartItem
    ) {
        persistenceEntity.setId(shoppingCartItem.id().value().toLong());
        persistenceEntity.setProductId(shoppingCartItem.productId().value());
        persistenceEntity.setProductName(shoppingCartItem.name().value());
        persistenceEntity.setPrice(shoppingCartItem.price().value());
        persistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        persistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        persistenceEntity.setAvailable(shoppingCartItem.isAvailable());

        return persistenceEntity;
    }
}
