package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.customer.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerNotFoundException;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final EntityManager entityManager;

    // ============================================================
    // SQL NATIVA - Constantes
    // ============================================================

    private static final String SELECT_CUSTOMER_DETAIL_COLUMNS = """
            SELECT c.id,
                   c.first_name,
                   c.last_name,
                   c.email,
                   c.phone,
                   c.document,
                   c.birth_date,
                   c.promotion_notifications_allowed,
                   c.loyalty_points,
                   c.address_street,
                   c.address_number,
                   c.address_complement,
                   c.address_neighborhood,
                   c.address_city,
                   c.address_state,
                   c.address_zip_code,
                   c.registered_at,
                   c.archived_at,
                   c.archived
            FROM customer c
            """;

    private static final String SELECT_CUSTOMER_SUMMARY_COLUMNS = """
            SELECT c.id,
                   c.first_name,
                   c.last_name,
                   c.email,
                   c.document,
                   c.phone,
                   c.birth_date,
                   c.loyalty_points,
                   c.registered_at,
                   c.archived_at,
                   c.promotion_notifications_allowed,
                   c.archived
            FROM customer c
            """;

    private static final Map<CustomerFilter.SortType, String> SORT_COLUMN_MAP = Map.of(
            CustomerFilter.SortType.REGISTERED_AT, "c.registered_at",
            CustomerFilter.SortType.FIRST_NAME, "c.first_name"
    );

    // ============================================================
    // JPQL - implementações originais
    // ============================================================

    private static final String findByIdAsOutputJPQL = """
            SELECT new com.algaworks.algashop.ordering.application.customer.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.phone,
                c.document,
                c.birthDate,
                c.promotionNotificationsAllowed,
                c.loyaltyPoints,
                new com.algaworks.algashop.ordering.application.commons.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.complement,
                    c.address.neighborhood,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode
                ),
                c.registeredAt,
                c.archivedAt,
                c.archived
            )
            FROM CustomerPersistenceEntity c
            WHERE c.id = :id""";

    @Override
    public CustomerOutput findById(UUID customerId) {
        try {
            TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filter(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(CustomerFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);

        Expression<Long> count = builder.count(root);
        Predicate[] predicates = toPredicates(builder, root, filter);

        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        return query.getSingleResult();
    }

    private Page<CustomerSummaryOutput> filter(CustomerFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerSummaryOutput> criteriaQuery = builder.createQuery(CustomerSummaryOutput.class);

        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);

        criteriaQuery.select(
                builder.construct(CustomerSummaryOutput.class,
                        root.get("id"),
                        root.get("firstName"),
                        root.get("lastName"),
                        root.get("email"),
                        root.get("document"),
                        root.get("phone"),
                        root.get("birthDate"),
                        root.get("loyaltyPoints"),
                        root.get("registeredAt"),
                        root.get("archivedAt"),
                        root.get("promotionNotificationsAllowed"),
                        root.get("archived")
                )
        );

        Predicate[] predicates = toPredicates(builder, root, filter);
        Order sortOrder = toSortOrder(builder, root, filter);

        criteriaQuery.where(predicates);
        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<CustomerSummaryOutput> typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setFirstResult(filter.getSize() * filter.getPage());
        typedQuery.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(CriteriaBuilder builder, Root<CustomerPersistenceEntity> root, CustomerFilter filter) {
        String propertyName = filter.getSortByPropertyOrDefault().getPropertyName();

        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return builder.asc(root.get(propertyName));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return builder.desc(root.get(propertyName));
        }

        return null;
    }

    private Predicate[] toPredicates(CriteriaBuilder builder,
                                     Root<CustomerPersistenceEntity> root, CustomerFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            predicates.add(builder.like(builder.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
        }

        return predicates.toArray(new Predicate[]{});
    }

    // ============================================================
    // SQL NATIVA - implementações equivalentes
    // ============================================================

    @Override
    public CustomerOutput findByIdNative(UUID customerId) {
        String sql = SELECT_CUSTOMER_DETAIL_COLUMNS + "WHERE c.id = ?1";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, customerId);
            Object[] row = (Object[]) query.getSingleResult();
            return mapRowToCustomerOutput(row);
        } catch (NoResultException e) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public Page<CustomerSummaryOutput> filterNative(CustomerFilter filter) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildCustomerWhereClause(filter, params);

        long totalElements = countTotalQueryResultsNative(whereClause, params);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        if (totalElements == 0L) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0L);
        }

        String sortColumn = SORT_COLUMN_MAP.getOrDefault(
                filter.getSortByPropertyOrDefault(), "c.registered_at");
        String sortDirection = filter.getSortDirectionOrDefault() == Sort.Direction.DESC ? "DESC" : "ASC";

        int offset = filter.getSize() * filter.getPage();

        String dataSql = SELECT_CUSTOMER_SUMMARY_COLUMNS
                + whereClause
                + " ORDER BY " + sortColumn + " " + sortDirection
                + " LIMIT " + filter.getSize()
                + " OFFSET " + offset;

        Query dataQuery = entityManager.createNativeQuery(dataSql);
        applyPositionalParams(dataQuery, params);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();
        List<CustomerSummaryOutput> content = rows.stream()
                .map(this::mapRowToCustomerSummaryOutput)
                .toList();

        return new PageImpl<>(content, pageRequest, totalElements);
    }

    private long countTotalQueryResultsNative(String whereClause, List<Object> params) {
        String countSql = "SELECT COUNT(c.id) FROM customer c " + whereClause;
        Query countQuery = entityManager.createNativeQuery(countSql);
        applyPositionalParams(countQuery, params);
        return ((Number) countQuery.getSingleResult()).longValue();
    }

    private String buildCustomerWhereClause(CustomerFilter filter, List<Object> params) {
        StringBuilder where = new StringBuilder("WHERE 1=1");

        if (filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            where.append(" AND LOWER(c.first_name) LIKE ?");
            params.add("%" + filter.getFirstName().toLowerCase() + "%");
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            where.append(" AND LOWER(c.email) LIKE ?");
            params.add("%" + filter.getEmail().toLowerCase() + "%");
        }

        return where.toString();
    }

    private void applyPositionalParams(Query query, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
    }

    private CustomerOutput mapRowToCustomerOutput(Object[] row) {
        return CustomerOutput.builder()
                .id((UUID) row[0])
                .firstName((String) row[1])
                .lastName((String) row[2])
                .email((String) row[3])
                .phone((String) row[4])
                .document((String) row[5])
                .birthDate((LocalDate) row[6])
                .promotionNotificationsAllowed((Boolean) row[7])
                .loyaltyPoints((Integer) row[8])
                .address(AddressData.builder()
                        .street((String) row[9])
                        .number((String) row[10])
                        .complement((String) row[11])
                        .neighborhood((String) row[12])
                        .city((String) row[13])
                        .state((String) row[14])
                        .zipCode((String) row[15])
                        .build())
                .registeredAt((OffsetDateTime) row[16])
                .archivedAt((OffsetDateTime) row[17])
                .archived((Boolean) row[18])
                .build();
    }

    private CustomerSummaryOutput mapRowToCustomerSummaryOutput(Object[] row) {
        return CustomerSummaryOutput.builder()
                .id((UUID) row[0])
                .firstName((String) row[1])
                .lastName((String) row[2])
                .email((String) row[3])
                .document((String) row[4])
                .phone((String) row[5])
                .birthDate((LocalDate) row[6])
                .loyaltyPoints((Integer) row[7])
                .registeredAt((OffsetDateTime) row[8])
                .archivedAt((OffsetDateTime) row[9])
                .promotionNotificationsAllowed((Boolean) row[10])
                .archived((Boolean) row[11])
                .build();
    }
}
