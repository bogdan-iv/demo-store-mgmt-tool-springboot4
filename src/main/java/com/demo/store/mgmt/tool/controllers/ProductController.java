package com.demo.store.mgmt.tool.controllers;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.dto.ProductResponse;
import com.demo.store.mgmt.tool.dto.UpdatePriceRequest;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid AddProductRequest productRequest) {
        Product savedProduct = productService.addProduct(productRequest);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> findAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> findProducts(@RequestParam String name) {
        logger.info("Searching products by name: {}", name);
        List<Product> products = productService.findProductsByNameContaining(name);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> changeProductPrice(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePriceRequest priceRequest) {
        logger.info("Updating price for product ID: {} to {}", id, priceRequest.newPrice());

        // Pass the value from the DTO record to the service
        Product updatedProduct = productService.changeProductPrice(id, priceRequest.newPrice());

        logger.info("Price updated successfully for product ID: {}", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        logger.info("Product deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getProductCount() {
        logger.info("Fetching product count");
        long count = productService.countProducts();
        return ResponseEntity.ok(count);
    }

}
