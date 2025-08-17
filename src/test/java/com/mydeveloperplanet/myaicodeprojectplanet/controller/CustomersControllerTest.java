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
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
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
    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openApiCustomer;
    private List<Customer> testCustomers;

    @BeforeEach
    void setUp() {
        // Domain model customer
        testCustomer = new Customer(1L, "John", "Doe");
        
        // OpenAPI model customer
        openApiCustomer = new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openApiCustomer.setId(1L);
        openApiCustomer.setFirstName("John");
        openApiCustomer.setLastName("Doe");
        
        // Test customers list
        testCustomers = Arrays.asList(
            new Customer(1L, "John", "Doe"),
            new Customer(2L, "Jane", "Smith"),
            new Customer(3L, "Bob", "Johnson")
        );
    }

    @Nested
    @DisplayName("GET /customers")
    class GetAllCustomersTests {

        @Test
        @DisplayName("Should return all customers when customers exist")
        void shouldReturnAllCustomersWhenCustomersExist() throws Exception {
            // Given
            given(customerService.getAllCustomers()).willReturn(testCustomers);

            // When & Then
            mockMvc.perform(get("/customers")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].firstName").value("John"))
                    .andExpect(jsonPath("$[0].lastName").value("Doe"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].firstName").value("Jane"))
                    .andExpect(jsonPath("$[1].lastName").value("Smith"))
                    .andExpect(jsonPath("$[2].id").value(3))
                    .andExpect(jsonPath("$[2].firstName").value("Bob"))
                    .andExpect(jsonPath("$[2].lastName").value("Johnson"));

            // Verify service interaction
            then(customerService).should().getAllCustomers();
        }

        @Test
        @DisplayName("Should return empty array when no customers exist")
        void shouldReturnEmptyArrayWhenNoCustomersExist() throws Exception {
            // Given
            given(customerService.getAllCustomers()).willReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/customers")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            // Verify service interaction
            then(customerService).should().getAllCustomers();
        }
    }

    @Nested
    @DisplayName("POST /customers")
    class CreateCustomerTests {

        @Test
        @DisplayName("Should create customer successfully with valid data")
        void shouldCreateCustomerSuccessfullyWithValidData() throws Exception {
            // Given
            given(customerService.createCustomer(any(Customer.class))).willReturn(testCustomer);

            String customerJson = objectMapper.writeValueAsString(openApiCustomer);

            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andDo(print())
                    .andExpect(status().isCreated());

            // Verify service interaction
            then(customerService).should().createCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Should handle customer creation with null ID")
        void shouldHandleCustomerCreationWithNullId() throws Exception {
            // Given
            Customer customerWithoutId = new Customer(null, "John", "Doe");
            given(customerService.createCustomer(any(Customer.class))).willReturn(customerWithoutId);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openApiCustomerWithoutId = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            openApiCustomerWithoutId.setFirstName("John");
            openApiCustomerWithoutId.setLastName("Doe");

            String customerJson = objectMapper.writeValueAsString(openApiCustomerWithoutId);

            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andDo(print())
                    .andExpect(status().isCreated());

            // Verify service interaction
            then(customerService).should().createCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid JSON")
        void shouldReturnBadRequestForInvalidJson() throws Exception {
            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("GET /customers/{id}")
    class GetCustomerByIdTests {

        @Test
        @DisplayName("Should return customer when customer exists")
        void shouldReturnCustomerWhenCustomerExists() throws Exception {
            // Given
            Long customerId = 1L;
            given(customerService.getCustomerById(customerId)).willReturn(Optional.of(testCustomer));

            // When & Then
            mockMvc.perform(get("/customers/{id}", customerId)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            // Verify service interaction
            then(customerService).should().getCustomerById(customerId);
        }

        @Test
        @DisplayName("Should return not found when customer does not exist")
        void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
            // Given
            Long customerId = 999L;
            given(customerService.getCustomerById(customerId)).willReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/customers/{id}", customerId)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            // Verify service interaction
            then(customerService).should().getCustomerById(customerId);
        }

        @Test
        @DisplayName("Should handle invalid ID format")
        void shouldHandleInvalidIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(get("/customers/{id}", "invalid-id")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("PUT /customers/{id}")
    class UpdateCustomerTests {

        @Test
        @DisplayName("Should update customer successfully with valid data")
        void shouldUpdateCustomerSuccessfullyWithValidData() throws Exception {
            // Given
            Long customerId = 1L;
            Customer updatedCustomer = new Customer(customerId, "John Updated", "Doe Updated");
            given(customerService.updateCustomer(eq(customerId), any(Customer.class))).willReturn(updatedCustomer);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer updateRequest = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            updateRequest.setId(customerId);
            updateRequest.setFirstName("John Updated");
            updateRequest.setLastName("Doe Updated");

            String customerJson = objectMapper.writeValueAsString(updateRequest);

            // When & Then
            mockMvc.perform(put("/customers/{id}", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andDo(print())
                    .andExpect(status().isOk());

            // Verify service interaction
            then(customerService).should().updateCustomer(eq(customerId), any(Customer.class));
        }

        @Test
        @DisplayName("Should handle mismatched ID in path and body")
        void shouldHandleMismatchedIdInPathAndBody() throws Exception {
            // Given
            Long pathId = 1L;
            Long bodyId = 2L;
            Customer updatedCustomer = new Customer(pathId, "John", "Doe");
            given(customerService.updateCustomer(eq(pathId), any(Customer.class))).willReturn(updatedCustomer);

            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer updateRequest = 
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
            updateRequest.setId(bodyId);
            updateRequest.setFirstName("John");
            updateRequest.setLastName("Doe");

            String customerJson = objectMapper.writeValueAsString(updateRequest);

            // When & Then
            mockMvc.perform(put("/customers/{id}", pathId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andDo(print())
                    .andExpect(status().isOk());

            // Verify service interaction uses path ID
            then(customerService).should().updateCustomer(eq(pathId), any(Customer.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid JSON")
        void shouldReturnBadRequestForInvalidJson() throws Exception {
            // When & Then
            mockMvc.perform(put("/customers/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("DELETE /customers/{id}")
    class DeleteCustomerTests {

        @Test
        @DisplayName("Should delete customer successfully")
        void shouldDeleteCustomerSuccessfully() throws Exception {
            // Given
            Long customerId = 1L;
            willDoNothing().given(customerService).deleteCustomer(customerId);

            // When & Then
            mockMvc.perform(delete("/customers/{id}", customerId))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // Verify service interaction
            then(customerService).should().deleteCustomer(customerId);
        }

        @Test
        @DisplayName("Should handle invalid ID format for deletion")
        void shouldHandleInvalidIdFormatForDeletion() throws Exception {
            // When & Then
            mockMvc.perform(delete("/customers/{id}", "invalid-id"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Model Conversion Tests")
    class ModelConversionTests {

        @Test
        @DisplayName("Should convert domain model to OpenAPI model correctly")
        void shouldConvertDomainModelToOpenApiModelCorrectly() throws Exception {
            // Given
            given(customerService.getCustomerById(1L)).willReturn(Optional.of(testCustomer));

            // When
            String response = mockMvc.perform(get("/customers/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then
            com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer responseCustomer = 
                objectMapper.readValue(response, com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer.class);

            assertThat(responseCustomer.getId()).isEqualTo(testCustomer.getId());
            assertThat(responseCustomer.getFirstName()).isEqualTo(testCustomer.getFirstName());
            assertThat(responseCustomer.getLastName()).isEqualTo(testCustomer.getLastName());
        }

        @Test
        @DisplayName("Should convert OpenAPI model to domain model correctly")
        void shouldConvertOpenApiModelToDomainModelCorrectly() throws Exception {
            // Given
            given(customerService.createCustomer(any(Customer.class))).willAnswer(invocation -> {
                Customer customer = invocation.getArgument(0);
                assertThat(customer.getId()).isEqualTo(openApiCustomer.getId());
                assertThat(customer.getFirstName()).isEqualTo(openApiCustomer.getFirstName());
                assertThat(customer.getLastName()).isEqualTo(openApiCustomer.getLastName());
                return customer;
            });

            String customerJson = objectMapper.writeValueAsString(openApiCustomer);

            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(customerJson))
                    .andExpect(status().isCreated());

            // Verification is done in the answer callback above
            then(customerService).should().createCustomer(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle service exceptions gracefully")
        void shouldHandleServiceExceptionsGracefully() throws Exception {
            // Given
            given(customerService.getAllCustomers()).willThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/customers")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is5xxServerError());

            // Verify service interaction
            then(customerService).should().getAllCustomers();
        }

        @Test
        @DisplayName("Should handle missing Content-Type header")
        void shouldHandleMissingContentTypeHeader() throws Exception {
            // When & Then
            mockMvc.perform(post("/customers")
                    .content(objectMapper.writeValueAsString(openApiCustomer)))
                    .andDo(print())
                    .andExpect(status().isUnsupportedMediaType());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Should handle empty request body")
        void shouldHandleEmptyRequestBody() throws Exception {
            // When & Then
            mockMvc.perform(post("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            // Verify service was not called
            then(customerService).shouldHaveNoInteractions();
        }
    }
}
