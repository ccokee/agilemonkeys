package com.agilemonkeys.controller;

import com.agilemonkeys.controller.response.body.CustomerResponseBody;
import com.agilemonkeys.controller.validation.CustomerValidationGroup;
import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.mapper.CustomerMapper;
import com.agilemonkeys.service.CustomerManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class CustomerManagerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerManagerController.class);
    private CustomerManagerService customerManagerService;

    public CustomerManagerController(CustomerManagerService customerManagerService) {
        this.customerManagerService = customerManagerService;
    }

    /**
     * Add new {@link Customer}.
     * @param customer instance of {@link Customer} to be added.
     * @param photo instance of {@link MultipartFile} to be added. Represents the picture.
     * @return {@link ResponseEntity} instance: Http response entity with body containing ID and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN","ROLE_USER"} )
    @PostMapping(
        value = "/customer",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<String> add(
        @Validated({CustomerValidationGroup.Add.class}) @RequestPart("customer") Customer customer,
        @RequestPart(value = "photo", required = false) MultipartFile photo) {

        // Retrieve user sessions to get username.
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Logged as user {}, ", user.getUsername());
        log.info("Adding new Customer {}", customer.toString());

        // Update photo with given file
        customer.setPhotoFromMultipartFile(photo);

        // Update createdBy and lastModifiedBy given by SecurityContextHolder
        customer.setCreatedBy(user.getUsername());
        customer.setLastModifiedBy(user.getUsername());

        String id = customerManagerService.add(customer);
        return ResponseEntity.ok(id);
    }

    /**
     * Find {@link Customer} by id.
     * @param id unique identifier.
     * @return {@link ResponseEntity} instance: Http response entity with found {@link CustomerResponseBody} body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN","ROLE_USER"} )
    @GetMapping(path = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponseBody> findById(@PathVariable("id") String id) {
        log.info("Retrieving Customer {}.", id);
        Customer customer = customerManagerService.findById(id);

        CustomerResponseBody customerResponseBody = CustomerMapper.customerToCustomerResponseBody(customer);
        return ResponseEntity.ok(customerResponseBody);
    }

    /**
     * Find all {@link Customer} persisted.
     * @return {@link ResponseEntity} instance: Http response entity with found List of {@link CustomerResponseBody} body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN","ROLE_USER"} )
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponseBody>> findAll() {
        log.info("Retrieving all customers...");

        Iterable<Customer> customers = customerManagerService.findAll();
        List<CustomerResponseBody> customerResponseBodies = StreamSupport.stream(customers.spliterator(), false)
            .map(CustomerMapper::customerToCustomerResponseBody)
            .collect(Collectors.toList());

        return ResponseEntity.ok(customerResponseBodies);
    }

    /**
     * Update {@link Customer}.
     * @param customer instance of {@link Customer} to be updated.
     * @param photo instance of {@link MultipartFile} to be updated.
     * @return {@link ResponseEntity} instance: Http response entity with empty body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN","ROLE_USER"} )
    @PutMapping(
            value = "/update-customer",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity update(
            @Validated({CustomerValidationGroup.Update.class}) @RequestPart("customer") Customer customer,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Logged as user {}, ", user.getUsername());
        log.info("Updating Customer.");

        // Update photo
        customer.setPhotoFromMultipartFile(photo);

        // Update lastModifiedBy given by SecurityContextHolder
        customer.setLastModifiedBy(user.getUsername());

        customerManagerService.update(customer);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Delete {@link Customer}.
     * @param id unique identifier.
     * @return {@link ResponseEntity} instance: Http response entity with empty body and Http Status code 200.
     */
    @Secured( {"ROLE_ADMIN","ROLE_USER"} )
    @DeleteMapping(path = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCustomer(@PathVariable("id") String id){
        log.info("Deleting Customer {}");
        this.customerManagerService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
