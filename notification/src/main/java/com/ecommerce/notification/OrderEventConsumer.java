package com.ecommerce.notification;

import com.ecommerce.notification.payload.OrderCreatedEvent;
import com.ecommerce.notification.payload.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {

  @RabbitListener(queues = "${rabbitmq.queue.name}")
  public void handleOrderEvent(OrderCreatedEvent event) {
    System.out.println("Received Order Event: " + event);

    long orderId = event.getOrderId();
    OrderStatus orderStatus = event.getStatus();

    System.out.println("OrderId: " + orderId);
    System.out.println("Order Status: " + orderStatus);

    // Update database
    // Send notification
    // Send Emails
    // Generate Invoice
    // Send Seller notification
  }
}
