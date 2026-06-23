package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.customer.CustomerOutput;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomerPersistenceEntityQueriesImpl implements CustomerPersistenceEntityQueries {

    private final EntityManager entityManager;

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
            WHERE c.id = :id
    """;

    private static final String findByIdAsOutputNativeSQL = """
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
            WHERE c.id = :id
            """;

    @Override
    public Optional<CustomerOutput> findByIdAsOutput(UUID customerId) {
        try {
            TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CustomerOutput> findByIdAsOutputNative(UUID customerId) {
        try {
            Query query = entityManager.createNativeQuery(findByIdAsOutputNativeSQL);
            query.setParameter("id", customerId);
            Object[] row = (Object[]) query.getSingleResult();
            return Optional.of(mapRowToCustomerOutput(row));
        } catch (NoResultException e) {
            return Optional.empty();
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
}
