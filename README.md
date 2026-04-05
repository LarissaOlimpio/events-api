# 🎟️ Events Management API

A robust RESTful API developed with Spring Boot for managing events, discount coupons, and locations. This project provides a scalable backend solution for event organizers to handle event data, address associations, and promotional codes.

## 🛠️ Tech Stack

This project was built using industry-standard backend technologies:

* **Java 21**: Leveraging the latest LTS features for performance and modern syntax.
* **Spring Boot 4.0.5**: Core framework for building the production-ready REST API.
* **Spring Data JPA**: For seamless ORM (Object-Relational Mapping) and database interaction.
* **PostgreSQL**: A powerful, open-source relational database for persistent data storage.
* **Flyway**: Version control for the database schema, ensuring consistent migrations across environments.
* **MapStruct**: High-performance, type-safe bean mapping between Entities and DTOs.
* **Maven**: Dependency management and build automation.

## ✨ Key Features

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

## 🚀 Getting Started

Follow these steps to run the project locally:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/LarissaOlimpio/events-api.git
    ```

2.  **Configure the Database:**
    Update the `src/main/resources/application.properties` with your PostgreSQL credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/database_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Build and Install dependencies:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

## 🏗️ Project Structure

```text
src/main/java/com/manager/events_api/
 ├── domain/             # Entities and Business Logic
 │    ├── address/       # Address domain (Entity, Service, Repository)
 │    ├── coupon/        # Coupon domain (Entity, Controller, DTOs)
 │    └── event/         # Event domain (Entity, Mapper, Controller)
 ├── resources/
 │    ├── db/migration/  # Flyway SQL migration scripts
 │    └── application.properties
 
```


 Developed with ❤️ by Larissa Olimpio
