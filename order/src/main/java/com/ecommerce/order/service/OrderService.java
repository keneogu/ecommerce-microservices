package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderCreatedEvent;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
  @Autowired
  CartService cartService;

  @Autowired
  OrderRepository orderRepository;

  private final StreamBridge streamBridge;

//  private final RabbitTemplate rabbitTemplate;
//
//  @Value("${rabbitmq.exchange.name}")
//  private String exchangeName;
//
//  @Value("${rabbitmq.routing.key}")
//  private String routingKey;

  public Optional<OrderResponse> createOrder(String userId) {
    List<CartItem> cartItems = cartService.getCart(userId);
    if (cartItems.isEmpty()){
      return Optional.empty();
    }

//    Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
//    if (userOpt.isEmpty()) {
//      return Optional.empty();
//    }
//    User user = userOpt.get();

    BigDecimal totalPrice = cartItems.stream()
        .map(CartItem::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order();
    order.setUserId(userId);
    order.setStatus(OrderStatus.CONFIRMED);
    order.setTotalAmount(totalPrice);

    List<OrderItem> orderItems = cartItems.stream()
        .map(item -> new OrderItem(
            null,
            item.getProductId(),
            item.getQuantity(),
            item.getPrice(),
            order
        )).toList();

    order.setItems(orderItems);
    Order savedOrder = orderRepository.save(order);
    cartService.clearCart(userId);

    // Publish order created event
    OrderCreatedEvent event = new OrderCreatedEvent(
        savedOrder.getId(),
        savedOrder.getUserId(),
        savedOrder.getStatus(),
        mapToOrderItemDTOs(savedOrder.getItems()),
        savedOrder.getTotalAmount(),
        savedOrder.getCreatedAt()
    );
//    rabbitTemplate.convertAndSend(exchangeName,
//        routingKey,
//        event);
    streamBridge.send("createOrder-out-0", event);

    return Optional.of(mapToOrderResponse(savedOrder));
  }

  private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items) {
    return items.stream()
        .map(item -> new OrderItemDTO(
            item.getId(),
            item.getProductId(),
            item.getQuantity(),
            item.getPrice(),
            item.getPrice().multiply(new BigDecimal(item.getQuantity()))
        )).collect(Collectors.toList());
  }

  private OrderResponse mapToOrderResponse(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getTotalAmount(),
        order.getStatus(),
        order.getItems().stream()
            .map(orderItem -> new OrderItemDTO(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getPrice().multiply(
                    new BigDecimal(orderItem.getQuantity())
                )
            )).toList(),
        order.getCreatedAt()
    );
  }
}
