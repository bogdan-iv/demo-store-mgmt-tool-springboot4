package com.demo.store.mgmt.tool.services;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.exception.ProductNotFoundException;
import com.demo.store.mgmt.tool.exception.ProductValidationException;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    // 2. Mock: We create a mock version of the repository dependency.
    // We control its behavior using Mockito's 'when().thenReturn()'.
    @Mock
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(BigDecimal.valueOf(1200.00));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(BigDecimal.valueOf(25.00));
    }


    @Test
    public void testAddProduct() {
        // Define the behavior of the mock repository when save() is called
        // We ensure it returns the product that was passed in, simulating a successful DB save
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        AddProductRequest productRequest1 = new AddProductRequest(product1.getName(), product1.getPrice());

        // Call the service method we are testing
        Product created = productService.addProduct(productRequest1);

        // Assertions using AssertJ
        assertThat(created.getName()).isEqualTo("Laptop");
        assertThat(created.getPrice()).isEqualTo(BigDecimal.valueOf(1200.00));

        // Verify that the save method was called exactly once on the mock repository
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testFindAllProducts() {
        // Define the behavior for finding all products
        List<Product> productList = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productList);

        // Call the service method
        List<Product> result = productService.findAllProducts();

        // Assertions
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");

        // Verify that findAll was called once
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testFindProductByNameContaining() {
        List<Product> productList = Arrays.asList(product2);
        when(productRepository.findByNameContaining("Mou")).thenReturn(productList);

        List<Product> result = productService.findProductsByNameContaining("Mou");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mouse");

        // Verify that findProductsByNameContaining was called once
        verify(productRepository, times(1)).findByNameContaining("Mou");
    }

    @Test
    public void testFindProductByName() {
        when(productRepository.findByName("Mouse")).thenReturn(Optional.of(product2));
        Optional<Product> result = productService.findProductByName("Mouse");

        assertThat(result.get().getName()).isEqualTo("Mouse");
        // Verify that findByName was called once
        verify(productRepository, times(1)).findByName("Mouse");
    }

    @Test
    public void testChangePrice_Success() {
        BigDecimal newPrice = BigDecimal.valueOf(1250.00);

        // Define behavior for finding the existing product
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product1));
        // Define behavior for saving the updated product
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Call the service method
        Product updatedProduct = productService.changeProductPrice(1L, newPrice);

        // Assertions
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);

        // Verify interactions
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1);
    }
    @Test
    public void testChangeNegativePrice_ThrowsException() {
        BigDecimal newPrice = BigDecimal.valueOf(-1250.00);
        ProductValidationException thrown = Assertions.assertThrows(
                ProductValidationException.class, // The expected exception type
                () -> {
                    // The code that should throw the exception
                    productService.changeProductPrice(1L, newPrice);
                }
        );
        assertThat(thrown.getMessage()).contains("New price must be greater than zero");
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void testChangePrice_NotFound_ThrowsException() {
        Long nonExistentId = 99L;

        // 1. Arrange the mock behavior: return empty when the specific ID is looked up
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 2. Act and Assert using assertThrows()
        // We expect a ProductNotFoundException to be thrown when the lambda is executed
        ProductNotFoundException thrown = Assertions.assertThrows(
                ProductNotFoundException.class, // The expected exception type
                () -> {
                    // The code that should throw the exception
                    productService.changeProductPrice(nonExistentId, BigDecimal.valueOf(500.00));
                }
        );

        // 3. Optional: Further assertions on the caught exception object itself
        assertThat(thrown.getMessage()).contains("Product not found with ID: 99");

        // Verify that the save method was NOT called, as the flow was interrupted by the exception
        verify(productRepository, times(0)).save(any(Product.class));
    }
}
