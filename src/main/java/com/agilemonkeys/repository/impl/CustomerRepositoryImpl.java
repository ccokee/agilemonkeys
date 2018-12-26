package com.agilemonkeys.repository.impl;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Repository
@Qualifier("fakeRepository")
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

    private Map<Integer, Customer> customers = new HashMap<Integer, Customer>();

    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    public void addCustomer(Customer customer) {
        customer.setId(1);
        customer.setPhotoUrl("gcs://...");
        customers.put(1, customer);
        log.info("Added new Customer {}", customer);
    }

    public Optional<Customer> findById(String id) {
        if(customers.containsKey(id))
            return Optional.of(customers.get(id));
        else
            return Optional.empty();
    }

    public void deleteCustomer(String id){
        customers.remove(id);
    }
}
