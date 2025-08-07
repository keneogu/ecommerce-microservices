package com.ecommerce.notification;

import com.ecommerce.notification.payload.OrderCreatedEvent;
import com.ecommerce.notification.payload.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {

  @Bean
  public Consumer<OrderCreatedEvent> orderCreated() {
    return event -> {
      log.info("Received order created event for order: {}", event.getOrderId());
      log.info("Received order created event for user id: {}", event.getUserId());
    };
  }


//  @RabbitListener(queues = "${rabbitmq.queue.name}")
//  public void handleOrderEvent(OrderCreatedEvent event) {
//    System.out.println("Received Order Event: " + event);
//
//    long orderId = event.getOrderId();
//    OrderStatus orderStatus = event.getStatus();
//
//    System.out.println("OrderId: " + orderId);
//    System.out.println("Order Status: " + orderStatus);
//
//    // Update database
//    // Send notification
//    // Send Emails
//    // Generate Invoice
//    // Send Seller notification
//  }
}
