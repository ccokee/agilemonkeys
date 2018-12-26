package com.agilemonkeys.repository;

import com.agilemonkeys.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRelationalRepository extends CrudRepository<Customer, String> {}
