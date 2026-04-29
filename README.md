<div align="center">
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original-wordmark.svg" alt="Spring Logo" width="100">
  <h1>Order Management Service</h1>
  <p><i>A production-grade Spring Boot REST API built with precision and modern best practices.</i></p>

  [![Java](https://img.shields.io/badge/Java-17-blue.svg)]()
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)]()
  [![MapStruct](https://img.shields.io/badge/Mapping-MapStruct-orange.svg)]()
  [![Lombok](https://img.shields.io/badge/Boilerplate-Lombok-red.svg)]()
  [![License](https://img.shields.io/badge/License-MIT-green.svg)]()
</div>

---

## 🚀 Overview

The **Order Management Service** is a sophisticated backend platform designed to handle the complete lifecycle of customer orders. Built using **Spring Boot 3.5.9**, it demonstrates a modern approach to building scalable, secure, and maintainable microservices.

This project goes beyond basic CRUD, implementing enterprise-level features like **JWT Refresh Tokens**, **MapStruct** for clean object mapping, **Lombok** for boilerplate reduction, and **JPA Auditing**.

---

## ✨ Key Features

- **🔐 Advanced Security**:
  - Stateless JWT Authentication.
  - **Refresh Token Mechanism** for seamless session renewal.
  - Role-Based Access Control (**ADMIN** vs **USER**).
- **📦 Domain-Driven Design**:
  - Rich Domain Model with encapsulated business logic (tax and total calculations).
  - Clean separation between Entities and DTOs using **MapStruct**.
- **🚦 API Excellence**:
  - **Pagination & Sorting** for all list endpoints.
  - **Bean Validation** (JSR-303) for all incoming requests.
  - Centralized Exception Handling with consistent error responses.
- **⚡ Performance & Maintenance**:
  - **Lombok** for zero-boilerplate code.
  - **JPA Auditing** for automatic `createdAt` and `updatedAt` tracking.
  - Efficient fetching with **EntityGraphs**.
- **📖 Documentation**:
  - Interactive API explorer via **Swagger UI / OpenAPI 3.0**.

---

## 🧱 Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.5.9 |
| **Security** | Spring Security + JWT + Refresh Tokens |
| **Language** | Java 17 |
| **Database** | MySQL |
| **ORM** | Spring Data JPA / Hibernate |
| **Mapping** | MapStruct 1.6.0 |
| **Utility** | Lombok 1.18.34 |
| **Docs** | Springdoc OpenAPI (Swagger) |
| **Testing** | JUnit 5 + Mockito |

---

## 📂 Project Architecture

The project follows a modular, package-by-feature structure for high maintainability:

```text
io.github.vikij.ordermanagement
├── 🔑 auth         # JWT, Refresh Tokens, Security Filters
├── 📦 order        # Order Domain, Mappers, Paginated Controllers
├── 👤 user         # User Management & Roles
├── 🛠️ common       # Global Exception Handlers & Error Models
└── ⚙️ config       # JPA Auditing & OpenAPI Configuration
```

---

## 🔐 Authentication Flow

1. **Login**: User provides credentials → System returns **Access Token** (Short-lived) and **Refresh Token** (Long-lived).
2. **Access**: Access Token is sent in the `Authorization: Bearer` header.
3. **Refresh**: When Access Token expires, client calls `/auth/refresh` with the Refresh Token to get a new Access Token.

---

## 🌐 API Reference

### 📖 Interactive Documentation
Access the full API specification and test endpoints directly:
👉 `http://localhost:8080/swagger-ui.html`

### 🛒 Order Management
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/orders` | USER/ADMIN | Create a new order with items. |
| `GET` | `/orders` | USER/ADMIN | List orders (Paginated). Users see own, Admins see all. |
| `GET` | `/orders/{num}/status` | Public | Check the status of an order by number. |
| `PATCH`| `/orders/{num}/status` | ADMIN | Update the status (CREATED → PROCESSING → etc.). |

---

## 🛠️ Setup & Installation

---

## 🔁 Order Lifecycle

```text
CREATED ─────► PROCESSING ─────► COMPLETED
   │                               ▲
   └──────────► CANCELLED ─────────┘
```
- **Only ADMIN** can update the order status.
- **Auditing**: Status changes are timestamped via `updatedAt`.
- **Completion**: `completedAt` or `cancelledAt` are automatically set upon reaching terminal states.

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

The API uses a centralized `GlobalExceptionHandler` to ensure consistent error responses across all modules.

**Example Error Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Order not found",
  "timestamp": "2026-04-30T22:08:00"
}
```

---

## 🛠️ Setup & Installation

### 1. Environment Configuration
Sensitive values are managed via environment variables or a `.env` file (ignored by Git):

```bash
DB_URL=jdbc:mysql://localhost:3306/order_management
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_super_secret_key_at_least_32_chars
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Running Tests
Run unit tests for business logic validation:
```bash
./mvnw test
```

---

## 👤 Author

**Jayavignesh**  
*Backend Engineer specializing in Java, Spring Boot, and Distributed Systems.*

- 🌐 [Portfolio](https://jayavignesh.dev)
- 📌 [GitHub](https://github.com/i-viki)
- 💼 [LinkedIn](https://www.linkedin.com/in/viki-j)

---
<div align="center">
  <sub>Built with ❤️ by Jayavignesh</sub>
</div>
