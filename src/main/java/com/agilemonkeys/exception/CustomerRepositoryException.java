package com.agilemonkeys.exception;

import com.agilemonkeys.repository.CustomerRepository;
/**
 * {@link CustomerRepository} related exception.
 */
public class CustomerRepositoryException extends RuntimeException {
    public CustomerRepositoryException(Exception exception) {
        super(exception);
    }
    public CustomerRepositoryException(String message) {
        super(message);
    }
}
