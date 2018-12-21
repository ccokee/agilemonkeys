package com.agilemonkeys.controller;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CustomerManagerController {

    private CustomerRepository customerRepository;

    public CustomerManagerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping(
        value = "/customer",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity addCustomer(@RequestPart("customer") Customer customer, @RequestPart("image") MultipartFile image) {
        customerRepository.addCustomer(customer);
        System.out.println("image size" + image.getSize());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Customer>> getCostumers() {
        List<Customer> costumers = customerRepository.getCustomers();
        return ResponseEntity.ok(costumers);
    }
}
