package com.demo.store.mgmt.tool.services;

import com.demo.store.mgmt.tool.controllers.ProductController;
import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.exception.ConcurrencyConflictException;
import com.demo.store.mgmt.tool.exception.ProductNotFoundException;
import com.demo.store.mgmt.tool.exception.ProductValidationException;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(AddProductRequest productRequest) {
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setPrice(productRequest.price());
        return productRepository.save(newProduct);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findProductsByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    public Product changeProductPrice(Long id, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductValidationException("New price must be greater than zero");
        }
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) {
                throw new ProductNotFoundException(id);
            }
            Product product = productOpt.get();
            product.setPrice(newPrice);
            return productRepository.save(product);
        } catch (OptimisticLockException ex) {
            logger.error(ex.getMessage());
            // Log the error and rethrow as a domain-specific exception if needed
            throw new ConcurrencyConflictException("Conflict occurred updating product " + id);
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public long countProducts() {
        return productRepository.count();
    }
}
