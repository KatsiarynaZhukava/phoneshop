package com.es.phoneshop.web.controller.dto.output;

import java.math.BigDecimal;

public class CartOutputDto {
    private Long totalQuantity;
    private BigDecimal totalCost;

    public CartOutputDto() {}

    public CartOutputDto( final Long totalQuantity, final BigDecimal totalCost ) {
        this.totalQuantity = totalQuantity;
        this.totalCost = totalCost;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}