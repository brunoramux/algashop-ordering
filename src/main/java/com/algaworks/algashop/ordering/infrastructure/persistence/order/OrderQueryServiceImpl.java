package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.*;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.order.exception.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;
    private final EntityManager entityManager;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderPersistenceEntity orderPersistenceEntity = repository.findById(new OrderId(id).value().toLong())
                .orElseThrow(OrderNotFoundException::new);
        return mapper.convert(orderPersistenceEntity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if(totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage() , filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        Root<OrderPersistenceEntity> root  =  criteriaQuery.from(OrderPersistenceEntity.class);

        Path<Object> customer = root.get("customer");

        CompoundSelection<OrderSummaryOutput> selectOrderSummary = builder.construct(OrderSummaryOutput.class,
                root.get("id"),
                root.get("totalItems"),
                root.get("totalAmount"),
                root.get("placedAt"),
                root.get("paidAt"),
                root.get("canceledAt"),
                root.get("readyAt"),
                root.get("status"),
                root.get("paymentMethod"),
                builder.construct(CustomerMinimalOutput.class,
                        customer.get("id"),
                        customer.get("firstName"),
                        customer.get("lastName"),
                        customer.get("email"),
                        customer.get("document"),
                        customer.get("phone")
                )
        );

        Predicate[] predicates = toPredicates(builder, root, filter);
        Order sortOrder = toSortOrder(builder, root, filter);

        criteriaQuery.select(selectOrderSummary);
        criteriaQuery.where(predicates);
        if(sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }


        TypedQuery<OrderSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getSize() * filter.getPage());
        query.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        Expression<?> sortExpression = toSortExpression(builder, root, filter);

        if(filter.getSortDirectionOrDefault() == Sort.Direction.ASC){
            return builder.asc(sortExpression);
        }

        // PROPERTYNAME RETORNA O NOME EXATO DA COLUNA A SER ORDENADA
        if(filter.getSortDirectionOrDefault() == Sort.Direction.DESC){
            return builder.desc(sortExpression);
        }

        return null;
    }

    private Expression<?> toSortExpression(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        String propertyName = filter.getSortByPropertyOrDefault().getPropertyName();

        if(filter.getSortByPropertyOrDefault() == OrderFilter.SortType.STATUS) {
            return builder.lower(root.get(propertyName));
        }

        return root.get(propertyName);
    }

    private Long countTotalQueryResults(OrderFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);

        Expression<Long> count = builder.count(root);
        Predicate[] predicates = toPredicates(builder, root, filter);

        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        return query.getSingleResult();
    }

    private Predicate[] toPredicates(CriteriaBuilder criteriaBuilder,
                                     Root<OrderPersistenceEntity> root,
                                     OrderFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCustomerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filter.getCustomerId()));
        }

        if(filter.getStatus() != null && !filter.getStatus().isBlank()) {
            predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus().toUpperCase()));
        }

        if(filter.getOrderId() != null) {
            long orderIdLongValue;
            try {
                OrderId orderId = new OrderId(filter.getOrderId());
                orderIdLongValue = orderId.value().toLong();
            } catch (IllegalArgumentException e) {
                orderIdLongValue = 0L;
            }
            predicates.add(criteriaBuilder.equal(root.get("id"), orderIdLongValue));
        }

        if(filter.getPlacedAtFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtFrom()));
        }

        if(filter.getPlacedAtTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtTo()));
        }

        if(filter.getTotalAmountFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountFrom()));
        }

        if(filter.getTotalAmountTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountTo()));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
