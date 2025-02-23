package com.mydeveloperplanet.myaicodeprojectplanet.controller;

import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.openapi.api.CustomersApi;
import com.mydeveloperplanet.myaicodeprojectplanet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomersController implements CustomersApi {

    @Autowired
    private CustomerService customerService;

    @Override
    public ResponseEntity<List<com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer>> customersGet() {
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(convertToOpenAPIModel(customers), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> customersPost(@RequestBody com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer) {
        Customer customer = convertToDomainModel(openAPICustomer);
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer> customersIdGet(@PathVariable Long id) {
        Optional<Customer> customerOptional = customerService.getCustomerById(id);
        if (customerOptional.isPresent()) {
            return new ResponseEntity<>(convertToOpenAPIModel(customerOptional.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> customersIdPut(@PathVariable Long id, @RequestBody com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer) {
        Customer customerDetails = convertToDomainModel(openAPICustomer);
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> customersIdDelete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private List<com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer> convertToOpenAPIModel(List<Customer> domainCustomers) {
        return domainCustomers.stream()
                .map(this::convertToOpenAPIModel)
                .toList();
    }

    private com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer convertToOpenAPIModel(Customer customer) {
        com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer =
                new com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer();
        openAPICustomer.setId(customer.getId());
        openAPICustomer.setFirstName(customer.getFirstName());
        openAPICustomer.setLastName(customer.getLastName());
        return openAPICustomer;
    }

    private Customer convertToDomainModel(com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer openAPICustomer) {
        return new Customer(
                openAPICustomer.getId(),
                openAPICustomer.getFirstName(),
                openAPICustomer.getLastName()
        );
    }
}
