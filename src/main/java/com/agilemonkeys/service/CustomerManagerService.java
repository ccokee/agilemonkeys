package com.agilemonkeys.service;

import com.agilemonkeys.controller.RequestBody.CustomerRequestBody;
import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.exception.CustomerNotFoundException;
import com.agilemonkeys.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerManagerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerService.class);

    @Autowired
    @Qualifier("fakeRepository")
    private CustomerRepository customerRepository;

    public CustomerManagerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void addCustomer(CustomerRequestBody customerRequestBody, MultipartFile profilePicture) {
        log.info("Service received new Customer {} with picture {} to be added.", customerRequestBody, profilePicture.getName());
        Customer customer = Customer.builder()
                .name(customerRequestBody.getName())
                .surname(customerRequestBody.getSurname())
                .email(customerRequestBody.getEmail())
                .build();
        customerRepository.addCustomer(customer);
    }

    public List<Customer> getCustomers() {
        log.info("Service retrieving all customers.");
        return customerRepository.findAll();
    }

    public Customer findById(String id) {
        log.info("Service retrieving Customer {}.", id);
        Optional<Customer> customer = customerRepository.findById(id);

        if (!customer.isPresent())
            throw new CustomerNotFoundException("Customer " + id + " not found.");

        return customer.get();
    }

    public void deleteCustomer(String id) {
        log.info("Service deleting Customer {}.", id);
        Optional<Customer> customer = customerRepository.findById(id);

        if (!customer.isPresent())
            throw new CustomerNotFoundException("Customer " + id + " not found.");

        customerRepository.deleteCustomer(id);
    }
}
