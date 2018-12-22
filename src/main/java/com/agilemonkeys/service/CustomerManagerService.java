package com.agilemonkeys.service;

import com.agilemonkeys.controller.RequestBody.CustomerRequestBody;
import com.agilemonkeys.controller.ResponseBody.CustomerResponseBody;
import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

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
                .photo(profilePicture)
                .email(customerRequestBody.getEmail())
                .build();
        customerRepository.addCustomer(customer);
    }

    public List<CustomerResponseBody> getCustomers() {
        log.info("Service retrieving all customers.");
        return customerRepository.getCustomers()
                .stream()
                .map(customer ->
                        CustomerResponseBody.builder()
                                .id(customer.getId())
                                .name(customer.getName())
                                .surname(customer.getSurname())
                                .photoUrl(customer.getPhotoUrl())
                                .createdBy(customer.getCreatedBy())
                                .lastModifiedBy(customer.getLastModifiedBy())
                                .email(customer.getEmail())
                                .build())
                .collect(Collectors.toList());
    }
}
