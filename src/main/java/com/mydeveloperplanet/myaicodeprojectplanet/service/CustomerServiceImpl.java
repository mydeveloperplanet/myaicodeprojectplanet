package com.mydeveloperplanet.myaicodeprojectplanet.service;

import com.mydeveloperplanet.myaicodeprojectplanet.model.Customer;
import com.mydeveloperplanet.myaicodeprojectplanet.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.getCustomerById(id);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.createCustomer(customer);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.updateCustomer(id, customerDetails);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteCustomer(id);
    }
}
