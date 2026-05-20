package com.algaworks.algashop.ordering.infrastructure.shipping.client.fake;

import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.springframework.stereotype.Component;

@Component
public class FixedOriginAddressService implements OriginAddressService {
    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Rua José Ferreira Candido")
                .number("200")
                .neighborhood("Recanto Elimar")
                .complement("Casa")
                .city("Franca")
                .state("São Paulo")
                .zipCode(new ZipCode("14303-288"))
                .build();
    }
}
