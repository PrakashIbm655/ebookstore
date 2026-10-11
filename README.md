# E-Bookstore API

A production-ready Spring Boot REST API backend for an e-bookstore (e-commerce) platform. The application provides endpoints for user authentication, product catalogue browsing, shopping cart management, mock payment processing, order lifecycle management, and collaborative-filtering product recommendations.

---

## Prerequisites

- **Java**: JDK 21
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 15+

---

## Environment Variables

The application requires the following environment variables. In compliance with security standards, secrets and credentials are never hardcoded.

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | JDBC connection URL for PostgreSQL | `jdbc:postgresql://localhost:5432/ebookstore` |
| `DB_USERNAME` | PostgreSQL database user | `postgres` |
| `DB_PASSWORD` | PostgreSQL database password | `postgres` |
| `JWT_SECRET` | Secret key for signing JWTs (min 32 characters / 256 bits) | `c2VjdXJlUmFuZG9tSldUU2VjcmV0S2V5Rm9yRUJvb2tzdG9yZTIwMjY=` |
| `JWT_EXPIRY_MS` | JWT token expiration time in milliseconds (default: 3600000 / 1 hour) | `3600000` |

---

## Database Setup

Connect to your PostgreSQL server and execute the following SQL commands to create the database:

```sql
CREATE DATABASE ebookstore;
```

When the application runs, Hibernate automatically generates and updates the schema (`ddl-auto: update`), and the initial seed data (`data.sql`) is loaded into the database.

---

## Build & Run

### 1. Build the Application

```bash
mvn clean package
```

### 2. Run the Application

#### Linux / macOS / Bash:
```bash
export DB_URL="jdbc:postgresql://localhost:5432/ebookstore"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_password"
export JWT_SECRET="your-32-plus-character-secret-key-for-jwt-signing"
export JWT_EXPIRY_MS="3600000"

java -jar target/ebookstore-0.0.1-SNAPSHOT.jar
```

#### Windows PowerShell:
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/ebookstore"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-32-plus-character-secret-key-for-jwt-signing"
$env:JWT_EXPIRY_MS="3600000"

java -jar target/ebookstore-0.0.1-SNAPSHOT.jar
```

The application starts on `http://127.0.0.1:8080`.

---

## API Documentation

Interactive Swagger UI and OpenAPI documentation are available when the server is running:

- **Swagger UI**: [http://127.0.0.1:8080/swagger-ui.html](http://127.0.0.1:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://127.0.0.1:8080/api-docs](http://127.0.0.1:8080/api-docs)

---

## Sample API Calls

Below are sample `curl` commands demonstrating key platform workflows.

### 1. Register a New User

```bash
curl -X POST http://127.0.0.1:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "reader@example.com",
    "password": "Password123!",
    "fullName": "Jane Doe"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "reader@example.com",
  "fullName": "Jane Doe",
  "roles": ["ROLE_USER"]
}
```

### 2. Login

```bash
curl -X POST http://127.0.0.1:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "reader@example.com",
    "password": "Password123!"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "reader@example.com",
  "fullName": "Jane Doe",
  "roles": ["ROLE_USER"]
}
```

> **Note:** Save the returned `token` and use it in the `Authorization: Bearer <TOKEN>` header for protected endpoints.

### 3. Browse Products (Public)

```bash
curl -X GET "http://127.0.0.1:8080/products?page=0&size=10"
```

### 4. Add Item to Shopping Cart (Authenticated)

```bash
curl -X POST http://127.0.0.1:8080/cart/items \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### 5. Initiate Payment (Authenticated)

```bash
curl -X POST http://127.0.0.1:8080/payments/initiate \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": 1,
    "addressId": 1,
    "method": "CREDIT_CARD",
    "giftPointsToUse": 0
  }'
```

### 6. Get Order History (Authenticated)

```bash
curl -X GET "http://127.0.0.1:8080/orders?page=0&size=10" \
  -H "Authorization: Bearer <TOKEN>"
```

---

## Project Structure

```
ebookstore/
├── src/
│   ├── main/
│   │   ├── java/com/ebookstore/
│   │   │   ├── config/         # Security, OpenAPI, and web configuration
│   │   │   ├── controller/     # REST API controllers
│   │   │   ├── dto/            # Request and response DTOs
│   │   │   ├── exception/      # Global exception handler and custom exceptions
│   │   │   ├── model/          # JPA entities (User, Product, Cart, Order, Payment, etc.)
│   │   │   ├── repository/     # Spring Data JPA repositories and specifications
│   │   │   ├── security/       # JWT utilities, user details, and authentication filters
│   │   │   └── service/        # Core business logic and interfaces
│   │   └── resources/
│   │       ├── application.yml # Application configuration
│   │       └── data.sql        # Seed data (categories, brands, products)
│   └── test/                   # Unit and integration test suites
├── pom.xml                     # Maven project definition & dependencies
└── README.md                   # Project documentation
```
