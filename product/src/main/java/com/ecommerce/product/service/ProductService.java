package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

  @Autowired
  ProductRepository productRepository;

  public ProductResponse createProduct(ProductRequest productRequest) {
    Product product = new Product();
    updateProductFromRequest(product, productRequest);
    Product savedProduct = productRepository.save(product);
    return mapToProductResponse(savedProduct);
  }

  public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
    return productRepository.findById(id).map(existingProduct -> {
      updateProductFromRequest(existingProduct, productRequest);
      Product savedProduct = productRepository.save(existingProduct);
      return mapToProductResponse(savedProduct);
    }).orElseThrow(() -> new RuntimeException("Product Not Found" + id));
  }

  private void updateProductFromRequest(Product product, ProductRequest productRequest) {
    product.setName(productRequest.getName());
    product.setDescription(productRequest.getDescription());
    product.setPrice(productRequest.getPrice());
    product.setStockQuantity(productRequest.getStockQuantity());
    product.setCategory(productRequest.getCategory());
    product.setImageUrl(productRequest.getImageUrl());
  }

  private ProductResponse mapToProductResponse(Product product) {
    ProductResponse response = new ProductResponse();
    response.setId(String.valueOf(product.getId()));
    response.setName(product.getName());
    response.setDescription(product.getDescription());
    response.setCategory(product.getCategory());
    response.setPrice(product.getPrice());
    response.setStockQuantity(product.getStockQuantity());
    response.setImageUrl(product.getImageUrl());
    response.setActive(product.getActive());
    return response;
  }

  public List<ProductResponse> fetchAllProducts() {
    return productRepository.findByActiveTrue().stream()
        .map(this::mapToProductResponse)
        .collect(Collectors.toList());
  }

  public boolean deleteProduct(Long id) {
    return productRepository.findById(id).map(product -> {
      product.setActive(false);
      productRepository.save(product);
      return true;
    }).orElse(false);
  }

  public List<ProductResponse> searchProducts(String keyword) {
    return productRepository.searchProducts(keyword)
        .stream()
        .map(this::mapToProductResponse)
        .collect(Collectors.toList());
  }

  public Optional<ProductResponse> getProductById(String id) {
    return productRepository.findByIdAndActiveTrue(Long.valueOf(id))
        .map(this::mapToProductResponse);
//    Product product = productRepository.findById(Long.valueOf(id))
//        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
//
//    return mapToProductResponse(product);
  }
}
