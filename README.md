# 🔀 API Gateway

The single entry point for all client requests in the Tech Store microservices architecture. This service handles routing, cross-origin resource sharing (CORS) policies, and request distribution to downstream services like the Order, Product, and Auth services.

## 🛠️ Tech Stack
* Java 17
* Spring Boot 3.4+
* Spring Cloud Gateway
* Spring Cloud Netflix Eureka Client

## ⚙️ Configuration
The gateway routes traffic based on URL path predicates.
* **Port:** `8080`
* **Eureka Registration:** Registers automatically as `API-GATEWAY`.

### Route Mapping
* `/api/orders/**` -> Routed to `ORDER-SERVICE`
* `/api/products/**` -> Routed to `PRODUCT-SERVICE`
* `/auth/**` -> Routed to `AUTH-SERVICE`

## 🚀 Running the Application
Ensure the Eureka Server is running before booting the gateway.
```bash
./mvnw spring-boot:run
