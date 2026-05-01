package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;

public class OrderTestDataBuilder {

    private CustomerId customerId = new CustomerId();
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
    private Money shippingCost = new Money("10.00");
    private LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(2);

    private Shipping shipping = aValidShipping();
    private Billing billing = aValidBilling();

    private boolean withItems;

    private OrderStatus orderStatus;


    private OrderTestDataBuilder() {}

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build(){
        Order order = Order.draft(customerId);
        order.changeShipping(shipping);
        order.changeBillingInfo(billing);

        order.changePaymentMethod(paymentMethod);

        if(!withItems) {
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
            order.addItem(ProductTestDataBuilder.aProductMacBook().build(), new Quantity(1));
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

    public Billing aValidBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-123-123"))
                .email(new Email("bruno.lemos@live.com"))
                .fullName(new FullName("Bruno", "Ramos Lemos"))
                .build();
    }

    public Shipping aValidShipping() {
        Recipient recipient = Recipient.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("225-09-1992"))
                .phone(new Phone("111-441-1244"))
                .build();

        return Shipping.builder()
                .address(anAddress())
                .recipient(recipient)
                .cost(this.shippingCost)
                .expectedDate(this.expectedDeliveryDate)
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

    public static Shipping.ShippingBuilder aShipping(){
        Recipient recipient = Recipient.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("225-09-1992"))
                .phone(new Phone("111-441-1244"))
                .build();

        return Shipping.builder()
                .address(anAddress())
                .recipient(recipient)
                .cost(new Money("10.00"))
                .expectedDate(LocalDate.now().plusWeeks(2));
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

    public OrderTestDataBuilder shippingInfo(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billingInfo(Billing billing) {
        this.billing = billing;
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
