package com.es.core;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import org.junit.Assert;

import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;


public class TestUtils {
    public static void assertPhonesEquality( final Phone expected, final Phone actual ) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBrand(), actual.getBrand());
        assertEquals(expected.getModel(), actual.getModel());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getDisplaySizeInches(), actual.getDisplaySizeInches());
        assertEquals(expected.getWeightGr(), actual.getWeightGr());
        assertEquals(expected.getLengthMm(), actual.getLengthMm());
        assertEquals(expected.getWidthMm(), actual.getWidthMm());
        assertEquals(expected.getHeightMm(), actual.getHeightMm());
        assertEquals(expected.getAnnounced(), actual.getAnnounced());
        assertEquals(expected.getDeviceType(), actual.getDeviceType());
        assertEquals(expected.getOs(), actual.getOs());
        assertEquals(expected.getColors(), actual.getColors());
        assertEquals(expected.getDisplayResolution(), actual.getDisplayResolution());
        assertEquals(expected.getPixelDensity(), actual.getPixelDensity());
        assertEquals(expected.getDisplayTechnology(), actual.getDisplayTechnology());
        assertEquals(expected.getBackCameraMegapixels(), actual.getBackCameraMegapixels());
        assertEquals(expected.getFrontCameraMegapixels(), actual.getFrontCameraMegapixels());
        assertEquals(expected.getRamGb(), actual.getRamGb());
        assertEquals(expected.getInternalStorageGb(), actual.getInternalStorageGb());
        assertEquals(expected.getBatteryCapacityMah(), actual.getBatteryCapacityMah());
        assertEquals(expected.getTalkTimeHours(), actual.getTalkTimeHours());
        assertEquals(expected.getStandByTimeHours(), actual.getStandByTimeHours());
        assertEquals(expected.getBluetooth(), actual.getBluetooth());
        assertEquals(expected.getPositioning(), actual.getPositioning());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    public static void assertStocksEquality(final Stock expected, final Stock actual) {
        Assert.assertEquals(expected.getPhone().getId(), actual.getPhone().getId());
        Assert.assertEquals(expected.getStock(), actual.getStock());
        Assert.assertEquals(expected.getReserved(), actual.getReserved());
    }

    public static void assertOrdersEquality(final Order expected, final Order actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSecureId(), actual.getSecureId());
        assertEquals(expected.getSubtotal(), actual.getSubtotal());
        assertEquals(expected.getDeliveryPrice(), actual.getDeliveryPrice());
        assertEquals(expected.getTotalPrice(), actual.getTotalPrice());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDeliveryAddress(), actual.getDeliveryAddress());
        assertEquals(expected.getContactPhoneNo(), actual.getContactPhoneNo());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOrderItems().size(), actual.getOrderItems().size());

        Iterator<OrderItem> actualOrderItemsIterator = actual.getOrderItems().iterator();
        for (OrderItem expectedOrderItem: expected.getOrderItems()) {
            OrderItem actualOrderItem = actualOrderItemsIterator.next();
            assertPhonesEquality(expectedOrderItem.getPhone(), actualOrderItem.getPhone());
            assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
        }
    }
}
