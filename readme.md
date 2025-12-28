# Recruitment Management System

A **Spring Boot–based Recruitment Management System** designed to streamline hiring workflows, candidate management, authentication, email notifications, and file/image handling.
The system is built using **Spring Boot 3.5**, **Java 21**, **MySQL**, **JWT-based security**, **caching**, and **cloud media integrations**.

---

## Tech Stack

### Backend

* Java 21
* Spring Boot 3.5.5
* Spring MVC
* Spring Data JPA (Hibernate)
* Spring Security (JWT Authentication)
* Spring Validation
* Spring Mail (SMTP)
* Spring Cache (Caffeine)
* Spring AOP
* Spring Retry

### Database

* MySQL 8+

### Build Tool

* Maven

### Utilities & Integrations

* JWT (jjwt)
* ModelMapper
* MapStruct
* Cloudinary (Image Uploads)
* Apache POI (Excel processing)
* SpringDoc OpenAPI (Swagger UI)
* HikariCP (Connection Pooling)

---

## Features

* Secure authentication using JWT
* Role-based access control
* Recruitment workflow management
* Email notifications (Gmail SMTP)
* Image upload and storage (Cloudinary / ImageKit)
* API documentation via Swagger UI
* Caching with Caffeine for performance optimization
* Database connection pooling using HikariCP
* Server response compression enabled

---

## Prerequisites

Before running the project, ensure you have:

* **Java 21**
* **Maven 3.9+**
* **MySQL 8+**
* **Git**

---

## Project Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/RecruitmentManagementSystem.git
cd RecruitmentManagementSystem
```

---

### 2. Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE recruitment_db;
```

Update the following properties in `application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recruitment_db
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD
```

---

### 3. JWT Configuration

JWT secret is required for authentication:

```properties
jwt.secret=YOUR_BASE64_SECRET
```

Use a strong Base64-encoded secret in production.

---

### 4. Email Configuration (Gmail SMTP)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> Use **Gmail App Password**, not your actual Gmail password.

---

### 5. Cloudinary Configuration (Image Upload)

```properties
cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET
```

---

### 6. Caching Configuration (Caffeine)

Already enabled by default:

```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
```

No external service is required.

---

### 7. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## API Documentation (Swagger)

Once the application is running, access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

or

```
http://localhost:8080/swagger-ui/index.html
```

---

## Configuration Highlights

* **DDL Auto Update**

  ```properties
  spring.jpa.hibernate.ddl-auto=update
  ```

* **Connection Pooling (HikariCP)**

  ```properties
  spring.datasource.hikari.maximum-pool-size=20
  ```

* **Server Compression Enabled**
  Improves API response performance.

* **Spring Logs Disabled by Default**

  ```properties
  logging.level.org.springframework=OFF
  ```

---