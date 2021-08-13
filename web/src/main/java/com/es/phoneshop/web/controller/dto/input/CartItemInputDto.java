package com.es.phoneshop.web.controller.dto.input;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CartItemInputDto {
    @NotNull(message = "Phone id should not be null")
    private Long phoneId;
    @NotNull(message = "Quantity should not be null")
    @Min(value = 1 , message = "Quantity should be > 0")

    private Long requestedQuantity;

    public CartItemInputDto() { }

    public CartItemInputDto( final Long phoneId,
                             final Long requestedQuantity ) {
        this.phoneId = phoneId;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public Long getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Long requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }
}