package com.agilemonkeys.controller;

import com.agilemonkeys.controller.RequestBody.CustomerRequestBody;
import com.agilemonkeys.controller.ResponseBody.CustomerResponseBody;
import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.service.CustomerManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CustomerManagerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerController.class);

    private CustomerManagerService customerManagerService;

    public CustomerManagerController(CustomerManagerService customerManagerService) {
        this.customerManagerService = customerManagerService;
    }

    @PostMapping(
        value = "/customer",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity addCustomer(
            @Valid @RequestPart("customer") CustomerRequestBody customerRequestBody,
            @RequestPart("photo") MultipartFile photo) {
        log.info("Adding new Customer {} with profile picture {}", customerRequestBody, photo.getName());
        customerManagerService.addCustomer(customerRequestBody, photo);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/customers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponseBody>> getCostumers() {
        log.info("Retrieving all customers...");

        List<Customer> customers = customerManagerService.getCustomers();
        List<CustomerResponseBody> customerResponseBodies = customers
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

        return ResponseEntity.ok(customerResponseBodies);
    }

    @GetMapping(path = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseBody> findById(@PathVariable("id") String id) {
        log.info("Retrieving Customer {}.", id);
        Customer customer = customerManagerService.findById(id);
        return ResponseEntity.ok(CustomerResponseBody.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .photoUrl(customer.getPhotoUrl())
                .createdBy(customer.getCreatedBy())
                .lastModifiedBy(customer.getLastModifiedBy())
                .email(customer.getEmail())
                .build());
    }

    @DeleteMapping(path = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCustomer(@PathVariable("id") String id){
        log.info("Deleting Customer {}");
        this.customerManagerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }



}
