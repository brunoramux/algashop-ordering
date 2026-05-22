package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.order.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.order.service.BuyNowService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.service.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.valueobject.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

    private final BuyNowService buyNowService;
    private final ProductCatalogService productCatalogService;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    private final Orders orders;
    private final Customers customers;

    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;

    @Transactional
    public String buyNow(BuyNowInput input) {

        Objects.requireNonNull(input);

        Customer customer = customers.ofId(new CustomerId(input.getCustomerId())).orElseThrow(
                CustomerNotFoundException::new
        );

        // CONSULTA CATALOGO PARA INFORMAÇÕES DO PRODUTO
        Product product = productCatalogService.ofId(new ProductId(input.getProductId()))
                .orElseThrow(ProductNotFoundException::new);

        // CALCULA CUSTO DE ENTREGA A PARTIR DOS ZIPCODES DE ORIGIN E DESTINO
        ZipCode originZipCode = originAddressService.originAddress().zipCode();
        ZipCode destinationZipCode = new ZipCode(input.getShipping().getAddress().getZipCode());

        // CRIA OBJETO DE REQUEST PARA ENVIAR A API QUE CALCULA A TAXA DE ENTREGA
        ShippingCostService.CalculationRequest calculationRequest = ShippingCostService.CalculationRequest.builder()
                .origin(originZipCode)
                .destination(destinationZipCode)
                .build();

        // CONSULTA API E RETORNA VALORES
        ShippingCostService.CalculationResult calculationResponse = shippingCostService.calculate(calculationRequest);

        // MONTA OS OBJETOS DE DOMINIO PARA SHIPPING E BILLING A PARTIR DOS INPUTS E DA RESPOSTA DO SERVIÇO DE CALCULO DE FRETE
        Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), calculationResponse);
        Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());


        // CRIA A ORDER
        Order order = buyNowService.buyNow(
                product,
                customer,
                billing,
                shipping,
                new Quantity(input.getQuantity()),
                PaymentMethod.valueOf(input.getPaymentMethod())
        );

        orders.add(order);

        return order.id().toString();
    }
}
