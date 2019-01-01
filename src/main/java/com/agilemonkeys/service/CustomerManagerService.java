package com.agilemonkeys.service;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.exception.CustomerNotFoundException;
import com.agilemonkeys.repository.CustomerRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomerManagerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerService.class);

    @Autowired
    @Qualifier("CustomerRepositoryImpl")
    private CustomerRepository customerRepository;

    public CustomerManagerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String add(@NonNull Customer customer) {
        log.info("Service received new Customer to be added.");
        Customer addedCostumer = customerRepository.add(customer);
        return addedCostumer.getId();
    }

    public Customer findById(@NonNull String id) {
        log.info("Service retrieving Customer with ID {}.", id);
        Optional<Customer> customer = customerRepository.findById(id);

        if (!customer.isPresent())
            throw new CustomerNotFoundException("Customer ID " + id + " not found.");

        return customer.get();
    }

    public Iterable<Customer> findAll() {
        log.info("Service retrieving all customers.");
        return customerRepository.findAll();
    }

    public void update(@NonNull Customer customer) {
        if(customer.getId() == null)
            throw new CustomerNotFoundException("Id must be provided in Request.");

        log.info("Service updating Customer {}", customer.getId());
        customerRepository.update(customer);
    }

    public void delete(@NonNull String id) {
        log.info("Service deleting Customer with ID {}.", id);
        customerRepository.delete(id);
    }
}
