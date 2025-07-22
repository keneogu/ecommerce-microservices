package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  @Autowired
  private ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
    return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
    return productService.getProductById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id,
      @RequestBody ProductRequest productRequest){
    return new ResponseEntity<ProductResponse>(productService.updateProduct(id, productRequest),
        HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    return ResponseEntity.ok(productService.fetchAllProducts());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(
      @PathVariable Long id){
    boolean deleted = productService.deleteProduct(id);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
    return ResponseEntity.ok(productService.searchProducts(keyword));
  }

}
