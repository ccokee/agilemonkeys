package com.agilemonkeys.exception;

public class PhotoFileException extends RuntimeException {
    public PhotoFileException(Exception exception) {
        super(exception);
    }
    public PhotoFileException(String message) {
        super(message);
    }
}
