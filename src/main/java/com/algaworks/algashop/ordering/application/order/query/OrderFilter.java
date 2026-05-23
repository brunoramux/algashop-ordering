package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    private String status;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime placedAtFrom;
    private OffsetDateTime placedAtTo;
    private BigDecimal totalAmountFrom;
    private BigDecimal totalAmountTo;

    @Getter
    @RequiredArgsConstructor
    public enum SortType {
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        CANCELED_AT("canceledAt"),
        READY_AT("readyAt"),
        STATUS("status");

        private final String propertyName;
    }

    public OrderFilter(int size, int page) {
        super(size, page);
    }

    @Override
    public OrderFilter.SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.PLACED_AT : getSortByProperty();
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }
}
