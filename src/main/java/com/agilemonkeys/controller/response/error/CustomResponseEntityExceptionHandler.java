package com.agilemonkeys.controller.response.error;

import com.agilemonkeys.controller.response.body.ResponseBody;
import com.agilemonkeys.exception.PhotoFileException;
import com.agilemonkeys.exception.CustomerNotFoundException;
import com.agilemonkeys.exception.UserNotFoundException;
import com.agilemonkeys.exception.UserAlreadyExistsException;
import com.agilemonkeys.exception.RoleNotValidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(),
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PhotoFileException.class)
    protected ResponseEntity<Object> handleConflictPhotoFileException(PhotoFileException ex, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CustomerNotFoundException.class)
    protected ResponseEntity<Object> handleConflictCustomerNotFoundException(CustomerNotFoundException ex, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    protected ResponseEntity<Object> handleConflictUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RoleNotValidException.class)
    protected ResponseEntity<Object> handleRoleNotValidException(RoleNotValidException ex, WebRequest request) {
        ResponseBody exceptionResponse = new ResponseBody(new Date(), ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
