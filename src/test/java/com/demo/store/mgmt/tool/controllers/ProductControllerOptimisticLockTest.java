package com.demo.store.mgmt.tool.controllers;

import com.demo.store.mgmt.tool.dto.UpdatePriceRequest;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductControllerOptimisticLockTest {

    private WebTestClient webTestClient;

    @Autowired
    private WebApplicationContext context;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        ProductService productService() {
            ProductService mock = mock(ProductService.class);
            when(mock.changeProductPrice(any(Long.class), any(BigDecimal.class)))
                    .thenThrow(new ObjectOptimisticLockingFailureException(Product.class, 1L));
            return mock;
        }
    }

    @BeforeEach
    void setUp() {
        this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePrice_OptimisticLock_Returns409() {
        UpdatePriceRequest request = new UpdatePriceRequest(BigDecimal.valueOf(99.99));

        webTestClient.put().uri("/api/v1/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}
