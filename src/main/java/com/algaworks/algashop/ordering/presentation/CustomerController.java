package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.customer.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.CustomerOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerManagementApplicationService customerManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input){
        UUID customerId = customerManagementApplicationService.create(input);
        return customerManagementApplicationService.findById(customerId);
    }
}
