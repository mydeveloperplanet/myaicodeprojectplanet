# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5 application that implements a RESTful API for managing customer data. The application uses:

- Spring Boot 3.5 with Java 21
- PostgreSQL database with Liquibase for database migrations
- JOOQ for database access
- OpenAPI/Swagger for API documentation
- Maven for build management

## Architecture

The application follows a layered architecture pattern:

1. **Controller Layer**: REST endpoints in `CustomersController`
2. **Service Layer**: Business logic in `CustomerService` and `CustomerServiceImpl`
3. **Repository Layer**: Database access in `CustomerRepository` using JOOQ
4. **Model Layer**: Domain objects in `Customer` class
5. **OpenAPI Layer**: Generated API interfaces and models from OpenAPI spec

## Key Files

- `src/main/java/com/mydeveloperplanet/myaicodeprojectplanet/MyAiCodeProjectPlanetApplication.java` - Main application class
- `src/main/java/com/mydeveloperplanet/myaicodeprojectplanet/controller/CustomersController.java` - REST endpoints
- `src/main/java/com/mydeveloperplanet/myaicodeprojectplanet/service/CustomerServiceImpl.java` - Business logic
- `src/main/java/com/mydeveloperplanet/myaicodeprojectplanet/repository/CustomerRepository.java` - Database operations
- `src/main/resources/db/changelog/migration/db.changelog-1.xml` - Database schema definition
- `src/main/resources/static/customers.yaml` - OpenAPI specification

## Development Commands

### Building
- `./mvnw clean compile` - Compile the application
- `./mvnw clean package` - Build a jar file
- `./mvnw clean install` - Install dependencies and build

### Running Tests
- `./mvnw test` - Run all tests
- `./mvnw test -Dtest=CustomersControllerTest` - Run specific test class
- `./mvnw test -Dtest=CustomersControllerTest#customersGet_shouldReturnAllCustomers` - Run specific test method

### Running Application
- `./mvnw spring-boot:run` - Run the application
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` - Run with specific profile

### Code Generation
- `./mvnw generate-sources` - Regenerate JOOQ and OpenAPI code
- `./mvnw compile` - Compile with generated code

### Mutation Testing
- `./mvnw org.pitest:pitest-maven:mutationCoverage` - Run mutation tests

## Database Setup

The application uses Liquibase for database migrations. The database schema is defined in `src/main/resources/db/changelog/migration/db.changelog-1.xml`. The application will automatically create the database schema on startup.

## API Endpoints

The API provides standard CRUD operations for customers:
- GET `/customers` - Get all customers
- POST `/customers` - Create a new customer
- GET `/customers/{id}` - Get a specific customer
- PUT `/customers/{id}` - Update a customer
- DELETE `/customers/{id}` - Delete a customer

## Key Implementation Details

1. The application uses JOOQ for type-safe database access
2. OpenAPI specification is used to generate API interfaces and models
3. The application uses Spring Boot's auto-configuration for database setup
4. Tests are written using Spring Boot's test framework with MockMvc for web layer testing