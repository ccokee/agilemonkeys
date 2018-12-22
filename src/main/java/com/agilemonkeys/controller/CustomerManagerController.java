package com.agilemonkeys.controller;

import com.agilemonkeys.controller.RequestBody.CustomerRequestBody;
import com.agilemonkeys.controller.ResponseBody.CustomerResponseBody;
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

    @RequestMapping(value = "/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponseBody>> getCostumers() {
        log.info("Retrieving all customers...");
        return ResponseEntity.ok(customerManagerService.getCustomers());
    }
}
