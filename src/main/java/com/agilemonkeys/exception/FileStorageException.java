package com.agilemonkeys.exception;

public class FileStorageException extends RuntimeException{
    public FileStorageException(Exception exception) {
        super(exception);
    }
}
