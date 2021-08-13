package com.es.core.model.phone;

public class Stock {
    private Phone phone;
    private Long stock;
    private Long reserved;

    public Stock() { }

    public Stock( final Phone phone, final Long stock, final Long reserved ) {
        this.phone = phone;
        this.stock = stock;
        this.reserved = reserved;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Long getReserved() {
        return reserved;
    }

    public void setReserved(Long reserved) {
        this.reserved = reserved;
    }
}
