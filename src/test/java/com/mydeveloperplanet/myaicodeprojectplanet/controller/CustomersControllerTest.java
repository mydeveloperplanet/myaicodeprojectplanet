package com.mydeveloperplanet.myaicodeprojectplanet.controller;

import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
//import com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomersController.class)
class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;
    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer1;
    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(1L, "John", "Doe");
        customer2 = new Customer(2L, "Jane", "Smith");
        
        openAPICustomer1 = new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer1.setId(1L);
        openAPICustomer1.setFirstName("John");
        openAPICustomer1.setLastName("Doe");
        
        openAPICustomer2 = new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer2.setId(2L);
        openAPICustomer2.setFirstName("Jane");
        openAPICustomer2.setLastName("Smith");
    }

    @Test
    void customersGet_ShouldReturnAllCustomers() throws Exception {
        // Given
        List<Customer> customers = Arrays.asList(customer1, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // When & Then
        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
        
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void customersPost_ShouldCreateNewCustomer() throws Exception {
        // Given
        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer1);

        // When & Then
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated());
        
        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void customersIdGet_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
        // Given
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer1));

        // When & Then
        mockMvc.perform(get("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
        
        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void customersIdGet_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        // Given
        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void customersIdPut_ShouldUpdateCustomer() throws Exception {
        // Given
        when(customerService.updateCustomer(anyLong(), any(Customer.class))).thenReturn(customer1);

        // When & Then
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isOk());
        
        verify(customerService, times(1)).updateCustomer(anyLong(), any(Customer.class));
    }

    @Test
    void customersIdDelete_ShouldDeleteCustomer() throws Exception {
        // When & Then
        mockMvc.perform(delete("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(customerService, times(1)).deleteCustomer(1L);
    }
}