package com.es.core.dao;

import com.es.core.model.phone.Color;
import com.es.core.model.phone.Phone;
import com.es.core.util.PhoneWithColorsExtractor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component
public class JdbcPhoneDao implements PhoneDao {
    private static final String SELECT_PHONES_QUERY = "select * from phones left join phone2color on phones.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id";
    private static final String SELECT_PHONES_COLORS_WITH_OFFSET_LIMIT = "select * from ( select * from phones offset ? limit ?) ph left join phone2color on ph.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id";
    private static final String SELECT_PHONE_BY_ID_QUERY = "select 1 from phones where id = ?";
    private static final String DELETE_PHONE2COLOR_QUERY = "delete from phone2color where phoneId = ?";
    private static final String INSERT_INTO_PHONE2COLOR_QUERY = "insert into phone2color (phoneId, colorId) values (?, ?)";
    private static final String INSERT_INTO_PHONES_QUERY = "insert into phones (brand, model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PHONES_QUERY = "update phones set brand = ?, model = ?, price = ?, displaySizeInches = ?, weightGr = ?, lengthMm = ?, widthMm = ?, heightMm = ?, announced = ?, deviceType = ?, os = ?, displayResolution = ?, pixelDensity = ?, displayTechnology = ?, backCameraMegapixels = ?, frontCameraMegapixels = ?, ramGb = ?, internalStorageGb = ?, batteryCapacityMah = ?, talkTimeHours = ?, standByTimeHours = ?, bluetooth = ?, positioning = ?, imageUrl = ?, description = ? where id = ?";
    private static final String COUNT_PHONES_QUERY_PART = "select count(*) from phones ";
    private static final String POSITIVE_STOCK_NOT_NULL_PRICE_QUERY_PART = "where (id in (select phoneId from stocks where stock > 0)) and (price is not null)";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Resource
    private PhoneWithColorsExtractor phoneExtractor;
    @Resource
    private SingleColumnRowMapper<Long> longSingleColumnRowMapper;
    private SimpleJdbcInsert simpleJdbcInsert;

    @PostConstruct
    private void initializeSimpleJdbcInsert() {
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("phones").usingGeneratedKeyColumns("id").compile();
    }

    @Override
    public Optional<Phone> get(final Long key) {
        List<Phone> phones = jdbcTemplate.query(SELECT_PHONES_QUERY + " where phones.id = ?",
                                                new Object[]{key}, phoneExtractor);
        return phones.isEmpty() ? Optional.empty() : Optional.of(phones.get(0));
    }

    @Override
    @Transactional
    public void save(final Phone phone) {
        Object[] fields = new Object[]{phone.getBrand(), phone.getModel(), phone.getPrice(), phone.getDisplaySizeInches(),
                phone.getWeightGr(), phone.getLengthMm(), phone.getWidthMm(), phone.getHeightMm(),
                phone.getAnnounced(), phone.getDeviceType(), phone.getOs(), phone.getDisplayResolution(),
                phone.getPixelDensity(), phone.getDisplayTechnology(), phone.getBackCameraMegapixels(),
                phone.getFrontCameraMegapixels(), phone.getRamGb(), phone.getInternalStorageGb(),
                phone.getBatteryCapacityMah(), phone.getTalkTimeHours(), phone.getStandByTimeHours(),
                phone.getBluetooth(), phone.getPositioning(), phone.getImageUrl(), phone.getDescription(), phone.getId()};
        try {
            Long id = phone.getId();
            if (id != null) {
                if (exists(id)) {
                    jdbcTemplate.update(UPDATE_PHONES_QUERY, fields);
                    jdbcTemplate.update(DELETE_PHONE2COLOR_QUERY, phone.getId());
                } else {
                    jdbcTemplate.update(INSERT_INTO_PHONES_QUERY, fields);
                }
            } else {
                id = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(phone)).longValue();
                phone.setId(id);
            }
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Illegal argument: a phone with such a brand and model value combination already exists");
        }

