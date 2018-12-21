package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import java.util.List;

/**
 * Customer Repository defining Customer related operations.
 */
public interface CustomerRepository {

    /**
     * Returns the list of all costumers persisted in the repo.
     * @return List of {@link Customer}.
     */
    List<Customer> getCustomers();

    /**
     * Add new Customer to Repo.
     * @param customer instance of {@link Customer}.
     */
    void addCustomer(Customer customer);
}
