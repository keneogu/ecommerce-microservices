package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  @Autowired
  CartService cartService;

  @PostMapping
  public ResponseEntity<String> addCart(
      @RequestHeader("X-User-ID") String userId, @RequestBody CartItemRequest request) {
    if (!cartService.addToCart(userId, request)){
      return ResponseEntity.badRequest().body("Product Out of Stock");
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/items/{productId}")
  public ResponseEntity<Void> removeFromCart(
      @RequestHeader("X-User-ID") String userId,
      @PathVariable String productId
  ) {
    boolean deleted = cartService.deleteItemFromCart(userId, productId);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<List<CartItem>> getCart(@RequestHeader("X-User-ID") String userId) {
    return ResponseEntity.ok(cartService.getCart(userId));
  }
}
