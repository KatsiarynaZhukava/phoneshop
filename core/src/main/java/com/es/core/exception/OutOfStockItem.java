package com.es.core.exception;

public class OutOfStockItem {
    private final Long phoneId;
    private final Long stockRequested;
    private final Long stockAvailable;

    public OutOfStockItem( final Long phoneId,
                           final Long stockRequested,
                           final Long stockAvailable ) {
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
