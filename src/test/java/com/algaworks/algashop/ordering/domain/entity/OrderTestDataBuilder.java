package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

import java.time.LocalDate;

public class OrderTestDataBuilder {

    private CustomerId customerId = new CustomerId();
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
    private Money shippingCost = new Money("10.00");
    private LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(2);

    private ShippingInfo shippingInfo = aValidShippingInfo();
    private BillingInfo billingInfo = aValidBillingInfo();

    private boolean withItems;

    private OrderStatus orderStatus;


    private OrderTestDataBuilder() {}

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build(){
        Order order = Order.draft(customerId);
        order.changeShippingInfo(shippingInfo, shippingCost, expectedDeliveryDate);
        order.changeBillingInfo(billingInfo);

        order.changePaymentMethod(paymentMethod);

        if(!withItems) {
            order.addItem(new ProductId(), new ProductName("IPhone"),
                    new Money("3000.00"), new Quantity(2));
            order.addItem(new ProductId(), new ProductName("Mac Book"),
                    new Money("10000.00"), new Quantity(1));
        }

        switch(this.orderStatus) {
            case DRAFT -> {}
            case PLACED -> {
                order.place();
            }
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {}
            case CANCELLED -> {}
        }

        return order;

    }

    public BillingInfo aValidBillingInfo() {
        return BillingInfo.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-123-123"))
                .fullName(new FullName("Bruno", "Ramos Lemos"))
                .build();
    }

    public ShippingInfo aValidShippingInfo() {
        return ShippingInfo.builder()
                .address(anAddress())
                .fullName(new FullName("John", "Doe"))
                .document(new Document("225-09-1992"))
                .phone(new Phone("111-441-1244"))
                .build();
    }

    public static Address anAddress(){
        return Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt. 11")
                .city("Brasilia")
                .state("South Carolina")
                .zipCode(new ZipCode("70763-540")).build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder shippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
        return this;
    }

    public OrderTestDataBuilder expectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        return this;
    }

    public OrderTestDataBuilder shippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
        return this;
    }

    public OrderTestDataBuilder billingInfo(BillingInfo billingInfor) {
        this.billingInfo = billingInfor;
        return this;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder orderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

}
