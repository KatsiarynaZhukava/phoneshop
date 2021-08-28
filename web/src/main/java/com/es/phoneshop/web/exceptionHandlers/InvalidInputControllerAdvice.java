package com.es.phoneshop.web.exceptionHandlers;

import com.es.phoneshop.web.exception.InvalidInputException;
import com.es.core.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.ArrayList;

@ControllerAdvice
public class InvalidInputControllerAdvice {

    @ExceptionHandler( InvalidInputException.class )
    public ResponseEntity<Error> handleException( InvalidInputException e ) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( ConstraintViolationException.class )
    public ModelAndView handleException( ConstraintViolationException e ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", new ArrayList<>(e.getConstraintViolations()).get(0).getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler( MethodArgumentTypeMismatchException.class )
    public ModelAndView handleException( MethodArgumentTypeMismatchException e ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", MessageFormat.format("{0} type mismatch", e.getName()));
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler( NotFoundException.class )
    public ModelAndView handleException( NotFoundException e ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.setViewName("error");
        return modelAndView;
    }
}