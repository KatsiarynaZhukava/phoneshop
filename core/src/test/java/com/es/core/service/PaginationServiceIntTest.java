package com.es.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaginationServiceIntTest {
    @Resource
    private PaginationService paginationService;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Before
    public void insertDataIntoDB() {
        jdbcTemplate.execute("insert into colors (id, code) values (1000, 'Black')");
        jdbcTemplate.execute("insert into colors (id, code) values (1001, 'White')");
        jdbcTemplate.execute("insert into colors (id, code) values (1002, 'Yellow')");

        jdbcTemplate.execute("insert into phones (id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values ('2000', 'Alcatel', 'Alcatel OT-117', 200.0, 1.3, null, 105.0, 46.0, 14.6, null, 'Basic phone', null, '96 x  64', 87, 'STN', null, null, null, null, 500, 5.0, 350.0, null, null, 'manufacturer/Alcatel/Alcatel OT-117.jpg', 'Alcatel OT-117 is a basic candybar phone with FM radio. It has 1.32\" STN display, organizer, games, calculator and chronometer.')");
        jdbcTemplate.execute("insert into phones (id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values ('2001', 'Alcatel', 'Alcatel OT-203A', 240.0, 1.5, 156, null, null, null, null, 'Basic phone', null, '128 x  128', 121, 'CSTN', null, null, null, null, 650, 7.0, 350.0, null, null, 'manufacturer/Alcatel/Alcatel OT-203A.jpg', 'Alcatel OT-203a is a simple candybar phone with FM Radio, 65K color CSTN display and SMS messaging.')");
        jdbcTemplate.execute("insert into phones (id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values ('2002', 'Alcatel', 'Alcatel OT-202A', 230.0, 1.4, 156, 98.0, 42.5, 18.5, null, 'Feature phone', null, '96 x  64', 89, 'CSTN', null, null, null, null, 720, 5.0, 250.0, null, null, 'manufacturer/Alcatel/Alcatel OT-202A.jpg', 'The Alcatel OT-202A is a basic phone with a 1.3-inch color display, 8-tone polyphonic ringtones, messaging and alarms.')");
        jdbcTemplate.execute("insert into phones (id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values ('2003', 'Samsung', 'Samsung SGH-J770', 710.0, 2.1, 156, 98.5, 48.6, 14.8, null, null, null, '176 x  220', 134, 'TFT', 2.0, null, null, 0.016, 880, 7.16, 370.0, '2.0', null, 'manufacturer/Samsung/Samsung SGH-J770.jpg', 'Samsung SGH-J770 is a tri-band GSM slider, featuring 262k color TFT display, 2-megapixel camera, stereo Bluetooth, Music player, FM Radio and microSD slot for memory.')");
        jdbcTemplate.execute("insert into phones (id, brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description) values ('2004', 'Lenovo', 'Lenovo A Plus', null, 4.5, 156, 133.0, 66.0, 9.9, '2016-09-03 00:00:00', 'Smart phone', 'Android (5.1)', '480 x  854', 218, null, 5.0, 2.0, 1.0, 8.0, 2000, 12.0, 384.0, '4.0', 'GPS, A-GPS', 'manufacturer/Lenovo/Lenovo A Plus.jpg', 'The Lenovo A Plus offers standard low-end fare. Its 4.5-inch display chugs out a very lowly 480 x 854 resolution, while MediaTek''s 1.3 GHz MT6580 chip along with one gig of RAM is about as frugal as it gets. The 8 GB storage isn''t much to work with either, but thankfully, Lenovo has gone to the trouble of including microSD support. The 2000 mAh battery is quite small when compared even with moderately-stocked smartphones on the market.')");

        jdbcTemplate.execute("insert into phone2color (phoneId, colorId) values (2000, 1000)");
        jdbcTemplate.execute("insert into phone2color (phoneId, colorId) values (2000, 1002)");
        jdbcTemplate.execute("insert into phone2color (phoneId, colorId) values (2001, 1000)");
        jdbcTemplate.execute("insert into phone2color (phoneId, colorId) values (2001, 1001)");
        jdbcTemplate.execute("insert into phone2color (phoneId, colorId) values (2001, 1002)");

        jdbcTemplate.execute("insert into stocks (phoneId, stock, reserved) values (2000, 10, 0)");
        jdbcTemplate.execute("insert into stocks (phoneId, stock, reserved) values (2001, 9, 7)");
        jdbcTemplate.execute("insert into stocks (phoneId, stock, reserved) values (2002, 28, 6)");
        jdbcTemplate.execute("insert into stocks (phoneId, stock, reserved) values (2003, 29, 7)");
        jdbcTemplate.execute("insert into stocks (phoneId, stock, reserved) values (2004, 0, 0)");
    }

    @Test
    public void testGetOffset() {
        assertEquals(0L, paginationService.getOffset(10, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetNegativeLimit() {
        paginationService.getOffset(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetZeroLimit() {
        paginationService.getOffset(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetNegativeCurrentPage() {
        paginationService.getOffset(10, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOffsetZeroCurrentPage() {
        assertEquals(0L, paginationService.getOffset(10, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalPagesNumberNegativeLimit() {
        paginationService.getTotalPagesNumber(-1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalPagesNumberZeroLimit() {
        paginationService.getTotalPagesNumber(0, null);
    }

    @Test
    public void testGetTotalPagesNumberWithQueryFound() {
        assertEquals(1L, paginationService.getTotalPagesNumber(10, "Lenovo"));
        assertEquals(2L, paginationService.getTotalPagesNumber(2, "Alcatel"));
    }

    @Test
    public void testGetTotalPagesNumberWithQueryNotFound() {
        assertEquals(1L, paginationService.getTotalPagesNumber(1, "Blabla"));
    }
}