package com.algaworks.algashop.ordering.domain.model.exception;

public class ErrorMessages {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Invalid Email";
    public static final String VALIDATION_ERROR_FULLNAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FULLNAME_IS_BLANK = "FullName cannot be blank";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "BirthDate must be a past date";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived. Cannot be changed";

    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED = "Cannot change order %s from %s to %s.";

    public static final String ERROR_ORDER_INVALID_SHIPPING_DELIVERY_DATE = "Delivery date for order %s cannot be in the past.";

    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO = "Order %s cannot be placed. Shipping info is missing.";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_COST = "Order %s cannot be placed. Shipping cost is missing.";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO = "Order %s cannot be placed. Billing info is missing.";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_EXPECTED_DELIVERY_DATE = "Order %s cannot be placed. Expected delivery date is missing.";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD = "Order %s cannot be placed. Payment method is missing.";

    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ORDER_ITEM = "Order %s does not contain Order Item whit ID %s";

    public static final String ERROR_PRODUCT_OUT_OF_STOCK = "Product ID %s is out of stock.";

    public static final String ERROR_ORDER_CANNOT_BE_EDITED = "Order %s with status %s cannot be edited";

    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM = "Shopping Cart id %s does not contains item %s.";
    public static final String ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT = "Shopping Cart id %s does not contains product %s.";

    public static final String ERROR_SHOPPING_CART_ITEM_INCOMPATIBLE_PRODUCT = "Shopping Cart %s cannot be updated, incompatible product %s";
}
