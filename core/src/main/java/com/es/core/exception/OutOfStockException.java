package com.es.core.exception;

import com.es.core.model.phone.Phone;

public class OutOfStockException extends Exception {
    private final Phone phone;
    private final long stockRequested;
    private final long quantityOfItemsInCart;
    private final long stockAvailable;

    public OutOfStockException( final Phone phone,
                                final long stockRequested,
                                final long stockAvailable,
                                final long quantityOfItemsInCart ) {
        this.phone = phone;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
        this.quantityOfItemsInCart = quantityOfItemsInCart;
    }

    public Phone getPhone() {
        return phone;
    }

    public long getStockRequested() {
        return stockRequested;
    }

    public long getStockAvailable() {
        return stockAvailable;
    }

    public long getQuantityOfItemsInCart() {
        return quantityOfItemsInCart;
    }
}
