package com.es.core.model.phone;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JdbcPhoneDao implements PhoneDao {
    private static final String SELECT_PHONES_QUERY = "select * from phones left join phone2color on phones.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id";
    private static final String SELECT_PHONES_COLORS_WITH_OFFSET_LIMIT = "select * from ( select * from phones offset ? limit ?) ph left join phone2color on ph.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id";
    private static final String SELECT_PHONE_BY_ID_QUERY = "select 1 from phones where id = ?";
    private static final String DELETE_PHONE2COLOR_QUERY = "delete from phone2color where phoneId = ?";
    private static final String INSERT_INTO_PHONE2COLOR_QUERY = "insert into phone2color (phoneId, colorId) values (?, ?)";
    private static final String INSERT_INTO_PHONES_QUERY = "insert into phones (brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PHONES_QUERY = "update phones set brand = ?, model = ?, price = ?, displaySizeInches = ?, weightGr = ?, lengthMm = ?, widthMm = ?, heightMm = ?, announced = ?, deviceType = ?, os = ?, displayResolution = ?, pixelDensity = ?, displayTechnology = ?, backCameraMegapixels = ?, frontCameraMegapixels = ?, ramGb = ?, internalStorageGb = ?, batteryCapacityMah = ?, talkTimeHours = ?, standByTimeHours = ?, bluetooth = ?, positioning = ?, imageUrl = ?, description = ? where id = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private SimpleJdbcInsert simpleJdbcInsert;
    @Resource
    private PhoneWithColorsExtractor phoneExtractor;
    @Resource
    private SingleColumnRowMapper<Long> longSingleColumnRowMapper;

    public Optional<Phone> get( final Long key ) {
        List<Phone> phones = jdbcTemplate.query(SELECT_PHONES_QUERY + " where phones.id = ?",
                                                    new Object[] { key }, phoneExtractor);
        return phones.isEmpty() ? Optional.empty() : Optional.of(phones.get(0));
    }

    @Transactional
    public void save( final Phone phone ) {
        Object[] fields = new Object[] { phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getDisplaySizeInches(),
                                         phone.getWeightGr(), phone.getLengthMm(), phone.getWidthMm(), phone.getHeightMm(),
                                         phone.getAnnounced(), phone.getDeviceType(), phone.getOs(), phone.getDisplayResolution(),
                                         phone.getPixelDensity(), phone.getDisplayTechnology(), phone.getBackCameraMegapixels(),
                                         phone.getFrontCameraMegapixels(), phone.getRamGb(), phone.getInternalStorageGb(),
                                         phone.getBatteryCapacityMah(), phone.getTalkTimeHours(), phone.getStandByTimeHours(),
                                         phone.getBluetooth(), phone.getPositioning(), phone.getImageUrl(), phone.getDescription(), phone.getId() };
        try {
            if (exists(phone.getId())) {
                jdbcTemplate.update(UPDATE_PHONES_QUERY, fields);
                jdbcTemplate.update(DELETE_PHONE2COLOR_QUERY, phone.getId());
            } else {
                if (phone.getId() != null) {
                    jdbcTemplate.update(INSERT_INTO_PHONES_QUERY, fields);
                } else {
                    simpleJdbcInsert.withTableName("phones").usingGeneratedKeyColumns("id");
                    Number id = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(phone));
                    phone.setId(id.longValue());
                }
            }
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Illegal argument: a phone with such a brand and model value combination already exists");
        }

        List<Object[]> batch = new ArrayList<>();
        for (Color color: phone.getColors()) {
            Object[] values = new Object[] { phone.getId(), color.getId() };
            batch.add(values);
        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PHONE2COLOR_QUERY, batch);
    }

    public List<Phone> findAll( int offset, int limit ) {
        if (offset < 0) throw new IllegalArgumentException("Offset must be >= 0");
        if (limit < 0) throw new IllegalArgumentException("Limit must be >= 0");
        return jdbcTemplate.query(SELECT_PHONES_COLORS_WITH_OFFSET_LIMIT,
                                  new Object[] { offset, limit }, phoneExtractor);
    }

    private boolean exists( Long id ) {
        return !jdbcTemplate.query(SELECT_PHONE_BY_ID_QUERY, new Object[] { id }, longSingleColumnRowMapper)
                            .isEmpty();
    }
}