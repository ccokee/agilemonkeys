package com.agilemonkeys.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String exception) {
        super(exception);
    }
}
