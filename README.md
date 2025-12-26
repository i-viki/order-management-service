<div align="center">
<h1>Order Management Service</h1>


[![Java](https://img.shields.io/badge/Java-17-blue.svg)]() [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)]() [![Build](https://img.shields.io/badge/Build-Maven-blue.svg)]() [![License](https://img.shields.io/badge/License-MIT-green.svg)]()

</div>

A production-style **Order Management Backend** built with **Spring Boot**, focusing on **security, clean architecture, and real business workflows**.

This project demonstrates how a backend engineer designs APIs, authentication, persistence, and domain boundaries in a realistic way.

---

## ✨ Features

* User signup & login
* JWT-based authentication (stateless)
* Role-based authorization (ADMIN / USER)
* Order creation with multiple items
* Order lifecycle management
* Public order-status tracking
* Secure access to user-owned orders
* Centralized exception handling
* Environment-based secret management (`.env`)
* Clean DTO boundaries (no entity leakage)

---

## 🧱 Tech Stack

| Layer     | Technology            |
| --------- | --------------------- |
| Language  | Java 17               |
| Framework | Spring Boot 3.5.9     |
| Security  | Spring Security + JWT |
| ORM       | JPA / Hibernate       |
| Database  | MySQL                 |
| Build     | Maven                 |
| Auth      | BCrypt + JWT          |
| Logging   | SLF4J + Logback       |

---

## 📁 Project Structure

```
io.github.vikij.ordermanagement
│
├── auth
│   ├── controller      # Login / Signup APIs
│   ├── dto             # Auth request/response models
│   ├── jwt             # JWT utilities & filters
│   └── service         # UserDetailsService
│
├── order
│   ├── controller      # Order APIs
│   ├── service         # Business logic
│   ├── entity          # Order, OrderItem, OrderStatus
│   ├── repository      # JPA repositories
│   └── dto             # Request/Response DTOs
│
├── user
│   ├── entity          # AppUser, Role
│   └── repository      # UserRepository
│
├── common
│   ├── error           # API error responses
│   ├── exception       # Custom exceptions
│   └── config          # Env & JWT config
│
└── OrderManagementApplication.java
```

---

## 🔐 Authentication & Authorization

### Authentication

* JWT-based (stateless)
* Token issued on login
* Sent via `Authorization: Bearer <token>`

### Authorization Rules

| Endpoint                       | Access                         |
| ------------------------------ | ------------------------------ |
| `/auth/signup`                 | Public                         |
| `/auth/login`                  | Public                         |
| `/orders` (POST)               | USER / ADMIN                   |
| `/orders` (GET)                | USER → own orders, ADMIN → all |
| `/orders/{orderNumber}/status` | Public                         |
| `/admin/**`                    | ADMIN only                     |

---

## 📦 Order Domain Model

**Order** is the aggregate root and owns:

* Delivery address
* Monetary values
* Order items
* Status lifecycle
* Audit timestamps

```
Order
 ├── OrderItem (1..n)
 ├── CreatedBy (User)
 ├── Status (CREATED → PROCESSING → COMPLETED / CANCELLED)
```

---

## 🔁 Order Lifecycle

```
CREATED → PROCESSING → COMPLETED
     ↘︎ CANCELLED
```

* Only ADMIN can update order status
* Status updates record timestamps
* No implicit state changes

---

## 🌐 API Overview

### Auth APIs

**Signup**

```
POST /auth/signup
```

**Login**

```
POST /auth/login
```

---

### Order APIs

**Create Order**

```
POST /orders
Authorization: Bearer <TOKEN>
```

**Get Orders**

```
GET /orders
Authorization: Bearer <TOKEN>
```

* USER → sees own orders
* ADMIN → sees all orders

**Update Order Status (ADMIN)**

```
PATCH /orders/{id}/status
```

**Public Order Status**

```
GET /orders/{orderNumber}/status
```

---

## 🧾 Sample Create Order Request

```json
{
  "items": [
    {
      "productCode": "SKU-IPHONE-15",
      "quantity": 1,
      "unitPrice": 79999.00
    },
    {
      "productCode": "SKU-AIRPODS-PRO",
      "quantity": 2,
      "unitPrice": 24999.00
    }
  ],
  "deliveryAddress": {
    "addressLine": "123 Main Street",
    "city": "Chennai",
    "country": "India",
    "postalCode": "600001"
  }
}
```

---

## ⚠️ Error Handling

* Centralized `GlobalExceptionHandler`
* Consistent error response structure
* Security errors handled at filter level

Example:

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Order not found",
  "timestamp": "2025-12-25T22:08:00"
}
```

---

## 🔐 Environment Configuration

Sensitive values are stored in `.env`:

```
DB_URL=jdbc:mysql://localhost:3306/order_management
DB_USERNAME=root
DB_PASSWORD=*****
JWT_SECRET=*****
```

> `.env` is ignored by Git and never committed.

---

## 🚀 Running the Application

```bash
./mvnw spring-boot:run
```

or from IDE:

```
Run OrderManagementApplication
```

Server starts at:

```
http://localhost:8080
```

---

## 🧪 Testing

* APIs tested using Postman / curl
* JWT expiration and invalid token scenarios handled
* Role-based access verified

---

## 👤 Author

**Jayavignesh**  
Backend Engineer  
Specializing in Java, Spring Boot, and Distributed Systems  

🌐 https://jayavignesh.dev  
📌 GitHub: https://github.com/i-viki
