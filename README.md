# 🛒 Online Shopping Application

A **production-style e-commerce backend application** built using **Java, Spring Boot, Microservices, Apache Kafka, PostgreSQL, Docker, Eureka Service Discovery, and Grafana**.

The project demonstrates how modern distributed applications can be designed using **loosely coupled microservices**, synchronous REST communication, asynchronous event-driven communication, service discovery, containerization, and monitoring.

---

## 📌 Project Overview

The Online Shopping Application is designed using a **Microservices Architecture**, where each business functionality is developed and deployed as an independent service.

The application currently contains:

* Product Service
* Inventory Service
* Order Service
* Notification Service
* Discovery Server
* API Gateway
* PostgreSQL databases
* Apache Kafka
* Grafana monitoring

The main business flow is:

```text
Customer
   │
   ▼
API Gateway
   │
   ▼
Order Service
   │
   ├──────────────► Inventory Service
   │                    │
   │                    ▼
   │               Check Stock
   │
   ▼
Create Order
   │
   ▼
Publish Order Event
   │
   ▼
Apache Kafka
   │
   ▼
Notification Service
   │
   ▼
Send Notification
```

---

# 🏗️ Architecture

```text
                         ┌──────────────────────┐
                         │       Client         │
                         │  Postman / Frontend  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     API Gateway      │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌────────────┐  ┌────────────┐  ┌──────────────┐
             │  Product   │  │   Order    │  │  Inventory   │
             │  Service   │  │  Service   │  │   Service    │
             └─────┬──────┘  └──────┬─────┘  └──────┬───────┘
                   │                │               │
                   ▼                │               ▼
            ┌─────────────┐         │        ┌──────────────┐
            │ Product DB  │         │        │ Inventory DB │
            └─────────────┘         │        └──────────────┘
                                    │
                                    ▼
                              ┌─────────────┐
                              │ Apache Kafka│
                              └──────┬──────┘
                                     │
                                     ▼
                              ┌──────────────┐
                              │ Notification │
                              │   Service    │
                              └──────────────┘
                                     │
                                     ▼
                                Notification


                     ┌─────────────────────────┐
                     │    Discovery Server     │
                     │    Eureka Server        │
                     └─────────────────────────┘


                     ┌─────────────────────────┐
                     │        Grafana          │
                     │       Monitoring        │
                     └─────────────────────────┘
```

---

# 🧩 Microservices

## 1. Discovery Server

The **Discovery Server** uses Netflix Eureka for service registration and discovery.

All microservices register themselves with Eureka so that they can discover and communicate with each other without relying on hard-coded hostnames and ports.

### Responsibilities

* Service registration
* Service discovery
* Maintain service registry
* Enable dynamic communication between microservices

Default Eureka dashboard:

```text
http://localhost:8761
```

---

## 2. Product Service

The **Product Service** manages product-related information.

### Responsibilities

* Create products
* Retrieve products
* Store product information
* Manage product details

### Example API

```http
POST /api/product
```

Create a product.

```http
GET /api/product
```

Retrieve products.

---

## 3. Inventory Service

The **Inventory Service** manages product stock.

### Responsibilities

* Maintain inventory
* Check product availability
* Validate requested quantity
* Maintain stock information

Example:

```text
Product: iPhone-15
Available Quantity: 10
Requested Quantity: 2

Result: Product Available
```

If sufficient stock is not available, the order cannot be completed.

---

## 4. Order Service

The **Order Service** manages customer orders.

It communicates with the Inventory Service to verify whether the requested products are available before creating an order.

### Responsibilities

* Receive order requests
* Validate order items
* Check inventory
* Create orders
* Store order information
* Publish order events to Kafka

Example API:

```http
POST /api/order
```

Example request:

```json
{
  "orderLineItemsDto": [
    {
      "skuCode": "iphone-15",
      "price": 79999,
      "quantity": 1
    }
  ]
}
```

---

# 🔔 5. Notification Service

The **Notification Service** is responsible for sending notifications related to application events.

It uses **Apache Kafka** for asynchronous communication.

Instead of the Order Service directly calling the Notification Service, the Order Service publishes an event to Kafka.

The Notification Service consumes the event and processes the notification.

### Flow

```text
Order Service
      │
      │ Publish Event
      ▼
Apache Kafka
      │
      │ Consume Event
      ▼
Notification Service
      │
      ▼
Send Notification
```

### Benefits

* Loose coupling
* Asynchronous processing
* Better scalability
* Better fault isolation
* Independent deployment of services
* Event-driven architecture

---

# 📨 Apache Kafka

Apache Kafka is used for **event-driven communication** between services.

The Order Service publishes order-related events, and the Notification Service consumes those events.

```text
                 ┌─────────────────┐
                 │  Order Service  │
                 └────────┬────────┘
                          │
                          │ Publish
                          ▼
                 ┌─────────────────┐
                 │   Kafka Topic   │
                 └────────┬────────┘
                          │
                          │ Consume
                          ▼
                 ┌─────────────────────┐
                 │ Notification Service│
                 └─────────────────────┘
```

