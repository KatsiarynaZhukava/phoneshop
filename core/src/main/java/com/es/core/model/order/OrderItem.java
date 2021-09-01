package com.es.core.model.order;

import com.es.core.model.phone.Phone;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Phone phone;
    private Order order;
    private Long quantity;
    private BigDecimal purchaseTimePrice;

    public OrderItem() { }

    public OrderItem( final Long id,
                      final Phone phone,
                      final Order order,
                      final Long quantity,
                      final BigDecimal purchaseTimePrice ) {
        this.id = id;
        this.phone = phone;
        this.order = order;
        this.quantity = quantity;
        this.purchaseTimePrice = purchaseTimePrice;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(final Phone phone) {
        this.phone = phone;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(final Order order) {
        this.order = order;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchaseTimePrice() {
        return purchaseTimePrice;
    }

    public void setPurchaseTimePrice(BigDecimal purchaseTimePrice) {
        this.purchaseTimePrice = purchaseTimePrice;
    }
}