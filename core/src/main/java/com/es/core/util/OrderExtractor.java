package com.es.core.util;

import com.es.core.dao.PhoneDao;
import com.es.core.exception.NotFoundException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.phone.Phone;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderExtractor implements ResultSetExtractor<List<Order>> {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private BeanPropertyRowMapper<Order> orderBeanPropertyRowMapper;

    @Override
    public List<Order> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        if (!resultSet.isBeforeFirst()) return null;

        Order order = null;
        List<OrderItem> orderItems = new ArrayList<>();
        Map<Long, Long> phoneQuantities = new HashMap<>();
        Map<Long, BigDecimal> phonePrices = new HashMap<>();
        while (resultSet.next()) {
            if (order == null) {
                order = orderBeanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
                order.setOrderItems(orderItems);
                order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
            }
            Long phoneId = resultSet.getLong("phone2order.phoneId");
            phoneQuantities.put(phoneId, resultSet.getLong("phone2order.quantity"));
            phonePrices.put(phoneId, resultSet.getBigDecimal("phone2order.purchaseTimePrice"));
        }
        Map<Long, Phone> phones = phoneDao.findAll(new ArrayList<>(phoneQuantities.keySet()))
                                          .stream()
                                          .collect( Collectors.toMap( Phone::getId,
                                                                      Function.identity() ));
        for (Long phoneId : phoneQuantities.keySet()) {
            Phone phone = phones.get(phoneId);
            if (phone != null) {
                orderItems.add(new OrderItem(null, phone, order, phoneQuantities.get(phoneId), phonePrices.get(phoneId)));
            } else {
                throw new NotFoundException(PhoneShopMessages.PHONE_NOT_FOUND_BY_ID_MESSAGE, phoneId);
            }
        }
        return Collections.singletonList(order);
    }
}