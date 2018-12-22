package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Qualifier("fakeRepository")
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

    private List<Customer> customers = new ArrayList<Customer>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        String uniqueID = UUID.randomUUID().toString();
        customer.setId(uniqueID);
        customer.setPhotoUrl("gcs://...");
        customers.add(customer);
        log.info("Added new Customer {}", customer);
    }
}
