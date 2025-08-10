package com.mydeveloperplanet.myaicodeprojectplanet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.service.CustomerService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomersController.class)
class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void customersGet_ShouldReturnAllCustomers() throws Exception {
        // Given
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "John", "Doe"),
                new Customer(2L, "Jane", "Smith")
        );
        when(customerService.getAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/customers"))
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

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void customersGet_ShouldReturnEmptyListWhenNoCustomers() throws Exception {
        // Given
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void customersPost_ShouldCreateCustomer() throws Exception {
        // Given
        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer.setId(1L);
        openAPICustomer.setFirstName("John");
        openAPICustomer.setLastName("Doe");

        Customer domainCustomer = new Customer(1L, "John", "Doe");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(domainCustomer);

        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openAPICustomer)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void customersIdGet_ShouldReturnCustomerWhenExists() throws Exception {
        // Given
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "John", "Doe");
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        mockMvc.perform(get("/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(customerService, times(1)).getCustomerById(customerId);
    }

    @Test
    void customersIdGet_ShouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        // Given
        Long customerId = 999L;
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/customers/{id}", customerId))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(customerId);
    }

    @Test
    void customersIdPut_ShouldUpdateCustomer() throws Exception {
        // Given
        Long customerId = 1L;
        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer.setId(customerId);
        openAPICustomer.setFirstName("John");
        openAPICustomer.setLastName("Updated");

        Customer updatedCustomer = new Customer(customerId, "John", "Updated");
        when(customerService.updateCustomer(eq(customerId), any(Customer.class))).thenReturn(updatedCustomer);

        // When & Then
        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openAPICustomer)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(eq(customerId), any(Customer.class));
    }

    @Test
    void customersIdDelete_ShouldDeleteCustomer() throws Exception {
        // Given
        Long customerId = 1L;
        doNothing().when(customerService).deleteCustomer(customerId);

        // When & Then
        mockMvc.perform(delete("/customers/{id}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(customerId);
    }

    @Test
    void customersPost_ShouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(Customer.class));
    }

    @Test
    void customersIdPut_ShouldHandleInvalidJson() throws Exception {
        // Given
        Long customerId = 1L;

        // When & Then
        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(eq(customerId), any(Customer.class));
    }

    @Test
    void customersPost_ShouldHandleMissingContentType() throws Exception {
        // Given
        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer.setFirstName("John");
        openAPICustomer.setLastName("Doe");

        // When & Then
        mockMvc.perform(post("/customers")
                        .content(objectMapper.writeValueAsString(openAPICustomer)))
                .andExpect(status().isUnsupportedMediaType());

        verify(customerService, never()).createCustomer(any(Customer.class));
    }
}
