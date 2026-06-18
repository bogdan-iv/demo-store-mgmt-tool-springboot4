package com.demo.store.mgmt.tool.services;

import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceOptimisticLockTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void testChangePrice_OptimisticLock_ThrowsException() {
        Product product = new Product(1L, "Laptop", BigDecimal.valueOf(1200.00), null);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenThrow(new OptimisticLockException("version mismatch"));

        assertThrows(OptimisticLockException.class,
                () -> productService.changeProductPrice(1L, BigDecimal.valueOf(99)));
    }
}
