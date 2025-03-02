package com.mydeveloperplanet.myaicodeprojectplanet.repository;

import java.util.List;
import java.util.Optional;

import com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.Customers;
import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;

import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.mydeveloperplanet.myaicodeprojectplanet.jooq.tables.Customers.CUSTOMERS;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@Testcontainers
@Import(CustomerRepository.class)
public class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        // Initialize the database schema and insert test data here if needed
        dslContext.insertInto(Customers.CUSTOMERS,
                Customers.CUSTOMERS.FIRST_NAME,
                Customers.CUSTOMERS.LAST_NAME)
                .values("Vince", "Doe")
                .execute();
    }

    @AfterEach
    public void tearDown() {
        dslContext.truncateTable(CUSTOMERS).cascade().execute();
    }

    @Test
    public void getAllCustomers() {
        List<Customer> customers = customerRepository.getAllCustomers();
        assertThat(customers).isNotEmpty();
    }

    @Test
    public void getCustomerById() {
        Optional<Customer> customer = customerRepository.getCustomerById(1L);
        assertThat(customer).isPresent();
    }

    @Test
    public void createCustomer() {
        Customer newCustomer = new Customer(null, "John", "Doe");
        Customer createdCustomer = customerRepository.createCustomer(newCustomer);
        assertThat(createdCustomer.getId()).isNotNull();
    }

    @Test
    public void updateCustomer() {
        Customer updatedCustomerDetails = new Customer(1L, "Jane", "Doe");
        Customer updatedCustomer = customerRepository.updateCustomer(1L, updatedCustomerDetails);
        assertThat(updatedCustomer.getFirstName()).isEqualTo("Jane");
        assertThat(updatedCustomer.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void deleteCustomer() {
        customerRepository.deleteCustomer(1L);
        Optional<Customer> deletedCustomer = customerRepository.getCustomerById(1L);
        assertThat(deletedCustomer).isNotPresent();
    }
}
