# Spring Boot OTP Inquiry Service

A backend application built with **Spring Boot** that demonstrates a basic CRUD workflow with **OTP-based authentication, Redis session management, Oracle database persistence, JPA/Hibernate, and centralized exception handling**.

The project is designed as a practical demonstration of how authentication and authorization can be added to a traditional CRUD application.

---

## 🚀 Features

* Client login using **BOID and PAN**
* OTP generation using `SecureRandom`
* OTP storage in **Redis** with TTL
* Transaction ID based OTP verification
* Session/access-token generation after successful OTP validation
* Redis-based session management
* Bearer token authentication for protected APIs
* Client inquiry/read operation
* Accept/Reject client decision
* Prevent duplicate client decisions
* Oracle database persistence using **JPA/Hibernate**
* HikariCP database connection pooling
* DTO validation using Jakarta Bean Validation
* Centralized exception handling using `@RestControllerAdvice`
* Database exception handling
* Separation of Controller, Service, Repository, DTO and Entity layers

---

## 🏗️ Architecture

```text
                         Client / Postman
                                │
                                ▼
                         Spring Boot API
                                │
              ┌─────────────────┴─────────────────┐
              │                                   │
              ▼                                   ▼
       Authentication Flow                  Inquiry / CRUD
              │                                   │
              ▼                                   ▼
        Login Controller                   Inquiry Controller
              │                                   │
              ▼                                   ▼
         Login Service                      Inquiry Service
              │                                   │
              ├──────────────┐                    ▼
              │              │              JPA Repository
              ▼              ▼                    │
           Oracle          Redis                  ▼
          (Client)      (OTP/Session)           Oracle
              │                                   │
              └──────────────┬────────────────────┘
                             │
                             ▼
                  Global Exception Handler
```

---

# 🔐 Authentication Flow

The application uses a two-step authentication process.

### Step 1 — Client Login

The client submits:

```json
{
  "boid": 1234567890123456,
  "pan": "ABCDE1234F"
}
```

The application:

1. Validates the request.
2. Checks the BOID/PAN combination against Oracle.
3. Generates a 6-digit OTP.
4. Creates a transaction ID.
5. Stores the OTP in Redis with a TTL.
6. Associates the transaction ID with the client.
7. Returns the transaction ID.

Example response:

```text
550e8400-e29b-41d4-a716-446655440000
```

---

### Step 2 — OTP Validation

The client submits:

```json
{
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "otp": "123456"
}
```

The application:

1. Retrieves the OTP from Redis.
2. Validates the OTP.
3. Retrieves the associated client ID.
4. Generates a session ID.
5. Stores the session ID in Redis.
6. Deletes the OTP and transaction data.
7. Returns the session ID.

The returned session ID is then used as a Bearer token.

---

# 🔑 Authorization

Protected endpoints require:

```http
Authorization: Bearer <session-id>
```

The session ID is validated against Redis.

Example:

```http
GET /otp-service/inquiry
Authorization: Bearer 550e8400-e29b-41d4-a716-446655440000
```

The application retrieves the client ID associated with the session from Redis before allowing access.

---

# 📡 API Endpoints

## 1. Login

### `POST /otp-service/login`

Authenticates the client using BOID and PAN and initiates OTP authentication.

### Request

```json
{
  "boid": 1234567890123456,
  "pan": "ABCDE1234F"
}
```

### Response

```text
<transaction-id>
```

---

## 2. Validate OTP

### `POST /otp-service/validate-otp`

Validates the OTP and creates an authenticated session.

### Request

```json
{
  "transactionId": "<transaction-id>",
  "otp": "123456"
}
```

### Response

```text
<session-id>
```

---

## 3. Client Inquiry

### `GET /otp-service/inquiry`

Returns client details for an authenticated session.

### Header

```http
Authorization: Bearer <session-id>
```

### Example Response

```json
{
  "clientId": "1234567890123456",
  "panNumber": "ABCDE1234F",
  "fullName": "John Doe",
  "dob": "1995-01-01",
  "addressLine1": "123 Main Street",
  "addressLine2": "Apartment 101",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "email": "john@example.com",
  "mobile": "9876543210",
  "kycStatus": "VERIFIED",
  "createdAt": "2026-09-01T10:00:00",
  "updatedAt": "2026-10-01T12:00:00"
}
```

