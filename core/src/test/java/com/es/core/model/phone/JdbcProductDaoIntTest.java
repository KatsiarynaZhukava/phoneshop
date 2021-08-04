package com.es.core.model.phone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:context/applicationContext-core-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcProductDaoIntTest {
    private final int NUMBER_OF_PHONES_IN_TEST_DB = 3;
    private final String COUNT_PHONE2COLOR_ROWS_QUERY = "select count(*) from phone2color";
    private final String COUNT_PHONES_ROWS_QUERY = "select count(*) from phones";

    @Autowired
    private PhoneDao phoneDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testGetExistingPhone() {
        Optional<Phone> phoneFromDb = phoneDao.get(1000L);
        Phone expected = initializePhone();
        assertTrue(phoneFromDb.isPresent());
        assertPhonesEquality(expected, phoneFromDb.get());
    }

    @Test
    public void testGetNonexistentPhone() {
        assertFalse(phoneDao.get(-1L).isPresent());
    }

    @Test
    public void testSavePhoneWithExistingId() {
        Long phoneId = 1000L;
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
        assertPhonesEquality(phoneToSave, phoneFromDb.get());
        assertEquals(oldPhone2ColorRecordsNumber + 1, newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSaveNewPhone() {
        Long phoneId = 1003L;
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(phoneId);
        phoneToSave.setBrand("Xiaomi");
        phoneToSave.setModel("Xiaomi Redmi Note 10");

        int oldPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave);
        int newPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        Optional<Phone> phoneFromDb = phoneDao.get(phoneId);

        assertTrue(phoneFromDb.isPresent());
        assertPhonesEquality(phoneToSave, phoneFromDb.get());
        assertEquals(oldPhone2ColorRecordsNumber + phoneToSave.getColors().size(), newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSaveExistingPhone() {
        Long phoneId = 1000L;
        Optional<Phone> phoneToSave = phoneDao.get(phoneId);
        assertTrue(phoneToSave.isPresent());

        int oldPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave.get());
        int newPhone2ColorRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONE2COLOR_ROWS_QUERY, Integer.class);
        Optional<Phone> phoneAfterSaving = phoneDao.get(phoneId);

        assertTrue(phoneAfterSaving.isPresent());
        assertPhonesEquality(phoneToSave.get(), phoneAfterSaving.get());
        assertEquals(oldPhone2ColorRecordsNumber, newPhone2ColorRecordsNumber);
    }

    @Test
    public void testSavePhoneWithNullId() {
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(null);
        phoneToSave.setBrand("Xiaomi");
        phoneToSave.setModel("Xiaomi Redmi Note 10");

        int oldPhonesRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONES_ROWS_QUERY, Integer.class);
        phoneDao.save(phoneToSave);
        int newPhonesRecordsNumber = jdbcTemplate.queryForObject(COUNT_PHONES_ROWS_QUERY, Integer.class);
        assertEquals(oldPhonesRecordsNumber + 1, newPhonesRecordsNumber);
    }

    @Test(expected = InvalidParameterException.class)
    public void testSavePhoneWithExistingBrandModel() {
        Long phoneId = 1042L;
        Phone phoneToSave = initializePhone();
        phoneToSave.setId(phoneId);
        phoneDao.save(phoneToSave);
    }

    @Test
    public void testFindAll() {
        assertEquals(phoneDao.findAll(0, Integer.MAX_VALUE).size(), NUMBER_OF_PHONES_IN_TEST_DB);
    }

    @Test(expected = InvalidParameterException.class)
    public void testFindAllNegativeOffset() {
        phoneDao.findAll(-1, Integer.MAX_VALUE);
    }

    @Test(expected = InvalidParameterException.class)
    public void testFindAllNegativeLimit() {
        phoneDao.findAll(0, -1);
    }

    @Test
    public void testFindAllOffsetEqualsNumberOfPhones() {
        assertEquals(phoneDao.findAll(NUMBER_OF_PHONES_IN_TEST_DB, Integer.MAX_VALUE).size(), 0);
    }

    private void assertPhonesEquality( final Phone expected, final Phone actual ) {
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

    private Phone initializePhone() {
        Phone phone = new Phone();
        phone.setId(1000L);
        phone.setBrand("Alcatel");
        phone.setModel("Alcatel OT-117");
        phone.setPrice(null);
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