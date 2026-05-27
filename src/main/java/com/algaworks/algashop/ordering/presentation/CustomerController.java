package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.customer.*;
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
    private final CustomerQueryService customerQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input){
        UUID customerId = customerManagementApplicationService.create(input);
        return customerManagementApplicationService.findById(customerId);
    }

    @GetMapping
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter){
        return PageModel.of(customerQueryService.filter(customerFilter));
    }
}