---

## 4. Accept / Reject Client

### `POST /otp-service/decision`

Updates the client's decision.

### Header

```http
Authorization: Bearer <session-id>
```

### Request

Accept:

```json
{
  "decision": "ACCEPT"
}
```

Reject:

```json
{
  "decision": "REJECT"
}
```

### Response

```text
Client ACCEPT successfully
```

or

```text
Client REJECT successfully
```

A client cannot submit another decision after a decision has already been recorded.

---

# 🗄️ Database

The application uses **Oracle Database** for persistent client information.

Example `CLIENTS` table:

```text
CLIENT_ID
PAN_NUMBER
FULL_NAME
DOB
ADDRESS_LINE1
ADDRESS_LINE2
CITY
STATE
PINCODE
EMAIL
MOBILE
KYC_STATUS
DECISION
CREATED_AT
UPDATED_AT
```

JPA/Hibernate is used for database access.

---

# ⚡ Redis

Redis is used for temporary authentication data rather than storing OTPs and sessions in Oracle.

### OTP

```text
otp:INQUIRY_AUTH:<transactionId>
```

TTL:

```text
5 minutes
```

### Transaction → Client

```text
otp:TRANSACTION:<transactionId>
```

TTL:

```text
5 minutes
```

### Session

```text
otp:SESSION:<sessionId>
```

TTL:

```text
15 minutes
```

After successful OTP validation, the OTP and transaction data are deleted.

---

# 🛡️ Security Considerations

The project demonstrates several basic security practices:

* OTPs are stored temporarily in Redis.
* OTPs expire automatically using Redis TTL.
* Sessions expire automatically.
* `SecureRandom` is used for OTP generation.
* OTPs are not returned in API responses.
* Authentication tokens are not stored in the Oracle client table.
* Sensitive authentication information is not logged.
* Protected endpoints require Bearer authentication.
* Invalid authentication attempts return appropriate HTTP status codes.
* Database/internal exceptions are not exposed directly to clients.

> This project is intended as a learning/demo application and is not a complete production authentication system.

---

# ⚠️ Exception Handling

The application uses centralized exception handling through:

```java
@RestControllerAdvice
```

Examples of handled errors:

| Exception                           | HTTP Status | Meaning                       |
| ----------------------------------- | ----------: | ----------------------------- |
| `InvalidAuthenticationException`    |         401 | Invalid BOID/PAN or session   |
| `InvalidOtpAuthException`           |         401 | Invalid or expired OTP        |
| `ClientNotFoundException`           |         404 | Client does not exist         |
| `DecisionAlreadyProcessedException` |         409 | Client already has a decision |
| `DataAccessException`               |         500 | Database-related failure      |
| Generic `Exception`                 |         500 | Unexpected server error       |

This keeps controllers focused on request handling while error responses are managed centrally.

---

# 📁 Project Structure

```text
src/
└── main/
    └── java/
        └── com/
            └── otp/
                └── inquiry_service/
                    │
                    ├── controller/
                    │   ├── LoginController.java
                    │   ├── OtpController.java
                    │   ├── InquiryController.java
                    │   └── DecisionController.java
                    │
                    ├── dto/
                    │   ├── ClientLoginDto.java
                    │   ├── OtpValidationDto.java
                    │   ├── InquiryResponseDto.java
                    │   └── ClientDecisionDto.java
                    │
                    ├── entity/
                    │   └── Clients.java
                    │
                    ├── repository/
                    │   └── ClientsRepository.java
                    │
                    ├── service/
                    │   ├── LoginService.java
                    │   ├── OtpService.java
                    │   ├── InquiryService.java
                    │   └── DecisionService.java
                    │
                    └── exception/
                        ├── GlobalExceptionHandler.java
                        ├── InvalidAuthenticationException.java
                        ├── InvalidOtpAuthException.java
                        ├── ClientNotFoundException.java
                        └── DecisionAlreadyProcessedException.java
```

---

# 🛠️ Technologies

