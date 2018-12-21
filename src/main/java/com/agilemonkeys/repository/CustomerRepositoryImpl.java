package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("fakeRepository")
public class CustomerRepositoryImpl implements CustomerRepository {

    private List<Customer> customers = new ArrayList<Customer>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }
}
