package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import com.agilemonkeys.exception.CustomerRepositoryException;
import java.util.Optional;

/**
 * Customer Repository defining Customer related operations.
 */
public interface CustomerRepository {

    /**
     * Add new {@link Customer} to the Repository.
     * @param customer instance of {@link Customer}.
     * @return The saved {@link Customer} instance.
     * @throws CustomerRepositoryException upon Failure.
     */
    Customer add(Customer customer);

    /**
     * Returns {@link Customer} instance given its id.
     * @return {@link Customer} instance if record found, Optional.empty() otherwise.
     */
    Optional<Customer> findById(String id);

    /**
     * Returns List of all costumers persisted in the repository.
     * @return List of {@link Customer}.
     */
    Iterable<Customer> findAll();

    /**
     * Update existing {@link Customer}.
     * @param customer instance of {@link Customer}.
     * @return updated {@link Customer}.
     * @throws CustomerRepositoryException upon Failure.
     */
    Customer update(Customer customer);

    /**
     * Delete {@link Customer} given its id
     * @param id Customer's Unique identifier.
     * @throws CustomerRepositoryException upon Failure.
     */
    void delete(String id);

}
