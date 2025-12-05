package com.demo.store.mgmt.tool.controllers;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.dto.UpdatePriceRequest;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // <-- Add this annotation to select the test resources config
public class ProductControllerTest {

    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity()) // Apply MVC Security Context
                .build();
        // Also clear the test DB here if needed
        productRepository.deleteAll();
    }

    // Test Case 1: Adding a product as an ADMIN user (authorized)
    @Test
    @WithMockUser(roles = "ADMIN")
    // User has the required "ADMIN" role
    void testAddProduct_AsAdmin_ShouldSucceedWith201() {
        AddProductRequest request = new AddProductRequest("Keyboard", BigDecimal.valueOf(75.0));

        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request) // Pass the DTO directly
                .exchange() // Perform the request
                .expectStatus().isCreated() // Assert HTTP status is 201 Created
                .expectHeader().contentType(MediaType.APPLICATION_JSON) // Assert JSON content type
                .expectBody() // Start asserting the response body content
                .jsonPath("$.name").isEqualTo("Keyboard"); // Use jsonPath for assertion
    }


    // Test Case 1: Adding a product as an USER user (not authorized)
    @Test
    @WithMockUser(roles = "USER")
    // User has the not authorized "USER" role
    void testAddProduct_AsUser_ShouldBeForbidden() {
        AddProductRequest request = new AddProductRequest("Keyboard", BigDecimal.valueOf(75.00));

        // The service layer is not mocked, but the security layer blocks the request
        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden(); // Expect 403 Forbidden
    }

    @Test
    @WithMockUser(roles = "ADMIN")
        // User has the required "ADMIN" role
    void testAddProduct_AsAdmin_ShouldFailWith400() {
        AddProductRequest request = new AddProductRequest("Keyboard", BigDecimal.valueOf(-75.0));

        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request) // Pass the DTO directly
                .exchange() // Perform the request
                .expectStatus().is4xxClientError();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddProduct_WithInvalidJsonPriceString_ShouldReturn400BadRequest() {
        String invalidJsonPayload = "{\"name\": \"Mouse3\", \"price\": \"invalid-string-value\"}";

        // Test the invalid type scenario:
        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJsonPayload) // Send the raw invalid JSON string
                .exchange()
                .expectStatus().isBadRequest() // Assert HTTP Status 400 Bad Request
                .expectBody()
                // Assert that the body contains the custom message from our GlobalExceptionHandler
                .jsonPath("$.message").isEqualTo("Malformed JSON or invalid data type for field.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddProduct_WithMalformedPriceString_ShouldReturn400BadRequest() {
        String malformedJsonPayload = "{\"name\": \"Mouse3\", \"price\": AAA}";

        // Test the malformed JSON scenario (AAA without quotes):
        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(malformedJsonPayload) // Send the raw invalid JSON string
                .exchange()
                .expectStatus().isBadRequest() // Assert HTTP Status 400 Bad Request
                .expectBody()
                .jsonPath("$.message").isEqualTo("Malformed JSON or invalid data type for field.");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllProducts() {
        productRepository.save(new Product(null, "Laptop", BigDecimal.valueOf(1200.00), 1L));
        productRepository.save(new Product(null, "Mouse", BigDecimal.valueOf(25.00), 1L));

        webTestClient.get().uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class) // Expect a list of Product objects back
                .hasSize(2)
                .value(products -> {
                    // AssertJ assertions on the resulting list
                    assertThat(products.get(0).getName()).isEqualTo("Laptop");
                });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePriceShouldSucceed() {
        // Pre-populate DB with an item
        Product existingProduct = productRepository.save(new Product(null, "Old Product", BigDecimal.valueOf(50.00), 1L));
        Long productId = existingProduct.getId();
        BigDecimal newPrice = BigDecimal.valueOf(99.99);

        UpdatePriceRequest requestDto = new UpdatePriceRequest(newPrice);

        webTestClient.put().uri("/api/v1/products/{id}", productId) // Use the cleaner URL
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto) // Send the DTO as the request body
                .exchange()
                .expectStatus().isOk() // Expecting 200 OK for an update
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice.doubleValue());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePriceShouldFailWith400ForInvalidPriceType() {
        // 1. Pre-populate DB with an item
        Product existingProduct = productRepository.save(new Product(null, "Old Product", BigDecimal.valueOf(50.00), null));
        Long productId = existingProduct.getId();

        // 2. Prepare an invalid JSON payload (a string value for a BigDecimal field)
        // We send a raw string here because we are intentionally testing the JSON parsing failure.
        String invalidJsonPayload = "{\"newPrice\": \"99A.99\"}";

        // 3. Perform the PUT request with the invalid JSON body
        webTestClient.put().uri("/api/v1/products/{id}", productId) // Use the cleaner URL
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJsonPayload) // Send the raw invalid JSON string
                .exchange()
                .expectStatus().isBadRequest() // Assert HTTP Status 400 Bad Request
                .expectBody()
                // Assert that the body contains the custom message from our GlobalExceptionHandler
                .jsonPath("$.message").isEqualTo("Malformed JSON or invalid data type for field.");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete() {
        // Pre-populate test DB with an item to delete
        Product itemToDelete = productRepository.save(new Product(null, "Temp Item", BigDecimal.valueOf(10.00), 1L));
        Long productId = itemToDelete.getId();

        webTestClient.delete().uri("/api/v1/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent() // Expect 204 No Content
                .expectBody().isEmpty(); // Assert body is empty

        // Verify it was actually deleted from the H2 DB
        assertThat(productRepository.findById(productId)).isEmpty();
    }
}
