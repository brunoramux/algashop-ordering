package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OrderControllerIT {

    @LocalServerPort
    private int port;

    private static boolean databaseInitialized;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    private static final UUID validCustomerId = UUID.fromString("019e6c32-f3c5-7db2-8555-148c50d44625");

    @BeforeEach
    public void setup(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        initDatabase();

    }

    private void initDatabase(){
        if(databaseInitialized){
            return;
        }

        customerRepository.saveAndFlush(
                CustomerPersistenceEntityTestDataBuilder.aCustomer()
                        .id(validCustomerId)
                        .build()
        );

        databaseInitialized = true;
    }

    @Test
    public void shouldCreateOrderUsingSingleProduct() {
        RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-product.v1+json")
                    .body("""
                            {
                              "customerId": "019e6c32-f3c5-7db2-8555-148c50d44625",
                              "productId": "28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff",
                              "quantity": 12,
                              "paymentMethod": "GATEWAY_BALANCE",
                              "shipping": {
                                "recipient": {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "document": "12345",
                                  "phone": "5511912341234"
                                },
                                "address": {
                                  "street": "123 Main St",
                                  "number": "4B",
                                  "complement": "Apt 101",
                                  "neighborhood": "Downtown",
                                  "city": "Springfield",
                                  "state": "IL",
                                  "zipCode": "71777-000"
                                }
                              },
                              "billing": {
                                "firstName": "John",
                                "lastName": "Doe",
                                "document": "12345",
                                "phone": "5511912341234",
                                "email": "johndoe@email.com",
                                "address": {
                                  "street": "123 Main St",
                                  "number": "4B",
                                  "complement": "Apt 101",
                                  "neighborhood": "Downtown",
                                  "city": "Springfield",
                                  "state": "IL",
                                  "zipCode": "71777-000"
                                }
                              }
                            }
                            """)
                .when()
                    .post("/api/v1/orders")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value());
    }

}
