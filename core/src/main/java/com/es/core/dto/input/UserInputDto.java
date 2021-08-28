package com.es.core.dto.input;

import com.es.core.util.PhoneShopMessages;
import org.hibernate.validator.constraints.NotBlank;

public class UserInputDto {
    @NotBlank(message = PhoneShopMessages.REQUIRED_MESSAGE)
    private String firstName;
    @NotBlank(message = PhoneShopMessages.REQUIRED_MESSAGE)
    private String lastName;
    @NotBlank(message = PhoneShopMessages.REQUIRED_MESSAGE)
    private String deliveryAddress;
    @NotBlank(message = PhoneShopMessages.REQUIRED_MESSAGE)
    private String contactPhoneNo;
    private String additionalInfo;

    public UserInputDto() { }

    public UserInputDto( final String firstName,
                         final String lastName,
                         final String deliveryAddress,
                         final String contactPhoneNo,
                         final String additionalInfo ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.deliveryAddress = deliveryAddress;
        this.contactPhoneNo = contactPhoneNo;
        this.additionalInfo = additionalInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}