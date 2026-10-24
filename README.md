# 🎟️ Events Management API

A robust RESTful API developed with Spring Boot for managing events, discount coupons, locations, and users. This
project provides a secure and scalable backend solution for event organizers to handle event data, address associations,
and promotional codes, with JWT authentication and role-based access control.

> 🚧 **Under Development**  
> This project is currently under development and new features are continuously being added.

## 🛠️ Tech Stack

* **Java 21**: Leveraging the latest LTS features for performance and modern syntax.
* **Spring Boot 4.0.5**: Core framework for building the production-ready REST API.
* **Spring Security**: Stateless authentication and role-based authorization.
* **JWT (java-jwt / Auth0)**: Signed access tokens with configurable expiration.
* **Spring Data JPA**: For seamless ORM (Object-Relational Mapping) and database interaction.
* **PostgreSQL**: A powerful, open-source relational database for persistent data storage.
* **Flyway**: Version control for the database schema, ensuring consistent migrations across environments.
* **MapStruct**: High-performance, type-safe bean mapping between Entities and DTOs.
* **JUnit 5, Mockito & MockMvc**: Automated tests for the security layer.
* **Maven**: Dependency management and build automation.

## ✨ Key Features

* **Authentication & Authorization**:
    * Login endpoint that returns a signed JWT.
    * Stateless sessions: every protected request must send `Authorization: Bearer <token>`.
    * Role-based access control with three roles: `VIEWER`, `ORGANIZER` and `ADMIN`.
    * Invalid, expired, tampered or missing tokens are rejected with `401 Unauthorized`.
    * Authenticated users without the required role receive `403 Forbidden`.
* **User Management**:
    * Public signup and admin-only user administration.
* **Event Management**:
    * Create and list events with details like title, date, description, and visual assets (img_url).
    * Support for both remote and in-person events.
* **Coupon System**:
    * Link dynamic discount coupons to specific events.
    * Unique code validation and expiration/validity control.
* **Address Integration**:
    * Dedicated address management linked to events (City/UF).
* **Reliable Data Migrations**:
    * Managed database evolution through Flyway scripts (V1, V2, V3, etc.), preventing schema inconsistency.
* **Architecture & Patterns**:
    * **DTO Pattern**: Separation between database entities and API responses using Request/Response DTOs.
    * **Service Layer**: Decoupled business logic from controllers.
    * **Repository Pattern**: Abstracted data access layer for clean maintenance.
    * **Security Filter Chain**: A custom `JwtAuthenticationFilter` integrated into the Spring Security chain.

## 🔐 Security & Access Rules

| Endpoint                                                        | Access                                                  |
|-----------------------------------------------------------------|---------------------------------------------------------|
| `POST /api/auth/login`                                          | Public                                                  |
| `POST /api/user` (signup)                                       | Public                                                  |
| `GET /api/user`, `PUT /api/user/{id}`, `DELETE /api/user/{id}`  | `ADMIN` only                                            |
| `GET /api/event/**`, `GET /api/coupon/**`                       | Any authenticated user (`VIEWER`, `ORGANIZER`, `ADMIN`) |
| `POST`, `PUT`, `DELETE` on `/api/event/**` and `/api/coupon/**` | `ORGANIZER` or `ADMIN`                                  |
| Any other route                                                 | Authenticated                                           |

### Authentication flow

1. Send your credentials to `POST /api/auth/login`:

```json
    {
  "email": "user@example.com",
  "password": "your-password"
}
```

2. The API responds with a token:

```json
    {
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

3. Send the token in the `Authorization` header of every protected request:

```
    Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

## 🚀 Getting Started

Follow these steps to run the project locally:

1. **Clone the repository:**

```bash
    git clone https://github.com/LarissaOlimpio/events-api.git
```

2. **Configure the Database and Security:**
   Update the `src/main/resources/application.properties` with your PostgreSQL credentials and a JWT secret:

```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
# JWT secret (use a long, random value with 32+ characters)
api.security.token.secret=${JWT_SECRET:change-me-with-a-long-random-secret}
```

    > ⚠️ Never commit a real secret. In production, provide it through an environment variable (e.g. `JWT_SECRET`).

3. **Build and Install dependencies:**

```bash
    mvn clean install
```

4. **Run the application:**

```bash
    mvn spring-boot:run
```

## 🧪 Running Tests

The security layer is covered by `@WebMvcTest` integration tests that exercise the real filter chain with `MockMvc`:

```bash
mvn test
```

To run a single test class:

```bash
mvn test -Dtest=JwtAuthenticationIntegrationTest
mvn test -Dtest=RoleAuthorizationTest
```

* **`JwtAuthenticationIntegrationTest`**: valid and invalid login, protected route without token, valid token, expired
  token, tampered token and manually signed token.
* **`RoleAuthorizationTest`**: public routes, `401` without token, and the permission matrix for `VIEWER`, `ORGANIZER`
  and `ADMIN` on the user, event and coupon routes.

## 🏗️ Project Structure

```text
src/main/java/com/manager/events_api/
 ├── domain/             # Entities and Business Logic
 │    ├── address/       # Address domain (Entity, Service, Repository)
 │    ├── coupon/        # Coupon domain (Entity, Controller, Service, DTOs)
 │    ├── event/         # Event domain (Entity, Mapper, Controller, Service)
 │    └── user/          # User domain (Entity, Role, Controller, Service, Repository)
 ├── infra/
 │    └── security/      # AuthController, TokenService, JwtAuthenticationFilter, SecurityConfig
 ├── resources/
 │    ├── db/migration/  # Flyway SQL migration scripts
 │    └── application.properties

src/test/java/com/manager/events_api/
 └── infra/security/    # JWT and role authorization integration tests
```

Developed with ❤️ by Larissa Olimpio