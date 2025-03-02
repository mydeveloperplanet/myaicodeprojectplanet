package com.mydeveloperplanet.myaicodeprojectplanet;

import java.util.List;

import com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.Customers;
import com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer;

import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.Customers.CUSTOMERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class CustomersControllerIntegrationTest {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private WebTestClient webTestClient;

    Long insertedId;

    @BeforeEach
    public void setUp() {
        // Initialize the database schema and insert test data here if needed
        insertedId = dslContext.insertInto(Customers.CUSTOMERS,
                        Customers.CUSTOMERS.FIRST_NAME,
                        Customers.CUSTOMERS.LAST_NAME)
                    .values("Vince", "Doe")
                    .returning()
                    .fetchOne(CUSTOMERS.ID);
    }

    @AfterEach
    public void tearDown() {
        dslContext.truncateTable(CUSTOMERS).cascade().execute();
    }

    @Test
    public void testCustomersGet() {
        webTestClient.get()
                .uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .hasSize(1) // Assuming there is one customer in the database for testing
                .consumeWith(response -> {
                    List<Customer> customers = response.getResponseBody();
                    assertThat(customers).isNotNull();
                    assertThat(customers).hasSize(1);
                    Customer customer = customers.get(0);
                    assertThat(customer.getId()).isEqualTo(insertedId);
                    assertThat(customer.getFirstName()).isEqualTo("Vince");
                    assertThat(customer.getLastName()).isEqualTo("Doe");
                });
    }

    @Test
    public void testCustomersPost() {
        Customer newCustomer = new Customer();
        newCustomer.setFirstName("Alice");
        newCustomer.setLastName("Johnson");

        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void testCustomersIdGet() {
        webTestClient.get()
                .uri("/customers/" + insertedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .consumeWith(response -> {
                    Customer customer = response.getResponseBody();
                    assertThat(customer).isNotNull();
                    assertThat(customer.getId()).isEqualTo(insertedId);
                    assertThat(customer.getFirstName()).isEqualTo("Vince");
                    assertThat(customer.getLastName()).isEqualTo("Doe");
                });
    }


    @Test
    public void testCustomersIdGetNotFound() {
        webTestClient.get()
                .uri("/customers/" + insertedId + 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testCustomersIdPut() {
        Customer updatedCustomerDetails = new Customer();
        updatedCustomerDetails.id(insertedId);
        updatedCustomerDetails.setFirstName("Jane");
        updatedCustomerDetails.setLastName("Doe");

        webTestClient.put()
                .uri("/customers/" + insertedId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCustomerDetails)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testCustomersIdDelete() {
        webTestClient.delete()
                .uri("/customers/" + insertedId)
                .exchange()
                .expectStatus().isNoContent();

        // Verify that the customer is deleted
        webTestClient.get()
                .uri("/customers/" + insertedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
