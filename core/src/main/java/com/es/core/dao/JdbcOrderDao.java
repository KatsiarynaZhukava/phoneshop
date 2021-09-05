package com.es.core.dao;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.util.OrderExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Types;
import java.util.*;

@Component
public class JdbcOrderDao implements OrderDao {
    private static final String SELECT_ORDERS_QUERY = "select * from orders left join phone2order on orders.id = phone2order.orderId";
    private static final String SELECT_ORDER_BY_ID_QUERY = "select * from orders left join phone2order on orders.id = phone2order.orderId where id = ?";
    private static final String CHECK_ORDER_EXISTS_BY_ID_QUERY = "select 1 from orders where id = ?";
    private static final String DELETE_PHONE2ORDER_QUERY = "delete from phone2order where orderId = ?";
    private static final String INSERT_INTO_PHONE2ORDER_QUERY = "insert into phone2order (phoneId, orderId, quantity, purchaseTimePrice) values (?, ?, ?, ?)";
    private static final String INSERT_INTO_ORDERS_QUERY = "insert into orders (subtotal, deliveryPrice, totalPrice, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInfo, date, status, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ORDERS_QUERY = "update orders set subtotal = ?, deliveryPrice = ?, totalPrice = ?, firstName = ?, lastName = ?, deliveryAddress = ?, contactPhoneNo = ?, additionalInfo = ?, date = ?, status = ? where id = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private OrderExtractor orderExtractor;
    @Resource
    private SingleColumnRowMapper<Long> longSingleColumnRowMapper;
    private SimpleJdbcInsert simpleJdbcInsert;

    @PostConstruct
    private void initializeSimpleJdbcInsert() {
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("orders").usingGeneratedKeyColumns("id").compile();
    }

    @Override
    public Optional<Order> get( final Long key ) {
        List<Order> orders = jdbcTemplate.query(SELECT_ORDER_BY_ID_QUERY, new Object[]{ key }, orderExtractor);
        return orders == null ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Override
    public List<Order> findAll() {
        return jdbcTemplate.query(SELECT_ORDERS_QUERY, orderExtractor);
    }

    @Override
    public void save( final Order order ) {
        Object[] fields = new Object[] { order.getSubtotal(), order.getDeliveryPrice(), order.getTotalPrice(),
                                         order.getFirstName(), order.getLastName(), order.getDeliveryAddress(),
                                         order.getContactPhoneNo(), order.getAdditionalInfo(), order.getDate(),
                                         order.getStatus().toString(), order.getId() };
        Long id = order.getId();
        if (id != null) {
            if (exists(id)) {
                jdbcTemplate.update(UPDATE_ORDERS_QUERY, fields);
                jdbcTemplate.update(DELETE_PHONE2ORDER_QUERY, order.getId());
            } else {
                jdbcTemplate.update(INSERT_INTO_ORDERS_QUERY, fields);
            }
        } else {
            BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(order) {
                @Override
                public int getSqlType(String var) {
                    int sqlType = super.getSqlType(var);
                    if (sqlType == TYPE_UNKNOWN && hasValue(var)) {
                        if (getValue(var).getClass().isEnum()) {
                            sqlType = Types.VARCHAR;
                        }
                    }
                    return sqlType;
                }
            };
            id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();
            order.setId(id);
        }

        List<Object[]> batch = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Object[] values = new Object[]{ orderItem.getPhone().getId(), orderItem.getOrder().getId(), orderItem.getQuantity(), orderItem.getPurchaseTimePrice() };
            batch.add(values);
        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PHONE2ORDER_QUERY, batch);
    }

    @Override
    public boolean exists( long id ) {
        return !jdbcTemplate.query(CHECK_ORDER_EXISTS_BY_ID_QUERY, new Object[]{ id }, longSingleColumnRowMapper)
                .isEmpty();
    }
}