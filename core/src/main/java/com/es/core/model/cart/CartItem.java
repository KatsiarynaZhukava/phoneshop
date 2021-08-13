package com.es.core.model.cart;

import com.es.core.model.phone.Phone;

public class CartItem {
    private final Phone phone;
    private long quantity;

    public CartItem( final Phone phone, long quantity ) {
        this.phone = phone;
        this.quantity = quantity;
    }

    public Phone getPhone() {
        return phone;
    }

    public synchronized long getQuantity() {
        return quantity;
    }

    public synchronized void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
