package com.es.phoneshop.web.controller.exceptionHandlers;

import com.es.phoneshop.web.exception.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidInputControllerAdvice {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Error> handleException( InvalidInputException e ) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
