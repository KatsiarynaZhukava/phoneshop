package com.es.core.model.phone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JdbcPhoneDao implements PhoneDao {
    private static final String SELECT_PHONES_WITH_COLORS_QUERY = "select * from phones " +
                                                                  "left join phone2color on phones.id = phone2color.phoneId " +
                                                                  "left join colors on phone2color.colorId = colors.id";
    private static final String DELETE_PHONE2COLOR_QUERY = "delete * from phone2color where phoneId = ?";
    private static final String INSERT_INTO_PHONE2COLOR_QUERY = "insert into phone2color (phoneId, colorId) values (?, ?)";
    private static final String INSERT_INTO_PHONES_QUERY = "insert into phones (brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PHONES_QUERY = "update phones set brand = ?, model = ?, price = ?, displaySizeInches = ?, weightGr = ?, lengthMm = ?, widthMm = ?, heightMm = ?, announced = ?, deviceType = ?, os = ?, displayResolution = ?, pixelDensity = ?, displayTechnology = ?, backCameraMegapixels = ?, frontCameraMegapixels = ?, ramGb = ?, internalStorageGb = ?, batteryCapacityMah = ?, talkTimeHours = ?, standByTimeHours = ?, bluetooth = ?, positioning = ?, imageUrl = ?, description = ? where id = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;

    public Optional<Phone> get( final Long key ) {
        List<Phone> phones = jdbcTemplate.query(SELECT_PHONES_WITH_COLORS_QUERY + " WHERE phones.id = ?", new Object[] { key }, new PhoneExtractor());
        return phones.stream().findFirst();
    }

    public void save( final Phone phone ) {
        Object[] fields = new Object[] { phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getDisplaySizeInches(),
                                         phone.getWeightGr(), phone.getLengthMm(), phone.getWidthMm(), phone.getHeightMm(),
                                         phone.getAnnounced(), phone.getDeviceType(), phone.getOs(), phone.getDisplayResolution(),
                                         phone.getPixelDensity(), phone.getDisplayTechnology(), phone.getBackCameraMegapixels(),
                                         phone.getFrontCameraMegapixels(), phone.getRamGb(), phone.getInternalStorageGb(),
                                         phone.getBatteryCapacityMah(), phone.getTalkTimeHours(), phone.getStandByTimeHours(),
                                         phone.getBluetooth(), phone.getPositioning(), phone.getImageUrl(), phone.getDescription(), phone.getId() };
        Optional<Phone> foundPhone = get(phone.getId());
        if (foundPhone.isPresent()) {
            jdbcTemplate.update(UPDATE_PHONES_QUERY, fields);
            jdbcTemplate.update(DELETE_PHONE2COLOR_QUERY, phone.getId());
        } else {
            jdbcTemplate.update(INSERT_INTO_PHONES_QUERY, fields);
        }
        for (Color color: phone.getColors()) {
            jdbcTemplate.update(INSERT_INTO_PHONE2COLOR_QUERY, phone.getId(), color.getId());
        }
    }

    public List<Phone> findAll( int offset, int limit ) {
        List<Phone> phones = jdbcTemplate.query(SELECT_PHONES_WITH_COLORS_QUERY + " OFFSET ? LIMIT ?", new Object[] { offset, limit }, new PhoneExtractor());
        return phones.stream()
                     .filter(phone -> phone.getId() != null)
                     .collect(Collectors.toList());
    }
}