# AGENTS.md — demo-store-mgmt-tool-springboot4

## Build & run

```bash
mvn clean package             # build
mvn spring-boot:run           # dev server on :8080
mvn clean test                # run all tests (uses test profile DB)
docker build -t store .       # multi-stage Docker build
```

Requires **JDK 25+** (pom.xml `java.version=25`, Spring Boot 4.0 mandates it).  
System `mvn` 3.8.7+ works. The `mvnw` wrapper is not set up (`.mvn/` dir missing).

## Authentication (HTTP Basic)

| User  | Password   | Roles             |
|-------|------------|-------------------|
| user  | password   | ROLE_USER         |
| admin | adminpass  | ROLE_ADMIN, USER  |

Seeded via `src/main/resources/data.sql`. BCrypt-hashed passwords.

## API

All endpoints under `/api/v1/products`.  
`POST/PUT/DELETE` require `ADMIN`. `GET`/search/count require `USER`.  
`PUT /{id}` body: `{"newPrice": <number>}`.  
Optimistic locking via `@Version` — 409 Conflict on stale updates.

## Tests

- **Controller tests** (`ProductControllerTest`): `@SpringBootTest(webEnvironment = RANDOM_PORT)`, use `WebTestClient` built from `MockMvcWebTestClient` with `springSecurity()`. Use `@WithMockUser(roles = ...)` for auth. **DB cleared** in `@BeforeEach`.
- **Service tests** (`ProductServiceTest`): pure `Mockito` + `@ExtendWith(MockitoExtension.class)`, no Spring context.
- **Active profile**: `@ActiveProfiles("test")` — loads `src/test/resources/application-test.properties` + `test-data.sql`.
- DTO `@Min(0)` allows price `0`, but `ProductService.addProduct` and `changeProductPrice` throw if `<= 0`.

## Architecture

Single-module Maven. Root package `com.demo.store.mgmt.tool`.  
Entrypoint: `ToolApplication.java`.  
Layers: `controllers/` → `services/` → `repositories/` → JPA `models/`.  
Lombok used (`@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor`). No `@Data`.  
Virtual threads enabled (`spring.threads.virtual.enabled=true`).  
H2 console at `/h2-console` (permit-all). CSRF/CORS disabled.
