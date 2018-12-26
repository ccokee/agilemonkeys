package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.UUID;

@Repository
@Qualifier("fakeRepository")
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

    private Map<String, Customer> customers = new HashMap<String, Customer>();

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers.values());
    }

    public void addCustomer(Customer customer) {
        String uniqueID = UUID.randomUUID().toString();
        customer.setId(uniqueID);
        customer.setPhotoUrl("gcs://...");
        customers.put(uniqueID, customer);
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
