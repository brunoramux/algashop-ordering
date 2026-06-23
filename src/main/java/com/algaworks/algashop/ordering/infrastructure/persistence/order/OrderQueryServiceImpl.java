package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.checkout.BillingData;
import com.algaworks.algashop.ordering.application.checkout.RecipientData;
import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.order.query.*;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.order.exception.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.OrderId;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;
    private final EntityManager entityManager;

    // ============================================================
    // SQL NATIVA - Constantes
    // ============================================================

    private static final String SELECT_ORDER_WITH_CUSTOMER = """
            SELECT o.id,
                   o.total_items,
                   o.total_amount,
                   o.placed_at,
                   o.paid_at,
                   o.canceled_at,
                   o.ready_at,
                   o.status,
                   o.payment_method,
                   c.id                             AS customer_id,
                   c.first_name,
                   c.last_name,
                   c.email,
                   c.document,
                   c.phone,
                   o.shipping_cost,
                   o.shipping_expected_date,
                   o.shipping_recipient_first_name,
                   o.shipping_recipient_last_name,
                   o.shipping_recipient_document,
                   o.shipping_recipient_phone,
                   o.shipping_address_street,
                   o.shipping_address_number,
                   o.shipping_address_complement,
                   o.shipping_address_neighborhood,
                   o.shipping_address_city,
                   o.shipping_address_state,
                   o.shipping_address_zip_code,
                   o.billing_first_name,
                   o.billing_last_name,
                   o.billing_document,
                   o.billing_phone,
                   o.billing_email,
                   o.billing_address_street,
                   o.billing_address_number,
                   o.billing_address_complement,
                   o.billing_address_neighborhood,
                   o.billing_address_city,
                   o.billing_address_state,
                   o.billing_address_zip_code
            FROM "order" o
            JOIN customer c ON c.id = o.customer_id
            """;

    private static final String SELECT_ORDER_SUMMARY_WITH_CUSTOMER = """
            SELECT o.id,
                   o.total_items,
                   o.total_amount,
                   o.placed_at,
                   o.paid_at,
                   o.canceled_at,
                   o.ready_at,
                   o.status,
                   o.payment_method,
                   c.id   AS customer_id,
                   c.first_name,
                   c.last_name,
                   c.email,
                   c.document,
                   c.phone
            FROM "order" o
            JOIN customer c ON c.id = o.customer_id
            """;

    private static final String SELECT_ORDER_ITEMS = """
            SELECT oi.id,
                   oi.product_id,
                   oi.product_name,
                   oi.price,
                   oi.quantity,
                   oi.total_amount,
                   oi.order_id
            FROM order_item oi
            WHERE oi.order_id = ?1
            """;

    private static final Map<OrderFilter.SortType, String> SORT_COLUMN_MAP = Map.of(
            OrderFilter.SortType.PLACED_AT, "o.placed_at",
            OrderFilter.SortType.PAID_AT, "o.paid_at",
            OrderFilter.SortType.CANCELED_AT, "o.canceled_at",
            OrderFilter.SortType.READY_AT, "o.ready_at",
            OrderFilter.SortType.STATUS, "LOWER(o.status)"
    );

    // ============================================================
    // Criteria API / JPQL - implementações originais
    // ============================================================

    @Override
    public OrderDetailOutput findById(String id) {
        OrderPersistenceEntity orderPersistenceEntity = repository.findById(new OrderId(id).value().toLong())
                .orElseThrow(OrderNotFoundException::new);
        return mapper.convert(orderPersistenceEntity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);

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
        if (sortOrder != null) {
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

        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return builder.asc(sortExpression);
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return builder.desc(sortExpression);
        }

        return null;
    }

    private Expression<?> toSortExpression(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        String propertyName = filter.getSortByPropertyOrDefault().getPropertyName();

        if (filter.getSortByPropertyOrDefault() == OrderFilter.SortType.STATUS) {
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

        if (filter.getCustomerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filter.getCustomerId()));
        }

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus().toUpperCase()));
        }

        if (filter.getOrderId() != null) {
            long orderIdLongValue;
            try {
                OrderId orderId = new OrderId(filter.getOrderId());
                orderIdLongValue = orderId.value().toLong();
            } catch (IllegalArgumentException e) {
                orderIdLongValue = 0L;
            }
            predicates.add(criteriaBuilder.equal(root.get("id"), orderIdLongValue));
        }

        if (filter.getPlacedAtFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtFrom()));
        }

        if (filter.getPlacedAtTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtTo()));
        }

        if (filter.getTotalAmountFrom() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountFrom()));
        }

        if (filter.getTotalAmountTo() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountTo()));
        }

        return predicates.toArray(new Predicate[]{});
    }

    // ============================================================
    // SQL NATIVA - implementações equivalentes
    // ============================================================

    @Override
    public OrderDetailOutput findByIdNative(String id) {
        long orderIdLong = new OrderId(id).value().toLong();

        String sql = SELECT_ORDER_WITH_CUSTOMER + "WHERE o.id = ?1";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, orderIdLong);

        Object[] row;
        try {
            row = (Object[]) query.getSingleResult();
        } catch (NoResultException e) {
            throw new OrderNotFoundException();
        }

        List<OrderItemDetailOutput> items = fetchItemsNative(orderIdLong);

        return mapRowToOrderDetailOutput(row, items);
    }

    @Override
    public Page<OrderSummaryOutput> filterNative(OrderFilter filter) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildOrderWhereClause(filter, params);

        long totalElements = countOrdersNative(whereClause, params);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        if (totalElements == 0L) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0L);
        }

        String sortColumn = SORT_COLUMN_MAP.getOrDefault(
                filter.getSortByPropertyOrDefault(), "o.placed_at");
        String sortDirection = filter.getSortDirectionOrDefault() == Sort.Direction.DESC ? "DESC" : "ASC";

        int offset = filter.getSize() * filter.getPage();

        String dataSql = SELECT_ORDER_SUMMARY_WITH_CUSTOMER
                + whereClause
                + " ORDER BY " + sortColumn + " " + sortDirection
                + " LIMIT " + filter.getSize()
                + " OFFSET " + offset;

        Query dataQuery = entityManager.createNativeQuery(dataSql);
        applyPositionalParams(dataQuery, params);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<OrderSummaryOutput> content = rows.stream()
                .map(this::mapRowToOrderSummaryOutput)
                .toList();

        return new PageImpl<>(content, pageRequest, totalElements);
    }

    private long countOrdersNative(String whereClause, List<Object> params) {
        String countSql = "SELECT COUNT(o.id) FROM \"order\" o JOIN customer c ON c.id = o.customer_id "
                + whereClause;
        Query countQuery = entityManager.createNativeQuery(countSql);
        applyPositionalParams(countQuery, params);
        return ((Number) countQuery.getSingleResult()).longValue();
    }

    private String buildOrderWhereClause(OrderFilter filter, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");

        if (filter.getCustomerId() != null) {
            where.append(" AND o.customer_id = ?");
            params.add(filter.getCustomerId());
        }

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            where.append(" AND o.status = ?");
            params.add(filter.getStatus().toUpperCase());
        }

        if (filter.getOrderId() != null) {
            long orderIdLongValue;
            try {
                orderIdLongValue = new OrderId(filter.getOrderId()).value().toLong();
            } catch (IllegalArgumentException e) {
                orderIdLongValue = 0L;
            }
            where.append(" AND o.id = ?");
            params.add(orderIdLongValue);
        }

        if (filter.getPlacedAtFrom() != null) {
            where.append(" AND o.placed_at >= ?");
            params.add(filter.getPlacedAtFrom());
        }

        if (filter.getPlacedAtTo() != null) {
            where.append(" AND o.placed_at <= ?");
            params.add(filter.getPlacedAtTo());
        }

        if (filter.getTotalAmountFrom() != null) {
            where.append(" AND o.total_amount >= ?");
            params.add(filter.getTotalAmountFrom());
        }

        if (filter.getTotalAmountTo() != null) {
            where.append(" AND o.total_amount <= ?");
            params.add(filter.getTotalAmountTo());
        }

        return where.toString();
    }

    private List<OrderItemDetailOutput> fetchItemsNative(long orderId) {
        Query itemQuery = entityManager.createNativeQuery(SELECT_ORDER_ITEMS);
        itemQuery.setParameter(1, orderId);

        @SuppressWarnings("unchecked")
        List<Object[]> itemRows = itemQuery.getResultList();
        return itemRows.stream().map(this::mapRowToOrderItemDetailOutput).toList();
    }

    private void applyPositionalParams(Query query, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
    }

    private OrderDetailOutput mapRowToOrderDetailOutput(Object[] row, List<OrderItemDetailOutput> items) {
        Long orderId = ((Number) row[0]).longValue();

        CustomerMinimalOutput customer = CustomerMinimalOutput.builder()
                .id((UUID) row[9])
                .firstName((String) row[10])
                .lastName((String) row[11])
                .email((String) row[12])
                .document((String) row[13])
                .phone((String) row[14])
                .build();

        RecipientData recipient = RecipientData.builder()
                .firstName((String) row[17])
                .lastName((String) row[18])
                .document((String) row[19])
                .phone((String) row[20])
                .build();

        AddressData shippingAddress = AddressData.builder()
                .street((String) row[21])
                .number((String) row[22])
                .complement((String) row[23])
                .neighborhood((String) row[24])
                .city((String) row[25])
                .state((String) row[26])
                .zipCode((String) row[27])
                .build();

        ShippingData shipping = ShippingData.builder()
                .cost((BigDecimal) row[15])
                .expectedDate((LocalDate) row[16])
                .recipient(recipient)
                .address(shippingAddress)
                .build();

        AddressData billingAddress = AddressData.builder()
                .street((String) row[33])
                .number((String) row[34])
                .complement((String) row[35])
                .neighborhood((String) row[36])
                .city((String) row[37])
                .state((String) row[38])
                .zipCode((String) row[39])
                .build();

        BillingData billing = BillingData.builder()
                .firstName((String) row[28])
                .lastName((String) row[29])
                .document((String) row[30])
                .phone((String) row[31])
                .email((String) row[32])
                .address(billingAddress)
                .build();

        return OrderDetailOutput.builder()
                .id(new OrderId(orderId).toString())
                .totalItems((Integer) row[1])
                .totalAmount((BigDecimal) row[2])
                .placedAt((OffsetDateTime) row[3])
                .paidAt((OffsetDateTime) row[4])
                .canceledAt((OffsetDateTime) row[5])
                .readyAt((OffsetDateTime) row[6])
                .status((String) row[7])
                .paymentMethod((String) row[8])
                .customer(customer)
                .shipping(shipping)
                .billing(billing)
                .items(items)
                .build();
    }

    private OrderSummaryOutput mapRowToOrderSummaryOutput(Object[] row) {
        Long orderId = ((Number) row[0]).longValue();

        CustomerMinimalOutput customer = CustomerMinimalOutput.builder()
                .id((UUID) row[9])
                .firstName((String) row[10])
                .lastName((String) row[11])
                .email((String) row[12])
                .document((String) row[13])
                .phone((String) row[14])
                .build();

        return OrderSummaryOutput.builder()
                .id(new OrderId(orderId).toString())
                .totalItems((Integer) row[1])
                .totalAmount((BigDecimal) row[2])
                .placedAt((OffsetDateTime) row[3])
                .paidAt((OffsetDateTime) row[4])
                .canceledAt((OffsetDateTime) row[5])
                .readyAt((OffsetDateTime) row[6])
                .status((String) row[7])
                .paymentMethod((String) row[8])
                .customer(customer)
                .build();
    }

    private OrderItemDetailOutput mapRowToOrderItemDetailOutput(Object[] row) {
        Long itemId = ((Number) row[0]).longValue();
        Long orderId = ((Number) row[6]).longValue();

        return OrderItemDetailOutput.builder()
                .id(new TSID(itemId).toString())
                .productId((UUID) row[1])
                .productName((String) row[2])
                .price((BigDecimal) row[3])
                .quantity((Integer) row[4])
                .totalAmount((BigDecimal) row[5])
                .orderId(new OrderId(orderId).toString())
                .build();
    }
}
