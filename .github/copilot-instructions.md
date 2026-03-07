# Copilot Instructions for MyAiCodeProjectPlanet

## Quick Start

This is a **Spring Boot 3.5.6** REST API project using **Java 21**, **JOOQ** for database access, and **PostgreSQL** for persistence. It includes OpenAPI schema-driven development.

## Build, Test, and Run

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
mvn spring-boot:run
```
The application starts on `http://localhost:8080` with PostgreSQL automatically started via Docker Compose integration.

### Run all tests
```bash
mvn test
```

### Run a single test
```bash
mvn test -Dtest=CustomersControllerTest
```

### Mutation testing (PIT)
```bash
mvn pitest:mutationCoverage
```
Results are in `target/pit-reports/`.

### Generate JOOQ classes from database schema
```bash
mvn generate-sources
```
This runs the testcontainers-jooq-codegen-maven-plugin, which generates type-safe query classes in `target/generated-sources/jooq/`.

## Architecture

### Layered Structure
The application follows a **classic 3-tier architecture**:

- **Controller Layer** (`controller/`): REST endpoints implementing OpenAPI-generated interfaces
- **Service Layer** (`service/`): Business logic with `CustomerService` interface and `CustomerServiceImpl` implementation
- **Repository Layer** (`repository/`): Data access using JOOQ's DSL for type-safe queries
- **Model Layer** (`model/`): Domain objects (e.g., `Customer`)

### API-First Development
The API is defined in `src/main/resources/static/customers.yaml` (OpenAPI spec). The OpenAPI Maven plugin auto-generates service interfaces in `com.mydeveloperplanet.myaicodeprojectplanet.openapi`. Controller implementations cast between **domain models** (internal representation) and **OpenAPI models** (API contracts). This separation isolates API changes from business logic.

### Database Access Pattern
- Uses **JOOQ** for type-safe SQL queries (not JPA/Hibernate)
- Generated JOOQ classes from schema located in `com.mydeveloperplanet.myaicodeprojectplanet.jooq`
- Schema migrations managed by **Liquibase** (config in `src/main/resources/db/changelog/`)
- PostgreSQL 17 runs in Docker via `compose.yaml` with Spring Boot's docker-compose support

### Key Dependencies
- `spring-boot-starter-web`: REST endpoints and Spring MVC
- `spring-boot-starter-jooq`: JOOQ integration
- `spring-boot-docker-compose`: Auto-starts PostgreSQL container
- `testcontainers-jooq-codegen-maven-plugin`: Generates JOOQ classes during build
- `openapi-generator-maven-plugin`: Generates API interfaces from YAML spec
- `pitest-maven`: Mutation testing for code quality validation

## Key Conventions

### Model Conversion Pattern
Controllers convert between two model layers:
- **Domain models** (`Customer` in `model/`): Core business objects
- **OpenAPI models** (`com.mydeveloperplanet.myaicodeprojectplanet.openapi.model.Customer`): API-specific DTOs

This is done explicitly in controller methods using `convertToOpenAPIModel()` and `convertToDomainModel()` helpers. Maintain this separation when adding new endpoints.

### Service Layer Usage
- All business logic resides in service implementations
- Controllers inject services via `@Autowired` (not constructor injection yet)
- Services return domain models; controllers handle API model conversion

### Repository Method Signatures
Repository methods return domain models, not JOOQ records. Internal mapping is done via `convertToCustomer()`. This keeps JOOQ types hidden from upper layers.

### Testing
- Unit tests are in `src/test/java/` mirroring source structure
- Use `@SpringBootTest` for integration tests requiring Spring context
- Consider Testcontainers for database integration tests (already a dependency)

## Common Tasks

### Adding a New Endpoint
1. Update `src/main/resources/static/customers.yaml` with the new operation
2. Run `mvn generate-sources` to regenerate OpenAPI interfaces
3. Implement the new method in `CustomersController`
4. Add business logic to `CustomerServiceImpl`
5. Extend `CustomerRepository` if new database queries are needed
6. Write tests in `CustomersControllerTest`

### Adding a New Domain Entity
1. Create domain model class in `model/`
2. Add schema changes to Liquibase changelog (if database entity)
3. Create repository class in `repository/` for data access
4. Create service interface and implementation in `service/`
5. Create controller in `controller/`
6. Add OpenAPI spec to the YAML file and regenerate

### Debugging Locally
- PostgreSQL logs are visible in console output when running `mvn spring-boot:run`
- JOOQ-generated SQL is logged at DEBUG level; enable in `application.properties` if needed
- Use `mvn test -X` for Maven debug output

## Notes

- **Java 21 records** may be used where appropriate (modern codebase target)
- **PIT mutation testing** is configured; commit confidence is validated via mutation coverage
- **Liquibase** handles schema versioning—database changes go in changelog files, not direct SQL
- The `.mvn/` directory contains Maven wrapper; `./mvnw` works on Unix/macOS, `.\mvnw.cmd` on Windows
