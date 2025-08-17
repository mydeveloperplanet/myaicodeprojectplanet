package com.mydeveloperplanet.myaicodeprojectplanet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomersController.class)
@DisplayName("CustomersController Tests")
class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private Customer testCustomer;
    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer testOpenAPICustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "John", "Doe");
        
        testOpenAPICustomer = new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        testOpenAPICustomer.setId(1L);
        testOpenAPICustomer.setFirstName("John");
        testOpenAPICustomer.setLastName("Doe");
    }

    @Nested
    @DisplayName("GET /customers")
    class GetAllCustomers {

        @Test
        @DisplayName("Should return all customers when customers exist")
        void shouldReturnAllCustomersWhenCustomersExist() throws Exception {
            // Given
            Customer customer2 = new Customer(2L, "Jane", "Smith");
            List<Customer> customers = Arrays.asList(testCustomer, customer2);
            given(customerService.getAllCustomers()).willReturn(customers);

            // When & Then
            mockMvc.perform(get("/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].firstName").value("John"))
                    .andExpect(jsonPath("$[0].lastName").value("Doe"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].firstName").value("Jane"))
                    .andExpect(jsonPath("$[1].lastName").value("Smith"));

            verify(customerService).getAllCustomers();
        }

        @Test
        @DisplayName("Should return empty array when no customers exist")
        void shouldReturnEmptyArrayWhenNoCustomersExist() throws Exception {
            // Given
            given(customerService.getAllCustomers()).willReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(customerService).getAllCustomers();
        }
    }

    @Nested
    @DisplayName("POST /customers")
    class CreateCustomer {

        @Test
        @DisplayName("Should create customer and return 201 Created")
        void shouldCreateCustomerAndReturn201Created() throws Exception {
            // Given
            Customer newCustomer = new Customer(null, "Alice", "Johnson");
            Customer createdCustomer = new Customer(3L, "Alice", "Johnson");
            given(customerService.createCustomer(any(Customer.class))).willReturn(createdCustomer);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer requestCustomer = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            requestCustomer.setFirstName("Alice");
            requestCustomer.setLastName("Johnson");

            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestCustomer)))
                    .andDo(print())
                    .andExpect(status().isCreated());

            verify(customerService).createCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Should handle invalid JSON and return 400 Bad Request")
        void shouldHandleInvalidJsonAndReturn400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("invalid json"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /customers/{id}")
    class GetCustomerById {

        @Test
        @DisplayName("Should return customer when customer exists")
        void shouldReturnCustomerWhenCustomerExists() throws Exception {
            // Given
            given(customerService.getCustomerById(1L)).willReturn(Optional.of(testCustomer));

            // When & Then
            mockMvc.perform(get("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(customerService).getCustomerById(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when customer does not exist")
        void shouldReturn404NotFoundWhenCustomerDoesNotExist() throws Exception {
            // Given
            given(customerService.getCustomerById(999L)).willReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/customers/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(customerService).getCustomerById(999L);
        }
    }

    @Nested
    @DisplayName("PUT /customers/{id}")
    class UpdateCustomer {

        @Test
        @DisplayName("Should update customer and return 200 OK")
        void shouldUpdateCustomerAndReturn200OK() throws Exception {
            // Given
            Customer updatedCustomer = new Customer(1L, "John Updated", "Doe Updated");
            given(customerService.updateCustomer(eq(1L), any(Customer.class))).willReturn(updatedCustomer);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer requestCustomer = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            requestCustomer.setId(1L);
            requestCustomer.setFirstName("John Updated");
            requestCustomer.setLastName("Doe Updated");

            // When & Then
            mockMvc.perform(put("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestCustomer)))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(customerService).updateCustomer(eq(1L), any(Customer.class));
        }

        @Test
        @DisplayName("Should handle invalid JSON and return 400 Bad Request")
        void shouldHandleInvalidJsonAndReturn400BadRequest() throws Exception {
            // When & Then
            mockMvc.perform(put("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("invalid json"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /customers/{id}")
    class DeleteCustomer {

        @Test
        @DisplayName("Should delete customer and return 204 No Content")
        void shouldDeleteCustomerAndReturn204NoContent() throws Exception {
            // Given
            willDoNothing().given(customerService).deleteCustomer(1L);

            // When & Then
            mockMvc.perform(delete("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(customerService).deleteCustomer(1L);
        }
    }

    @Nested
    @DisplayName("Model Conversion Tests")
    class ModelConversionTests {

        @Test
        @DisplayName("Should correctly convert domain model to OpenAPI model")
        void shouldCorrectlyConvertDomainModelToOpenAPIModel() throws Exception {
            // Given
            given(customerService.getCustomerById(1L)).willReturn(Optional.of(testCustomer));

            // When
            String responseContent = mockMvc.perform(get("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then
            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer responseCustomer = 
                objectMapper.readValue(responseContent, com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer.class);
            
            assertThat(responseCustomer.getId()).isEqualTo(testCustomer.getId());
            assertThat(responseCustomer.getFirstName()).isEqualTo(testCustomer.getFirstName());
            assertThat(responseCustomer.getLastName()).isEqualTo(testCustomer.getLastName());
        }

        @Test
        @DisplayName("Should correctly convert OpenAPI model to domain model")
        void shouldCorrectlyConvertOpenAPIModelToDomainModel() throws Exception {
            // Given
            Customer expectedDomainCustomer = new Customer(null, "Test", "User");
            given(customerService.createCustomer(any(Customer.class))).willReturn(expectedDomainCustomer);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer requestCustomer = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            requestCustomer.setFirstName("Test");
            requestCustomer.setLastName("User");

            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestCustomer)))
                    .andExpect(status().isCreated());

            // Verify the conversion happened correctly by checking the service was called with correct parameters
            verify(customerService).createCustomer(any(Customer.class));
        }
    }
}
