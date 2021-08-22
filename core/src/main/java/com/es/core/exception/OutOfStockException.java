package com.es.core.exception;

import java.text.MessageFormat;

public class OutOfStockException extends Exception {
    private final Long phoneId;
    private final Long stockRequested;
    private final Long stockAvailable;

    public OutOfStockException( long phoneId, long stockRequested, long stockAvailable ) {
        super( MessageFormat.format("The overall requested stock {0} exceeds the available {1}",
                                    stockRequested, stockAvailable ));
        this.phoneId = phoneId;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
    }

    public Long getPhoneId() {
        return phoneId;
    }

    public Long getStockRequested() {
        return stockRequested;
    }

    public Long getStockAvailable() {
        return stockAvailable;
    }
}