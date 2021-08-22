package com.es.phoneshop.web.dto.input;

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

    @Override
    public int hashCode() {
        int result = 42;
        if (phoneId != null) {
            result = 31 * result + phoneId.hashCode();
        }
        if (requestedQuantity != null) {
            result = 31 * result + requestedQuantity.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CartItemInputDto))
            return false;
        final CartItemInputDto other = (CartItemInputDto) obj;
        boolean phoneIdEquals = (this.phoneId == null && other.phoneId == null)
                || (this.phoneId != null && this.phoneId.equals(other.phoneId));
        boolean requestedQuantityEquals = (this.requestedQuantity == null && other.requestedQuantity == null)
                || (this.requestedQuantity != null && this.requestedQuantity.equals(other.requestedQuantity));
        return phoneIdEquals && requestedQuantityEquals;
    }
}