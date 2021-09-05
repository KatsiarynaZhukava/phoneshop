package com.es.core.dao;

import com.es.core.TestUtils;
import com.es.core.model.phone.Color;
import com.es.core.model.phone.Phone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcPhoneDaoIntTest {
    private final int NUMBER_OF_PHONES_IN_TEST_DB = 5;
    private final String COUNT_PHONE2COLOR_ROWS_QUERY = "select count(*) from phone2color";
    private final String COUNT_PHONES_ROWS_QUERY = "select count(*) from phones";

    @Resource
    private JdbcPhoneDao phoneDao;
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
    public void testGetExistingPhone() {
        Optional<Phone> phoneFromDb = phoneDao.get(2000L);
        Phone expected = initializePhone();
        assertTrue(phoneFromDb.isPresent());
        TestUtils.assertPhonesEquality(expected, phoneFromDb.get());
    }

    @Test
    public void testGetNonexistentPhone() {
        assertFalse(phoneDao.get(-1L).isPresent());
    }

    @Test
    public void testSavePhoneWithExistingId() {
        Long phoneId = 2000L;
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(phoneId);
        phoneToSave.setBrand("Xiaomi");
        phoneToSave.setModel("Xiaomi Redmi Note 10");
        phoneToSave.getColors().add(new Color(1001L, "White"));

        int oldPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave);
        int newPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        Optional<Phone> phoneFromDb = phoneDao.get(phoneId);

        assertTrue(phoneFromDb.isPresent());
        TestUtils.assertPhonesEquality(phoneToSave, phoneFromDb.get());
        assertEquals(oldPhone2ColorRecordsNumber + 1, newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSaveNewPhone() {
        Long phoneId = 3000L;
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(phoneId);
        phoneToSave.setBrand("Xiaomi");
        phoneToSave.setModel("Xiaomi Redmi Note 10");

        int oldPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave);
        int newPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        Optional<Phone> phoneFromDb = phoneDao.get(phoneId);

        assertTrue(phoneFromDb.isPresent());
        TestUtils.assertPhonesEquality(phoneToSave, phoneFromDb.get());
        assertEquals(oldPhone2ColorRecordsNumber + phoneToSave.getColors().size(), newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSaveExistingPhone() {
        Long phoneId = 2000L;
        Optional<Phone> phoneToSave = phoneDao.get(phoneId);
        assertTrue(phoneToSave.isPresent());

        int oldPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave.get());
        int newPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        Optional<Phone> phoneAfterSaving = phoneDao.get(phoneId);

        assertTrue(phoneAfterSaving.isPresent());
        TestUtils.assertPhonesEquality(phoneToSave.get(), phoneAfterSaving.get());
        assertEquals(oldPhone2ColorRecordsNumber, newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSavePhoneWithNullIdMultipleTimes() {
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(null);
        phoneToSave.setBrand("Xiaomi");
        phoneToSave.setModel("Xiaomi Redmi Note 10");

        int oldPhonesRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONES_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave);

        phoneToSave.setModel("Xiaomi Redmi 9C");
        phoneToSave.setId(null);
        phoneDao.save(phoneToSave);
        int newPhonesRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONES_ROWS_QUERY, Integer.class);
        assertEquals(oldPhonesRecordsNumber + 2, newPhonesRecordsNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSavePhoneWithExistingBrandModel() {
        Long phoneId = 2042L;
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(phoneId);
        phoneDao.save(phoneToSave);
    }

    @Test
    public void testFindAll() {
        assertEquals(phoneDao.findAll(0, Integer.MAX_VALUE).size(), NUMBER_OF_PHONES_IN_TEST_DB - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllNegativeOffset() {
        phoneDao.findAll(-1, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllNegativeLimit() {
        phoneDao.findAll(0, -1);
    }

    @Test
    public void testFindAllOffsetEqualsNumberOfPhones() {
        assertEquals(phoneDao.findAll(NUMBER_OF_PHONES_IN_TEST_DB, Integer.MAX_VALUE).size(), 0);
    }

    @Test
    public void testFindAllQueryNullOrderByBrandDesc() {
        List<Phone> phones = phoneDao.findAll(null, "brand", "desc", 0, Integer.MAX_VALUE);
        assertEquals(4, phones.size());
        assertEquals(2003L, phones.get(0).getId().longValue());
        long id = phones.get(1).getId();
        assertTrue(id == 2000L || id == 2001L || id == 2002L);
        id = phones.get(2).getId();
        assertTrue(id == 2000L || id == 2001L || id == 2002L);
        id = phones.get(3).getId();
        assertTrue(id == 2000L || id == 2001L || id == 2002L);
    }

    @Test
    public void testFindAllQueryNullOrderByModelAsc() {
        List<Phone> phonesAscendingOrder = phoneDao.findAll(null, "model", "asc", 0, Integer.MAX_VALUE);
        assertEquals(4, phonesAscendingOrder.size());
        assertEquals(2000L, phonesAscendingOrder.get(0).getId().longValue());
        assertEquals(2002L, phonesAscendingOrder.get(1).getId().longValue());
        assertEquals(2001L, phonesAscendingOrder.get(2).getId().longValue());
        assertEquals(2003L, phonesAscendingOrder.get(3).getId().longValue());
        List<Phone> phonesDefaultSortOrder = phoneDao.findAll(null, "model", null, 0, Integer.MAX_VALUE);
        assertEquals(4, phonesDefaultSortOrder.size());
        for (int i = 0; i < phonesAscendingOrder.size(); i++) {
            assertEquals(phonesAscendingOrder.get(i).getId().longValue(),
                         phonesDefaultSortOrder.get(i).getId().longValue());
        }
    }

    @Test
    public void testFindAllQueryNullOrderByPriceDesc() {
        List<Phone> phones = phoneDao.findAll(null, "price", "desc", 0, Integer.MAX_VALUE);
        assertEquals(4, phones.size());
        assertEquals(2003L, phones.get(0).getId().longValue());
        assertEquals(2001L, phones.get(1).getId().longValue());
        assertEquals(2002L, phones.get(2).getId().longValue());
        assertEquals(2000L, phones.get(3).getId().longValue());
    }

    @Test
    public void testFindAllQueryNullOrderByDisplaySizeAsc() {
        List<Phone> phones = phoneDao.findAll(null, "displaySizeInches", "asc", 0, Integer.MAX_VALUE);
        assertEquals(4, phones.size());
        assertEquals(2000L, phones.get(0).getId().longValue());
        assertEquals(2002L, phones.get(1).getId().longValue());
        assertEquals(2001L, phones.get(2).getId().longValue());
        assertEquals(2003L, phones.get(3).getId().longValue());
    }

    @Test
    public void testFindAllQueryNotNull() {
        List<Phone> phones = phoneDao.findAll("Alcatel", null, null, 0, Integer.MAX_VALUE);
        assertEquals(3, phones.size());
        assertEquals(2000L, phones.get(0).getId().longValue());
        assertEquals(2001L, phones.get(1).getId().longValue());
        assertEquals(2002L, phones.get(2).getId().longValue());
    }

    @Test
    public void testFindAllNonexistentPhones() {
        List<Long> phoneIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        List<Phone> phones = phoneDao.findAll(phoneIds);
        assertEquals(0L, phones.size());
    }

    @Test
    public void testFindAllExistingPhones() {
        List<Long> phoneIds = new ArrayList<>(Arrays.asList(2001L, 2002L, 2003L));
        List<Phone> phones = phoneDao.findAll(phoneIds);
        assertEquals(3L, phones.size());
        assertEquals(2001L, phones.get(0).getId().longValue());
        assertEquals(2002L, phones.get(1).getId().longValue());
        assertEquals(2003L, phones.get(2).getId().longValue());
    }

    @Test
    public void testCheckExistingPhoneExistence() { assertTrue(phoneDao.exists(2000L)); }

    @Test
    public void testCheckNonexistentPhoneExistence() { assertFalse(phoneDao.exists(-1L)); }

    @Test
    public void testGetTotalNumber() {
        assertEquals(4, phoneDao.getTotalNumber());
    }

    @Test
    public void testGetTotalNumberSearchQueryNull() {
        assertEquals(4, phoneDao.getTotalNumber(null));
    }

    @Test
    public void testGetTotalNumberSearchQueryEmpty() {
        assertEquals(4, phoneDao.getTotalNumber(""));
    }

    @Test
    public void testGetTotalNumberWithSearchQuery() {
        assertEquals(3, phoneDao.getTotalNumber("Alcatel"));
    }

    private Phone initializePhone() {
        Phone phone = new Phone();
        phone.setId(2000L);
        phone.setBrand("Alcatel");
        phone.setModel("Alcatel OT-117");
        phone.setPrice(new BigDecimal("200.00"));
        phone.setDisplaySizeInches(new BigDecimal("1.3"));
        phone.setWeightGr(null);
        phone.setLengthMm(new BigDecimal("105.0"));
        phone.setWidthMm(new BigDecimal("46.0"));
        phone.setHeightMm(new BigDecimal("14.6"));
        phone.setImageUrl(null);
        phone.setAnnounced(null);
        phone.setDeviceType("Basic phone");
        phone.setOs(null);
        phone.setDisplayResolution("96 x  64");
        phone.setPixelDensity(87);
        phone.setDisplayTechnology("STN");
        phone.setBackCameraMegapixels(null);
        phone.setFrontCameraMegapixels(null);
        phone.setRamGb(null);
        phone.setInternalStorageGb(null);
        phone.setBatteryCapacityMah(500);
        phone.setTalkTimeHours(new BigDecimal("5.0"));
        phone.setStandByTimeHours(new BigDecimal("350.0"));
        phone.setBluetooth(null);
        phone.setPositioning(null);
        phone.setImageUrl("manufacturer/Alcatel/Alcatel OT-117.jpg");
        phone.setDescription("Alcatel OT-117 is a basic candybar phone with FM radio. It has 1.32\" STN display, organizer, games, calculator and chronometer.");

        Set<Color> colors = new HashSet<>();
        colors.add(new Color(1000L, "Black"));
        colors.add(new Color(1002L, "Yellow"));
        phone.setColors(colors);

        return phone;
    }
}