package com.agilemonkeys.mapper;

import com.agilemonkeys.controller.response.body.CustomerResponseBody;
import com.agilemonkeys.domain.Customer;

public class CustomerMapper {

    public static CustomerResponseBody customerToCustomerResponseBody(Customer customer) {
        return CustomerResponseBody.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surname(customer.getSurname())
                .photoUrl(customer.getPhotoUrl())
                .createdBy(customer.getCreatedBy())
                .lastModifiedBy(customer.getLastModifiedBy())
                .email(customer.getEmail())
                .build();
    }
}
