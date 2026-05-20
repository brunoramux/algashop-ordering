package com.algaworks.algashop.ordering.application.customer;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOutput {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String document;
    private LocalDate birthDate;
    private Boolean promotionNotificationsAllowed;
    private Integer loyaltyPoints;
    private AddressData address;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Boolean archived;

}
