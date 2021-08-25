package com.es.core.dto.output;

import java.math.BigDecimal;

public class CartTotalsOutputDto {
    private Long totalQuantity;
    private BigDecimal totalCost;

    public CartTotalsOutputDto() {}

    public CartTotalsOutputDto(final Long totalQuantity, final BigDecimal totalCost ) {
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