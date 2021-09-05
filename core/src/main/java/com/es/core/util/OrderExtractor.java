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

        Map<Long, Order> ordersMap = new HashMap<>();

        Map<Long, List<OrderItem>> phonesOrderItemsMap = new HashMap<>();

        Order order = null;
        while (resultSet.next()) {
            Long id = resultSet.getLong("orders.id");
            order = ordersMap.get(id);
            if (order == null) {
                order = orderBeanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
                order.setOrderItems(new ArrayList<>());
                order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
                ordersMap.put(id, order);
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(resultSet.getLong("phone2order.quantity"));
            orderItem.setPurchaseTimePrice(resultSet.getBigDecimal("phone2order.purchaseTimePrice"));
            order.getOrderItems().add(orderItem);

            Long phoneId = resultSet.getLong("phone2order.phoneId");
            if (!phonesOrderItemsMap.containsKey(phoneId)) {
                phonesOrderItemsMap.put(phoneId, new ArrayList<>());
            }
            phonesOrderItemsMap.get(phoneId).add(orderItem);
        }

        Map<Long, Phone> phones = phoneDao.findAll(new ArrayList<>(phonesOrderItemsMap.keySet()))
                                          .stream()
                                          .collect( Collectors.toMap( Phone::getId,
                                                                      Function.identity() ));
        for (Long phoneId : phonesOrderItemsMap.keySet()) {
            Phone phone = phones.get(phoneId);
            if (phone != null) {
                for (OrderItem orderItem: phonesOrderItemsMap.get(phoneId)) {
                    orderItem.setPhone(phone);
                }
            } else {
                throw new NotFoundException(PhoneShopMessages.PHONE_NOT_FOUND_BY_ID_MESSAGE, phoneId);
            }
        }
        return new ArrayList<>(ordersMap.values());
    }
}