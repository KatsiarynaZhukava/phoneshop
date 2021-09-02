package com.es.core.service;

import com.es.core.dao.OrderDao;
import com.es.core.dao.PhoneDao;
import com.es.core.dao.StockDao;
import com.es.core.dto.input.UserInputDto;
import com.es.core.exception.NotFoundException;
import com.es.core.exception.OutOfStockException;
import com.es.core.exception.OutOfStockItem;
import com.es.core.model.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
import com.es.core.util.PhoneShopMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderDao orderDao;
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private StockDao stockDao;
    @Resource
    private CartService cartService;
    @Value("${delivery.price}")
    private BigDecimal deliveryPrice;

    @Override
    public Order createOrder( final Cart cart ) {
        Map<Long, Long> cartItems = new HashMap<>(cart.getItems());
        Map<Long, Phone> phones = phoneDao.findAll(new ArrayList<>(cartItems.keySet()))
                                          .stream()
                                          .collect(Collectors.toMap(Phone::getId, Function.identity()));
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        for(Map.Entry<Long, Long> cartItem: cartItems.entrySet()) {
            Phone phone = phones.get(cartItem.getKey());
            if (phone != null) {
                orderItems.add(new OrderItem(null, phone, order, cartItem.getValue(), phone.getPrice()));
            } else {
                throw new NotFoundException(PhoneShopMessages.PHONE_NOT_FOUND_BY_ID_MESSAGE, cartItem.getKey());
            }
        }
        order.setOrderItems(orderItems);
        order.setSubtotal(orderItems.stream()
                                    .map(orderItem -> new BigDecimal(orderItem.getQuantity()).multiply(orderItem.getPurchaseTimePrice()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    @Override
    public Order createOrder( final Cart cart,
                              final UserInputDto userInputDto ) {
        Order order = createOrder(cart);
        fillUserInfo(order, userInputDto);
        return order;
    }

    @Override
    public void fillUserInfo( final Order order,
                              final UserInputDto userInputDto ) {
        order.setFirstName(userInputDto.getFirstName());
        order.setLastName(userInputDto.getLastName());
        order.setDeliveryAddress(userInputDto.getDeliveryAddress());
        order.setContactPhoneNo(userInputDto.getContactPhoneNo());
        order.setAdditionalInfo(userInputDto.getAdditionalInfo());
    }

    @Override
    @Transactional
    public void placeOrder( final Order order ) throws OutOfStockException {
        List<Long> phoneIds = order.getOrderItems().stream()
                                                   .map(orderItem -> orderItem.getPhone().getId())
                                                   .collect(Collectors.toList());
        Map<Long, OutOfStockItem> outOfStockItems = getOutOfStockItems(order, phoneIds);
        if (outOfStockItems.isEmpty()) {
            Map<Long, Long> requestedStocks = order.getOrderItems()
                                                   .stream()
                                                   .collect( Collectors.toMap( orderItem -> orderItem.getPhone().getId(),
                                                                               OrderItem::getQuantity ));
            orderDao.save(order);
            try {
                stockDao.update(requestedStocks);
            } catch (DataIntegrityViolationException e) {
                outOfStockItems = getOutOfStockItems(order, phoneIds);
                removeOutOfStockItemsFromCart(outOfStockItems);
                throw new OutOfStockException(new ArrayList<>(outOfStockItems.values()));
            }
        } else {
            removeOutOfStockItemsFromCart(outOfStockItems);
            throw new OutOfStockException(new ArrayList<>(outOfStockItems.values()));
        }
    }

    private Map<Long, OutOfStockItem> getOutOfStockItems( final Order order, final List<Long> phoneIds ) {
        Map<Long, Stock> stocks = stockDao.findAll(phoneIds)
                                          .stream()
                                          .collect( Collectors.toMap( stock -> stock.getPhone().getId(),
                                                                      Function.identity() ));
        Map<Long, OutOfStockItem> outOfStockItems = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Long phoneId = orderItem.getPhone().getId();
            Long stockRequested = orderItem.getQuantity();

            if (stocks.containsKey(phoneId)) {
                Long stockAvailable = stocks.get(phoneId).getStock();
                if (stockAvailable < stockRequested) {
                    outOfStockItems.put(phoneId, new OutOfStockItem(phoneId, stockRequested, stockAvailable));
                }
            } else {
                outOfStockItems.put(phoneId, new OutOfStockItem(phoneId, stockRequested, 0L));
            }
        }
        return outOfStockItems;
    }

    private void removeOutOfStockItemsFromCart( final Map<Long, OutOfStockItem> outOfStockItems ) {
        for (OutOfStockItem outOfStockItem : outOfStockItems.values()) {
            cartService.remove(outOfStockItem.getPhoneId());
        }
    }
}