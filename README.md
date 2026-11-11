# Order Lifecycle & Fulfillment Engine

[![Java](https://img.shields.io/badge/Java-17-18181b?logo=openjdk&logoColor=ED8B00&labelColor=101012)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-18181b?logo=springboot&logoColor=6DB33F&labelColor=101012)](https://spring.io)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--GA-18181b?logo=spring&logoColor=6DB33F&labelColor=101012)](https://spring.io/projects/spring-ai)
[![Gemini](https://img.shields.io/badge/Gemini_Flash-1.5-18181b?logo=google-gemini&logoColor=4285F4&labelColor=101012)](https://deepmind.google/technologies/gemini/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-18181b?logo=mysql&logoColor=4479A1&labelColor=101012)](https://www.mysql.com)
[![Hibernate](https://img.shields.io/badge/Hibernate-JPA-18181b?logo=hibernate&logoColor=59666C&labelColor=101012)](https://hibernate.org)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.6.0-18181b?logo=mapstruct&logoColor=F58220&labelColor=101012)](https://mapstruct.org)
[![Lombok](https://img.shields.io/badge/Lombok-1.18.34-18181b?logo=lombok&logoColor=BC1B1B&labelColor=101012)](https://projectlombok.org)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-18181b?logo=swagger&logoColor=85EA2D&labelColor=101012)](https://swagger.io)
[![Maven](https://img.shields.io/badge/Maven-3.9+-18181b?logo=apachemaven&logoColor=C71A36&labelColor=101012)](https://maven.apache.org)
[![JWT](https://img.shields.io/badge/Security-JWT-18181b?logo=jsonwebtokens&logoColor=FFFFFF&labelColor=101012)](https://jwt.io)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

An enterprise-grade **order processing service and intelligent customer resolution platform** combining an audited finite state machine with **grounded conversational AI**. Built on **Java 17**, **Spring Boot 3.4.1**, **Spring AI**, and **Google Gemini Flash**, the system orchestrates resilient order lifecycle transitions (`CREATED` $\to$ `PROCESSING` $\to$ `COMPLETED` / `CANCELLED`), prevents N+1 query bottlenecks via JPA EntityGraphs, and enforces strict zero-data-leakage prompt grounding to resolve customer inquiries securely.

---

## Key Architecture Pillars

- **Audited Order Finite State Machine (`io.github.vikij.ordermanagement.order`):**
  Deterministic lifecycle enforcement guaranteeing orders progress strictly along validated pathways (`CREATED` $\to$ `PROCESSING` $\to$ `COMPLETED`/`CANCELLED`) with domain invariant validation and automated tax adjustments.
- **Dynamic Prompt Grounding & Context Isolation (`io.github.vikij.ordermanagement.ai`):**
  Secure context injection feeding only authenticated customer order history into Google Gemini Flash `ChatClient`, eliminating cross-tenant data leaks and hallucinated resolution terms.
- **High-Performance JPA EntityGraphs (`io.github.vikij.ordermanagement.order`):**
  Optimized relational persistence utilizing Spring Data JPA NamedEntityGraphs to fetch order headers and associated line items in a single round-trip, completely eradicating N+1 query degradation.
- **Stateless JWT Security & Token Rotation (`io.github.vikij.ordermanagement.auth`):**
  Dual-token architecture issuing short-lived access JWTs and database-persisted refresh tokens, paired with granular RBAC guards (`ADMIN`, `USER`).
- **Declarative DTO Mapping with MapStruct (`io.github.vikij.ordermanagement.common`):**
  Compile-time type-safe object transformations preventing runtime reflection penalties and maintaining absolute separation between internal JPA entities and public REST contracts.

---

## System Architecture

```mermaid
flowchart TB
    subgraph CLIENT["Client & Consumer Tier"]
        WEB["E-Commerce / Enterprise Web App<br/>Port: Client Dependent"]
        MOBILE["Mobile App Client"]
    end

    subgraph SECURITY["Security & Governance Perimeter"]
        direction TB
        JWT_FILTER["JwtAuthenticationFilter<br/>• Bearer Extraction<br/>• Signature Verification"]
        RBAC["RBAC Authorization Guard<br/>• ROLE_USER (Own Data)<br/>• ROLE_ADMIN (Global Fleet)"]
        JWT_FILTER --> RBAC
    end

    subgraph CONTROLLERS["API Gateway & Controllers (Java 17 / Spring Boot)"]
        AUTH_CTRL["AuthController<br/>/api/v1/auth/*"]
        ORDER_CTRL["OrderController<br/>/api/v1/orders/*"]
        AI_CTRL["AiController<br/>/api/v1/ai/chat"]
    end

    subgraph SERVICES["Core Service & Domain Layer"]
        AUTH_SVC["AuthService<br/>• Token Minting<br/>• Refresh Rotation"]
        ORDER_SVC["OrderService<br/>• State Machine FSM<br/>• Tax Computation Engine"]
        AI_SVC["AiService<br/>• Context Grounding<br/>• Prompt Isolation Filter"]
    end

    subgraph AI_CORE["Spring AI & Gemini Inference"]
        CHAT_CLIENT["Spring AI ChatClient<br/>• System Prompt Grounding<br/>• Model: gemini-flash-latest"]
    end

    subgraph DATA_TIER["Persistence Infrastructure"]
        JPA["Spring Data JPA + EntityGraphs<br/>(N+1 Query Elimination)"]
        DB[("Relational Store<br/>MySQL 8.0 / H2 Engine<br/>• orders & order_items<br/>• users & refresh_tokens")]
    end

    CLIENT -->|"HTTPS / Bearer JWT"| SECURITY
    SECURITY --> CONTROLLERS
    
    AUTH_CTRL --> AUTH_SVC
    ORDER_CTRL --> ORDER_SVC
    AI_CTRL --> AI_SVC

    ORDER_SVC --> JPA
    AUTH_SVC --> JPA
    AI_SVC -->|"1. Fetch Authenticated Orders"| JPA
    AI_SVC -->|"2. Inject Isolated Context"| CHAT_CLIENT
    
    JPA <--> DB
```

### Project Structure

```text
io.github.vikij.ordermanagement
├── auth         # Security infrastructure (JWT, RBAC)
├── order        # Core domain, business logic, and mapping
├── ai           # AI orchestration and prompt grounding
├── user         # Identity management
├── common       # Shared exceptions and error models
└── config       # Infrastructure configuration (Dotenv, Security)
```

---

## Order State Machine & Prompt Grounding Invariants

The platform maintains strict integrity across order status progressions and AI reasoning boundaries:

$$\text{TotalAmount} = \sum_{i=1}^{n} (\text{UnitPrice}_i \times \text{Quantity}_i) \times (1 + \text{TaxRate})$$

$$\text{Context}_{\text{LLM}} = \{ O_k \in \text{Orders} \mid O_k.\text{userId} = \text{Principal}.\text{id} \}$$

| Initial State | Allowed Target State | Trigger Condition | Authorization Required |
| :--- | :--- | :--- | :---: |
| **`CREATED`** | `PROCESSING` | Payment confirmed & inventory reserved | `ROLE_ADMIN` |
| **`CREATED`** | `CANCELLED` | Customer cancellation before fulfillment | `ROLE_USER` / `ROLE_ADMIN` |
| **`PROCESSING`**| `COMPLETED` | Carrier delivery confirmation | `ROLE_ADMIN` |
| **`PROCESSING`**| `CANCELLED` | Inventory deficit / fulfillment failure | `ROLE_ADMIN` |
| **`COMPLETED`** | *Terminal* | Immutable record; archive state | None |
| **`CANCELLED`** | *Terminal* | Immutable record; refund issued | None |

### Dynamic Prompt Grounding Implementation

The system implements a secure grounding pattern to prevent data leaks. Before dispatching queries to Gemini, `AiService` executes:

1. **Identity Extraction:** Extracts the authenticated user identity from Spring Security's `SecurityContextHolder`.
2. **Authorized Retrieval:** Queries the relational persistence store strictly for orders owned by the caller (`orderRepository.findByCreatedBy(user)`).
3. **Context Injection:** Synthesizes the retrieved order records into a constrained system prompt template.
4. **Execution:** Dispatches the grounded prompt and customer query to Gemini Flash.

```java
// Logic implemented in AiService.java
String ordersData = userOrders.stream()
    .map(order -> String.format("- Order #%s: %s", order.getOrderNumber(), order.getStatus()))
    .collect(Collectors.joining("\n"));

return chatClient.prompt()
    .system(s -> s.text(SYSTEM_PROMPT_TEMPLATE)
        .param("name", user.getFirstName())
        .param("orders_data", ordersData))
    .user(userQuery)
    .call()
    .content();
```

---

## Core API Specification

All endpoints are prefixed with `/api/v1`.

| Resource | Method & Path | Authorization | Description |
| :--- | :--- | :---: | :--- |
| **Authentication** | `POST /api/v1/auth/signup` | Public | Registers a new user account with BCrypt password hashing. |
| **Authentication** | `POST /api/v1/auth/login` | Public | Authenticates credentials and returns Access & Refresh tokens. |
| **Authentication** | `POST /api/v1/auth/refresh` | Public | Issues a new access token using a valid refresh token. |
| **AI Support** | `POST /api/v1/ai/chat` | **Authenticated** | Queries the grounded AI assistant regarding personal order records. |
| **Orders** | `POST /api/v1/orders` | `ROLE_USER` | Creates a new order with automated tax calculations. |
| **Orders** | `GET /api/v1/orders` | `ROLE_USER` / `ROLE_ADMIN` | Retrieves paginated orders (users see own; admins see fleet). |
| **Orders** | `GET /api/v1/orders/{orderNumber}/status` | Public | Retrieves public tracking status for an order number. |
| **Orders** | `PATCH /api/v1/orders/{orderNumber}/status`| `ROLE_ADMIN` | Transitions order lifecycle status along audited FSM paths. |

---

## Quickstart & Platform Orchestration

### Prerequisites
- Java 17 LTS & Maven 3.9+ (wrapper included)
- Google AI Studio API Key ([Get API Key](https://aistudio.google.com/apikey))
- MySQL 8.0 (Optional; H2 in-memory database used by default for local development)

---

### Option A: Local Development Launch with H2 (Recommended)

Run the entire platform with embedded H2 persistence and live Swagger documentation:
```bash
# Clone repository
git clone https://github.com/i-viki/order-management-service.git
cd order-management-service

# Create local environment config
cp .env.example .env
# Edit .env and insert your GEMINI_API_KEY

# Launch application via Maven wrapper
./mvnw spring-boot:run
```
*Application boots on `http://localhost:8080` with in-memory H2 database active.*

---

### Option B: Production MySQL Launch

#### Step 1: Start MySQL Datastore
```bash
docker run --name order-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=order_management -p 3306:3306 -d mysql:8.0
```

#### Step 2: Configure Environment Profile
Set `SPRING_PROFILES_ACTIVE=prod` in `.env` and configure MySQL credentials:
```properties
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://localhost:3306/order_management
DB_USERNAME=root
DB_PASSWORD=root
GEMINI_API_KEY=your_gemini_api_key_here
JWT_SECRET=your_super_secret_jwt_key_at_least_32_characters_long
```

#### Step 3: Boot Application
```bash
./mvnw spring-boot:run
```

---

## Decoupled Environment Strategy

All services extract hostnames, ports, and external LLM keys into environment files loaded via `dotenv-java`:

| Variable | Default (Dev) | Purpose |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev` for H2, `prod` for MySQL). |
| `SERVER_PORT` | `8080` | Port for the HTTP / REST application server. |
| `GEMINI_API_KEY` | *(Required)* | Google AI Studio API key for Gemini Flash reasoning. |
| `JWT_SECRET` | *(256-bit key)* | HMAC-SHA secret for token minting and signature validation. |
| `JWT_EXPIRATION_MS` | `3600000` | Access token lifespan in milliseconds (1 hour). |
| `DB_URL` | `jdbc:h2:mem:orderdb` | JDBC connection string. Set to MySQL connection in prod. |
| `DB_USERNAME` | `sa` | Relational database username. |
| `DB_PASSWORD` | *(empty)* | Relational database password. |
| `HIBERNATE_DIALECT` | `org.hibernate.dialect.H2Dialect` | Hibernate dialect matching target datastore. |

---

## Live Telemetry & Observability Hubs

| Hub / Service | Port | URL | Role & Live Features |
| :--- | :--- | :--- | :--- |
| **Interactive Swagger UI** | `8080` | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | OpenAPI 3.0 interactive API documentation and execution sandbox. |
| **OpenAPI Schema** | `8080` | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Raw JSON OpenAPI 3 specification for client SDK generation. |
| **H2 Database Console** | `8080` | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | In-memory relational inspection console (`jdbc:h2:mem:orderdb`). |
| **Actuator Health** | `8080` | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Application health checks, datasource validation, and JVM metrics. |

---

## Testing & Quality Assurance Suite

### Unit & Integration Test Suites
```bash
./mvnw test
# Validates order lifecycle state transitions, MapStruct bindings, and security filters.
```

### AI Support Query with Grounded Orders
```bash
curl -X POST http://localhost:8080/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "message": "Where is my latest order and what items are included?"
  }'
```

### Order Creation Sample Request
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "items": [
      { "productName": "Ergonomic Mechanical Keyboard", "quantity": 1, "unitPrice": 149.99 },
      { "productName": "USB-C Braided Cable", "quantity": 2, "unitPrice": 19.99 }
    ]
  }'
```

---

## Author

**Jayavignesh**
- **Website:** [jayavignesh.dev](https://jayavignesh.dev)
- **GitHub:** [@i-viki](https://github.com/i-viki)
- **Email:** [hello@jayavignesh.dev](mailto:hello@jayavignesh.dev)

---

## License

This platform is distributed under the **MIT License**. See the [`LICENSE`](LICENSE) file for complete terms.
