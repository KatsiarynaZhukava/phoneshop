package com.es.phoneshop.web.exception;

public class InvalidInputException extends RuntimeException {

    public InvalidInputException() { }

    public InvalidInputException(String message) {
        super(message);
    }
}