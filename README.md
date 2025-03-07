# Customer Management Microservice

A robust microservice for customer data management, providing functionality for registration, querying, and analysis of customer information.

## Overview

This microservice implements a scalable, secure solution for managing customer data with the following features:
- Customer registration and CRUD operations
- Statistical analysis (average age, standard deviation)
- Life expectancy calculation
- Asynchronous processing with message queues
- Comprehensive security with JWT authentication
- API documentation and monitoring

## Architecture

The solution follows a layered architecture with clear separation of concerns:

### Layers
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Implements business logic
- **Repository Layer**: Manages data persistence
- **Security Layer**: Handles authentication and authorization
- **Messaging Layer**: Manages asynchronous communication

### Design Patterns
- **Repository Pattern**: For data access abstraction
- **DTO Pattern**: For data transfer between layers
- **Builder Pattern**: For object construction
- **Factory Pattern**: For creating complex objects
- **Strategy Pattern**: For life expectancy calculations

### Technologies Used
- **Spring Boot**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence
- **PostgreSQL**: Database
- **RabbitMQ**: Message broker for asynchronous processing
- **JWT**: Token-based security
- **OpenAPI/Swagger**: API documentation
- **Spring Actuator**: Monitoring and metrics

## API Documentation

The API is documented using OpenAPI/Swagger and can be accessed at:
`http://localhost:8080/swagger-ui.html`

### Key Endpoints

- `POST /api/auth/login`: Authenticate and get JWT token
- `POST /api/customers`: Create a new customer
- `GET /api/customers`: List all customers with life expectancy
- `GET /api/customers/{id}`: Get a specific customer
- `GET /api/customers/metrics`: Get statistical metrics
- `PUT /api/customers/{id}`: Update a customer
- `DELETE /api/customers/{id}`: Delete a customer

## Asynchronous Processing

The service uses RabbitMQ for asynchronous processing of customer-related events:

1. When a customer is created, a message is sent to a queue
2. A consumer processes this message asynchronously
3. This pattern allows for scalable handling of operations like:
    - Sending welcome emails
    - Updating analytics systems
    - Triggering integrations with other services

This approach improves system responsiveness by offloading time-consuming operations.

## Security Implementation

The service implements JWT-based authentication:
1. Users authenticate via username/password
2. Server returns a signed JWT token
3. Subsequent requests include this token in the Authorization header
4. Server validates the token for each request

This approach provides secure, stateless authentication suitable for microservices.

## Setup and Deployment

### Prerequisites
- Java 17
- Docker and Docker Compose
- Maven

### Local Development Setup

1. Clone the repository:
   - git clone https://github.com/yourusername/customer-service.git
   - cd customer-service
2. Start the required infrastructure:
   - docker-compose up -d
3. Build and run the application:
   - mvn clean install
   - mvn spring-boot:run
4. Access the application:
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - RabbitMQ Management: http://localhost:15672 (guest/guest)

### Environment Variables

The following environment variables can be configured:

- `SPRING_DATASOURCE_URL`: Database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_RABBITMQ_HOST`: RabbitMQ host
- `SECURITY_JWT_SECRET`: Secret key for JWT signing
- `SECURITY_JWT_EXPIRATION`: Token expiration time in milliseconds

### Cloud Deployment

The service is designed for deployment on Kubernetes:

1. Build the Docker image:
   - docker build -t customer-service:latest
2. Push to your container registry
3. Deploy using the provided Kubernetes manifests in the `k8s` directory

## Monitoring and Metrics

The service exposes metrics via Spring Actuator:
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus endpoint: `/actuator/prometheus`

## Testing

Run tests using Maven:
mvn test

  The test suite includes:
- Unit tests for service and utility classes
- Integration tests for repositories and controllers
- End-to-end tests for API endpoints

## Future Improvements

- Implement caching for frequently accessed data
- Add pagination for customer listing
- Enhance metrics with more business insights
- Implement event sourcing for customer data changes