This avoids tight coupling between the Order Service and Notification Service.

---

# 🔄 Complete Order Flow

When a customer places an order, the following flow takes place:

### Step 1 — Customer places an order

The client sends an HTTP request to the Order Service through the API Gateway.

```text
Client
  │
  ▼
API Gateway
  │
  ▼
Order Service
```

### Step 2 — Inventory validation

The Order Service communicates with the Inventory Service.

```text
Order Service
      │
      ▼
Inventory Service
      │
      ▼
Check Product Stock
```

### Step 3 — Order creation

If sufficient inventory is available:

```text
Inventory Available
        │
        ▼
Create Order
        │
        ▼
Save Order in Database
```

### Step 4 — Publish Kafka event

After creating the order, the Order Service publishes an event.

```text
Order Service
      │
      ▼
Kafka Topic
```

### Step 5 — Notification

The Notification Service consumes the event.

```text
Kafka
  │
  ▼
Notification Service
  │
  ▼
Process Notification
```

---

# 🗄️ Database Architecture

The project follows the **Database-per-Service** approach.

Each microservice owns its own database.

```text
┌────────────────────┐
│   Product Service  │
└─────────┬──────────┘
          │
          ▼
     Product DB


┌────────────────────┐
│   Order Service    │
└─────────┬──────────┘
          │
          ▼
      Order DB


┌────────────────────┐
│ Inventory Service  │
└─────────┬──────────┘
          │
          ▼
   Inventory DB
```

This provides:

* Service independence
* Loose coupling
* Independent database changes
* Better scalability
* Better fault isolation

---

# 🌐 API Gateway

The API Gateway acts as the **single entry point** for client requests.

Instead of directly calling individual microservices, clients can communicate through the gateway.

```text
Client
   │
   ▼
API Gateway
   │
   ├──────► Product Service
   │
   ├──────► Order Service
   │
   └──────► Inventory Service
```

The gateway can also be used for:

* Routing
* Authentication
* Authorization
* Request filtering
* Centralized logging

---

# 🔎 Service Discovery with Eureka

The project uses **Eureka Service Discovery**.

Instead of services using hard-coded URLs:

```text
http://localhost:8082
```

services can discover other services through Eureka.

```text
                Eureka Server
                     │
       ┌─────────────┼──────────────┐
       │             │              │
       ▼             ▼              ▼
 Product Service  Order Service  Inventory Service
       │             │              │
       └─────────────┴──────────────┘
```

This makes the system more flexible when service instances or ports change.

---

# 📊 Monitoring with Grafana

The application also includes **Grafana** for monitoring and visualization.

Grafana can be used to create dashboards for application and infrastructure metrics.

Typical monitoring architecture:

```text
Application
     │
     ▼
Application Metrics
     │
     ▼
Monitoring / Metrics System
     │
     ▼
Grafana
     │
     ▼
Dashboard
```

Grafana dashboards can be used to monitor:

* Application health
* Request metrics
* Service performance
* Resource usage
* Error rates
* Container metrics

---

# 🛠️ Technology Stack

## Backend

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* Spring Cloud
* Spring Cloud Netflix Eureka
* REST APIs

## Messaging

* Apache Kafka
* Event-driven architecture
* Kafka Producer
* Kafka Consumer

## Database

* PostgreSQL
* JPA / Hibernate

## API & Communication

* REST
* Inter-service communication
* API Gateway
* Eureka Service Discovery

## DevOps

* Docker
* Docker Compose
* Grafana

## Development Tools

* IntelliJ IDEA
* Maven
* Git
* GitHub
* Postman

---

# 📁 Project Structure

```text
Online-Shopping-Application/
│
├── discovery-server/
│   ├── src/
│   │   └── main/
│   └── pom.xml
│
├── product-service/
│   ├── src/
│   │   └── main/
│   └── pom.xml
│
├── inventory-service/
│   ├── src/
│   │   └── main/
│   └── pom.xml
│
├── order-service/
│   ├── src/
│   │   └── main/
│   └── pom.xml
│
├── notification-service/
│   ├── src/
│   │   └── main/
│   └── pom.xml
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# 🔗 Service Communication

The application uses two major communication patterns.

## Synchronous Communication

REST APIs are used when an immediate response is required.

Example:

```text
Order Service
      │
      │ REST API
      ▼
Inventory Service
      │
      ▼
Check Inventory
```

## Asynchronous Communication

Kafka is used for event-driven communication.

Example:

```text
Order Service
      │
      │ Kafka Event
      ▼
Kafka Topic
      │
      ▼
