package com.es.core.exception;

import java.text.MessageFormat;

public class OutOfStockException extends Exception {
    public OutOfStockException( final long stockRequested, final long stockAvailable ) {
        super( MessageFormat.format("The requested stock {0} exceeds the available {1}",
                                    stockRequested, stockAvailable ));
    }
}
