package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.customer.*;
import com.algaworks.algashop.ordering.infrastructure.config.security.SecurityAnnotations;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerManagementApplicationService customerManagementApplicationService;
    private final CustomerQueryService customerQueryService;

    @SecurityAnnotations.CanWriteCustomers
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOutput create(@RequestBody @Valid CustomerInput input, HttpServletResponse httpServletResponse) {
        UUID customerId = customerManagementApplicationService.create(input);

        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(
                MvcUriComponentsBuilder.on(CustomerController.class).findById(customerId)
        );
        httpServletResponse.addHeader("Location", builder.build().toUriString());

        return customerManagementApplicationService.findById(customerId);
    }

    @SecurityAnnotations.CanReadCustomers
    @GetMapping
    public PageModel<CustomerSummaryOutput> findAll(CustomerFilter customerFilter){
        return PageModel.of(customerQueryService.filter(customerFilter));
    }

    @SecurityAnnotations.CanReadCustomers
    @GetMapping("/{customerId}")
    public CustomerOutput findById(@PathVariable UUID customerId){
        return customerQueryService.findById(customerId);
    }

    @SecurityAnnotations.CanWriteCustomers
    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID customerId) {
        customerManagementApplicationService.archive(customerId);
    }
}

