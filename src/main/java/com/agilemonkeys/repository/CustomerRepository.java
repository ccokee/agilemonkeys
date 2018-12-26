package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Customer Repository defining Customer related operations.
 */
public interface CustomerRepository {

    /**
     * Returns list of all costumers persisted in the repo.
     * @return List of {@link Customer}.
     */
    List<Customer> findAll();

    /**
     * Add new Customer to Repo.
     * @param customer instance of {@link Customer}.
     */
    void addCustomer(Customer customer);

    /**
     * Returns {@link Customer} instance given its id.
     * @return Customer's Unique identifier.
     */
    Optional<Customer> findById(String id);

    /**
     * Delete {@link Customer} given its id
     * @param id Customer's Unique identifier.
     */
    void deleteCustomer(String id);

}
