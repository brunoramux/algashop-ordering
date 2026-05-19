package com.algaworks.algashop.ordering.infrastructure.fake;

import com.algaworks.algashop.ordering.domain.model.service.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
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
