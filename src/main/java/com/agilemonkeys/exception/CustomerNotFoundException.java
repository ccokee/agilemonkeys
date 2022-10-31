package com.agilemonkeys.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String exception) {
        super(exception);
    }
}
