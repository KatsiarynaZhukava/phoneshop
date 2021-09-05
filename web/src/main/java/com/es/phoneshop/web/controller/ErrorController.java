package com.es.phoneshop.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {
    @GetMapping(value = "errors")
    public ModelAndView showErrorPage( final HttpServletRequest httpRequest ) {
        String errorMessage = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorMessage = "Http Error Code: 400. Bad Request";
                break;
            }
            case 404: {
                errorMessage = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMessage = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", errorMessage);
        return modelAndView;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
    }
}