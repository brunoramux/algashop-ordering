package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.domain.model.customer.exception.CustomerEmailIsInUseException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more fields are invalid. Please correct and try again.");
        problemDetail.setType(URI.create("/errors/invalid-fields"));

        Map<String, String> fieldsErrors = ex.getBindingResult().getAllErrors().stream().collect(
                Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale())
                )
        );

        problemDetail.setProperty("fields", fieldsErrors);


        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
             String message = messageSource.getMessage(fieldError, request.getLocale());
             problemDetail.setProperty(fieldError.getField(), message);
         });

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(CustomerEmailIsInUseException.class)
    protected ResponseEntity<Object> handleMethodEmailIsInUse(CustomerEmailIsInUseException ex, WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Email already in use");
        problemDetail.setDetail("Email already in use.");
        problemDetail.setType(URI.create("/errors/email-already-in-use"));


        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