| Technology         | Purpose                      |
| ------------------ | ---------------------------- |
| Java               | Backend programming language |
| Spring Boot        | Application framework        |
| Spring Web         | REST APIs                    |
| Spring Data JPA    | Database persistence         |
| Hibernate          | ORM                          |
| Oracle Database    | Persistent storage           |
| Redis              | OTP and session storage      |
| Spring Data Redis  | Redis integration            |
| HikariCP           | Database connection pooling  |
| Lombok             | Boilerplate reduction        |
| Jakarta Validation | Request validation           |
| Maven              | Dependency management        |

---

# ⚙️ Configuration

Configure the following in `application.properties` or environment variables.

```properties
# Application
server.port=8080

# Oracle
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
spring.datasource.username=<username>
spring.datasource.password=<password>

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

**Do not commit real database passwords, Redis credentials, API keys, or other secrets to Git.**

For GitHub, use environment variables or an untracked local configuration file.

---

# ▶️ Running the Application

## 1. Start Oracle

Make sure the Oracle database and required schema are running.

## 2. Start Redis

If using Docker:

```bash
docker run --name otp-redis -p 6379:6379 -d redis
```

Verify:

```bash
docker ps
```

## 3. Start the Spring Boot application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the application from your IDE.

The application starts on:

```text
http://localhost:8080
```

---

# 🧪 Testing with Postman

Recommended testing sequence:

```text
1. POST /otp-service/login
          ↓
2. Obtain transactionId
          ↓
3. Retrieve OTP for local testing
          ↓
4. POST /otp-service/validate-otp
          ↓
5. Obtain sessionId
          ↓
6. GET /otp-service/inquiry
   Authorization: Bearer <sessionId>
          ↓
7. POST /otp-service/decision
   Authorization: Bearer <sessionId>
          ↓
8. Client is ACCEPTED / REJECTED
```

---

# 🔄 Overall Workflow

```text
                   ┌──────────────┐
                   │    Client    │
                   └──────┬───────┘
                          │
                    BOID + PAN
                          │
                          ▼
                   ┌──────────────┐
                   │ Login API    │
                   └──────┬───────┘
                          │
                    Validate Oracle
                          │
                          ▼
                   ┌──────────────┐
                   │ Generate OTP │
                   └──────┬───────┘
                          │
                          ▼
                    ┌───────────┐
                    │   Redis   │
                    │ OTP + TTL │
                    └─────┬─────┘
                          │
                    Validate OTP
                          │
                          ▼
                    Session Token
                          │
                          ▼
                    ┌───────────┐
                    │   Redis   │
                    │  Session  │
                    └─────┬─────┘
                          │
                   Bearer Authentication
                          │
             ┌────────────┴────────────┐
             ▼                         ▼
      Inquiry API               Decision API
             │                         │
             ▼                         ▼
          Oracle                    Oracle
             │                         │
             └────────────┬────────────┘
                          ▼
                    Client Data
```

---

# 📌 What This Project Demonstrates

This project demonstrates how a traditional CRUD backend can be extended with authentication and temporary state management.

The main concepts demonstrated are:

* REST API development
* Layered Spring Boot architecture
* DTO → Service → Repository → Entity flow
* JPA/Hibernate
* Oracle integration
* Redis integration
* OTP authentication
* Session/token management
* Bearer token authorization
* Validation
* Transaction management
* Connection pooling
* Centralized exception handling
* Basic business-rule enforcement

---

# 🚧 Possible Future Improvements

Some possible enhancements for a production-oriented version include:

* OTP delivery through email/SMS provider
* Refresh-token mechanism
* Rate limiting for login and OTP attempts
* Maximum OTP retry limits
* Account/session revocation
* Audit logging
* Structured JSON error responses
* Spring Security integration
* JWT-based authentication
* Redis-backed rate limiting
* Docker Compose for Oracle/Redis/application dependencies
* Unit and integration tests
* OpenAPI/Swagger documentation
* CI/CD pipeline

---

## 👨‍💻 Author

**Rohan Mathew**

This project was built as a practical demonstration of backend development using **Java, Spring Boot, Oracle, Redis, JPA, and REST APIs**.
