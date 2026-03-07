package com.mydeveloperplanet.myaicodeprojectplanet.controller;

import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomersController.class)
class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer testCustomer;
    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "John", "Doe");
        openAPICustomer = new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer.setId(1L);
        openAPICustomer.setFirstName("John");
        openAPICustomer.setLastName("Doe");
    }

    @Test
    void testGetAllCustomers_ReturnsListOfCustomers() throws Exception {
        // Arrange
        Customer customer2 = new Customer(2L, "Jane", "Smith");
        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("John", "Doe", "Jane", "Smith");
                });

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testGetAllCustomers_ReturnsEmptyList() throws Exception {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testCreateCustomer_ReturnsCreated() throws Exception {
        // Arrange
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act & Assert
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openAPICustomer)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void testGetCustomerById_WithValidId_ReturnsCustomer() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(get("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("John", "Doe", "1");
                });

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void testGetCustomerById_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        when(customerService.getCustomerById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/customers/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(999L);
    }

    @Test
    void testUpdateCustomer_WithValidId_ReturnsOk() throws Exception {
        // Arrange
        Customer updatedCustomer = new Customer(1L, "John", "Updated");
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(updatedCustomer);

        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer updateRequest =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        updateRequest.setId(1L);
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Updated");

        // Act & Assert
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void testDeleteCustomer_WithValidId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(customerService).deleteCustomer(1L);

        // Act & Assert
        mockMvc.perform(delete("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    void testGetAllCustomers_VerifiesServiceCallCount() throws Exception {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer));

        // Act
        mockMvc.perform(get("/customers"));
        mockMvc.perform(get("/customers"));

        // Assert
        verify(customerService, times(2)).getAllCustomers();
    }

    @Test
    void testCreateCustomer_PassesCorrectDataToService() throws Exception {
        // Arrange
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        // Act & Assert
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openAPICustomer)))
                .andExpect(status().isCreated());

        verify(customerService, times(1)).createCustomer(argThat(customer ->
                customer.getFirstName().equals("John") &&
                customer.getLastName().equals("Doe")
        ));
    }

    @Test
    void testUpdateCustomer_PassesCorrectIdAndData() throws Exception {
        // Arrange
        Customer updatedCustomer = new Customer(1L, "John", "Updated");
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(updatedCustomer);

        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer updateRequest =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Updated");

        // Act & Assert
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCustomer(eq(1L), argThat(customer ->
                customer.getFirstName().equals("John") &&
                customer.getLastName().equals("Updated")
        ));
    }

}
