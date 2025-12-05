# demo-store-mgmt-tool
Spring Boot 4 Store Management Tool Demo
# Store Management API (v1)

A simple, secure RESTful API for managing a product catalog using Spring Boot 4, an H2 in-memory database, and HTTP Basic Authentication.

## Features

*   **Platform:** Built with Spring Boot 4.0 and requires **Java 25+**.
*   **Performance:** Uses **Java Virtual Threads** (`spring.threads.virtual.enabled=true`) for high concurrency and efficient I/O operations.  
*   **CRUD Operations:** Add, find, update price, delete products following RESTful conventions.
*   **Search Functionality:** Filter products by name or get total count.
*   **Security:** HTTP Basic Authentication with role-based access control (`USER` and `ADMIN` roles).
*   **Data Storage:** H2 in-memory database for local development. 
*   **Error Handling:** Global exception handling for consistent API responses (HTTP 409, 400, 404, 403).
*   **Validation:** Jakarta Bean Validation for input integrity.
*   **API Versioning:** All endpoints are prefixed with `/api/v1/`.
*   **Concurrency Control:** Implements **Optimistic Locking** using `@Version` fields to safely manage simultaneous updates (returns HTTP 409 Conflict on mismatch).
*   **Transaction Management:** Configured service layer with `@Transactional` (including `readOnly` optimizations) for data integrity.
*   **RESTful Design:** Uses standard REST conventions (PUT requests with body data, clean URLs, `/search` and `/count` endpoints).

## Technologies Used


*   **Java:** JDK 25+
*   **Framework:** Spring Boot 4.0, Spring Security 7.0, Spring Framework 7.0
*   **Build Tool:** Maven
*   **Database:** H2 In-Memory Database
*   **Authentication:** Spring Security (HTTP Basic Auth)
*   **Validation:** Jakarta Bean Validation
*   **Testing:** JUnit 5, WebTestClient, Spring Security Test
*   **Utility:** Lombok

## Getting Started

### Prerequisites

*   JDK 17 or higher installed.
*   Maven installed (optional if using the included `mvnw` wrapper).

### Installation

1.  **Clone the repository:**
    ```bash
    git clone github.com
    cd demo-store-mgmt-tool-springboot4
    ```

2.  **Build the project:**
    ```bash
    ./mvnw clean package
    ```

### Running the Application

You can run the application directly using the Spring Boot Maven plugin:


```bash
./mvnw spring-boot:run
```

The application will start on http://localhost:8080.

### API Documentation and Endpoints

The API is secured using HTTP Basic Authentication. 
Use the following default credentials (loaded from src/main/resources/data.sql):

```bash
Username	Password	Role(s)
user	password	ROLE_USER
admin	adminpass	ROLE_ADMIN, ROLE_USER
```

Endpoints Overview
```bash
HTTP Method 	Endpoint	                            Description	                Required Role
POST	        /api/v1/products	                    Add a new product	        ADMIN
GET	            /api/v1/products	                    List all products	        USER, ADMIN
GET	            /api/v1/products/{id}	                Get product by ID	        USER, ADMIN
GET	            /api/v1/products/count	          Get the total count of products	USER, ADMIN
DELETE	        /api/v1/products/{id}	                Delete a product	        ADMIN
PUT	            /api/v1/products/{id}	                Update a product's price 	ADMIN
                                                        (body required: 
                                                        {"newPrice": ...})
GET	            /api/v1/products/search?name={string}	Search products by name 	USER, ADMIN                                            	
                                                        containing {string}	                                            	
```
Example Usage (using curl)

1. Add a Product (ADMIN)
```bash
curl -X POST --user admin:adminpass -H "Content-Type: application/json" -d '{"name": "Laptop", "price": 1200.00}' http://localhost:8080/api/v1/products
```
2. Update a Product Price (ADMIN)
```bash
curl -X PUT --user admin:adminpass -H "Content-Type: application/json" -d '{"newPrice": 999.99}' http://localhost:8080/api/v1/products/1
```
3. Get All Products (USER)
```bash
curl --user user:password http://localhost:8080/api/v1/products
```
4. Search Products by Name (USER)
```bash
curl --user user:password "http://localhost:8080/api/v1/products/search?name=Mouse"
```
5.Get Product Count (USER)
```bash
curl --user user:password http://localhost:8080/api/v1/products/count
```

Running Tests

Tests are configured to run against a separate H2 in-memory database instance to ensure isolation from the main application.
bash

```bash
./mvnw clean test
```