        List<Object[]> batch = new ArrayList<>();
        for (Color color : phone.getColors()) {
            Object[] values = new Object[]{phone.getId(), color.getId()};
            batch.add(values);
        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PHONE2COLOR_QUERY, batch);
    }

    @Override
    public List<Phone> findAll(long offset, long limit) {
        validateOffsetLimit(offset, limit);
        return jdbcTemplate.query(SELECT_PHONES_COLORS_WITH_OFFSET_LIMIT,
                new Object[]{offset, limit}, phoneExtractor);
    }

    @Override
    public List<Phone> findAll(final String searchQuery, final String sortField,
                               final String sortOrder, long offset, long limit) {
        validateOffsetLimit(offset, limit);
        String sqlQuery = formSqlQuery(searchQuery, sortField, sortOrder);
        Map<String, Object> sqlParameters = fillSqlParameters(searchQuery, sortField, offset, limit);
        return namedParameterJdbcTemplate.query(sqlQuery, sqlParameters, phoneExtractor);
    }

    @Override
    public List<Phone> findAll(final List<Long> phoneIds) {
        StringBuilder sqlQuery = new StringBuilder(SELECT_PHONES_QUERY + " where phones.id in ( ");
        for (int i = 0; i < phoneIds.size(); i++) {
            sqlQuery.append("?,");
        }
        sqlQuery.replace(sqlQuery.length() - 1, sqlQuery.length(), ")");
        return jdbcTemplate.query(sqlQuery.toString(), phoneIds.toArray(), phoneExtractor);
    }

    @Override
    public boolean exists(Long id) {
        return !jdbcTemplate.query(SELECT_PHONE_BY_ID_QUERY, new Object[]{id}, longSingleColumnRowMapper)
                .isEmpty();
    }

    @Override
    public long getTotalNumber() {
        return jdbcTemplate.queryForObject(COUNT_PHONES_QUERY_PART + POSITIVE_STOCK_NOT_NULL_PRICE_QUERY_PART, Long.TYPE);
    }

    @Override
    public long getTotalNumber(final String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return getTotalNumber();
        } else {
            String sqlQuery = COUNT_PHONES_QUERY_PART + POSITIVE_STOCK_NOT_NULL_PRICE_QUERY_PART + " and (lower(brand) like lower(:searchQuery) or lower(model) like lower(:searchQuery))";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("searchQuery", searchQuery);
            return namedParameterJdbcTemplate.queryForObject(sqlQuery, parameters, Long.TYPE);
        }
    }

    private void validateOffsetLimit(long offset, long limit) {
        if (offset < 0) throw new IllegalArgumentException("Offset must be >= 0");
        if (limit <= 0) throw new IllegalArgumentException("Limit must be > 0");
    }

    private String formSqlQuery(final String searchQuery, final String sortField, final String sortOrder) {
        StringBuilder sqlQuery = new StringBuilder("select * from (select * from phones " + POSITIVE_STOCK_NOT_NULL_PRICE_QUERY_PART);
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sqlQuery.append(" and (");
            String[] keywords = searchQuery.toLowerCase().trim().split("[ ]+");
            for (int i = 0; i < keywords.length; i++) {
                sqlQuery.append("lower(brand) like lower(:keyword").append(i).append(") or lower(model) like lower(:keyword").append(i).append(") or ");
            }
            sqlQuery.replace(sqlQuery.length() - 3, sqlQuery.length(), ")");
        }
        if (sortField != null && !sortField.isEmpty()) {
            sqlQuery.append(" order by ")
                    .append(sortField)
                    .append(" ")
                    .append((sortOrder != null && !sortOrder.isEmpty()) ? sortOrder : "asc");
        }
        sqlQuery.append(" offset :offset limit :limit) ph left join phone2color on ph.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id");
        return sqlQuery.toString();
    }

    private Map<String, Object> fillSqlParameters(final String searchQuery, final String sortField,
                                                  long offset, long limit) {
        Map<String, Object> parameters = new HashMap<>();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String[] keywords = searchQuery.toLowerCase().trim().split("[ ]+");
            for (int i = 0; i < keywords.length; i++) {
                parameters.put("keyword" + i, "%" + keywords[i] + "%");
            }
        }
        parameters.put("sortField", sortField);
        parameters.put("offset", offset);
        parameters.put("limit", limit);
        return parameters;
    }
}