Notification Service
```

This combination of **synchronous REST + asynchronous Kafka communication** is commonly used in distributed systems.

---

# 🐳 Docker

The application is containerized using Docker.

Docker Compose can be used to start the required infrastructure and services together.

### Start Application

```bash
docker compose up -d
```

### Check Containers

```bash
docker compose ps
```

### View Logs

```bash
docker compose logs -f
```

### View Specific Service Logs

```bash
docker compose logs -f order-service
```

```bash
docker compose logs -f notification-service
```

### Stop Application

```bash
docker compose down
```

### Stop and Remove Volumes

```bash
docker compose down -v
```

> ⚠️ `docker compose down -v` removes Docker volumes and can delete persisted database data.

---

# ⚙️ Running the Project Locally

## Prerequisites

Install the following:

* Java 17
* Maven
* Docker Desktop
* Git
* IntelliJ IDEA
* Postman

---

## 1. Clone Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate to the project:

```bash
cd Online-Shopping-Application
```

---

## 2. Build the Project

```bash
mvn clean install
```

If you want to skip tests:

```bash
mvn clean install -DskipTests
```

---

## 3. Start Infrastructure

Start Docker services:

```bash
docker compose up -d
```

Verify:

```bash
docker compose ps
```

---

## 4. Start Microservices

Start the services according to the project configuration.

Recommended startup order:

```text
1. Discovery Server
2. Product Service
3. Inventory Service
4. Order Service
5. Notification Service
6. API Gateway
```

If the services are included in Docker Compose, they can be started together:

```bash
docker compose up -d
```

---

# 🧪 Testing

APIs can be tested using **Postman**.

Typical testing flow:

```text
1. Create Product
        ↓
2. Add Inventory
        ↓
3. Check Inventory
        ↓
4. Create Order
        ↓
5. Inventory Validation
        ↓
6. Order Created
        ↓
7. Kafka Event Published
        ↓
8. Notification Service Consumes Event
```

---

# 📝 Example API Request

### Create Order

```http
POST /api/order
Content-Type: application/json
```

Request:

```json
{
  "orderLineItemsDto": [
    {
      "skuCode": "iphone-15",
      "price": 79999,
      "quantity": 1
    }
  ]
}
```

Expected flow:

```text
POST /api/order
       │
       ▼
Order Service
       │
       ▼
Inventory Service
       │
       ▼
Stock Available
       │
       ▼
Create Order
       │
       ▼
Kafka Event
       │
       ▼
Notification Service
```

---

# 🔐 Configuration

Each microservice has its own configuration.

Typical configuration includes:

```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/database
spring.datasource.username=postgres
spring.datasource.password=password

eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

Kafka configuration may include:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

> Update ports, database names, credentials, Kafka configuration, and service URLs according to your `application.properties`, `application.yml`, and `docker-compose.yml`.

---

# 📈 Key Concepts Demonstrated

This project demonstrates practical knowledge of:

* Microservices Architecture
* Spring Boot
* REST APIs
* Inter-service communication
* Eureka Service Discovery
* API Gateway
* Apache Kafka
* Event-driven architecture
* Kafka Producer
* Kafka Consumer
* Database-per-Service
* PostgreSQL
* Spring Data JPA
* Hibernate
* Docker
* Docker Compose
* Grafana
* Application monitoring
* Synchronous communication
* Asynchronous communication
* Loose coupling
* Independent service deployment

---

# 🎯 What This Project Demonstrates

The project combines several important concepts used in modern Java backend development:

```text
                   Java 17
                      │
                      ▼
                Spring Boot
                      │
                      ▼
               Microservices
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
       REST API                 Kafka
          │                       │
          ▼                       ▼
    Synchronous              Asynchronous
    Communication            Communication
          │                       │
          └───────────┬───────────┘
                      ▼
                  PostgreSQL
                      │
                      ▼
                    Docker
                      │
                      ▼
                   Grafana
```

---

# 🚀 Future Enhancements

The following features can be added as the project evolves:

* [ ] Spring Security
* [ ] JWT Authentication
* [ ] Role-based authorization
* [ ] Redis caching
* [ ] Resilience4j Circuit Breaker
* [ ] Spring Cloud Config
* [ ] Distributed tracing
* [ ] Prometheus
* [ ] Advanced Grafana dashboards
* [ ] Centralized logging
* [ ] Unit testing with JUnit and Mockito
* [ ] Integration testing
* [ ] Testcontainers
* [ ] CI/CD with GitHub Actions
* [ ] AWS deployment
* [ ] Kubernetes deployment

---

# 📚 Learning Goals

This project was developed to gain practical understanding of **Java Spring Boot Microservices** and distributed system architecture.

The major learning areas include:

1. Designing independent microservices
2. Building REST APIs using Spring Boot
3. Implementing service discovery with Eureka
4. Implementing synchronous service-to-service communication
5. Implementing asynchronous communication using Kafka
6. Working with PostgreSQL and JPA
7. Containerizing applications using Docker
8. Running multiple services using Docker Compose
9. Monitoring applications using Grafana
10. Understanding real-world microservices architecture

---

# 👩‍💻 Author

**Bhumi Shah**

Java Backend / Full Stack Software Engineer

**Technologies:** Java | Spring Boot | Microservices | Kafka | PostgreSQL | Docker | AWS

---

# ⭐ Support

If you find this project useful for learning **Java, Spring Boot, Microservices, Kafka, and Docker**, consider giving the repository a ⭐ on GitHub.
