# Recruitment Management System

A **Spring Boot–based Recruitment Management System** designed to streamline hiring workflows, candidate management, authentication, email notifications, and file/image handling.
The system is built using **Spring Boot 3.5**, **Java 21**, **MySQL**, **JWT-based security**, **caching**, and **cloud media integrations**.

---

## Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Features](#features)
4. [Prerequisites](#prerequisites)
5. [Quick Start (Clone & Run)](#quick-start-clone--run)
6. [Environment / Configuration](#environment--configuration)
7. [Default seeded credentials](#default-seeded-credentials)
8. [Database Setup](#database-setup)
9. [Run with Docker](#run-with-docker)
10. [API Documentation (Swagger)](#api-documentation-swagger)

---

## Overview

This repository contains a recruitment management backend service built with Spring Boot. It provides endpoints for user and role management, job postings, interviews, candidate tracking, and notification workflows.

## Tech Stack

See original file for full details. Key items:

* Java 21
* Spring Boot 3.5.x
* Spring Security (JWT)
* Spring Data JPA (Hibernate)
* MySQL
* Maven

---

## Features

* Role-based authentication and authorization (ADMIN, RECRUITER, HR, INTERVIEWER, CANDIDATE, etc.)
* JWT-based auth with refresh token support
* Email notifications for candidate workflows
* Image upload (Cloudinary/ImageKit integration)
* Caching (Caffeine)
* Swagger API documentation

---

## Prerequisites

* Java 21 (JDK)
* Maven 3.9+ (or use the included Maven wrapper)
* MySQL 8+
* Git

---

## Quick Start (Clone & Run)

1. Clone the repository and change directory:

```bash
git clone https://github.com/your-username/RecruitmentManagementSystem.git
cd RecruitmentManagementSystem
```

2. Copy or update properties (see [Environment / Configuration](#environment--configuration)).

3. Run the application:

- On Windows (using included wrapper):

```powershell
.\mvnw.cmd clean install; .\mvnw.cmd spring-boot:run
```

- On macOS / Linux:

```bash
./mvnw clean install && ./mvnw spring-boot:run
```

After startup, the server will be available at http://localhost:8080

Notes:
- If you already built the project once, you can run only the Spring Boot plugin: `mvnw spring-boot:run`.
- The app prints a console message on successful startup: `Recruitment Management System Live On : http://localhost:8080`.

---

## Environment / Configuration

This project uses Spring Boot configuration files located under `src/main/resources`. There are multiple profiles (for example `application-local.properties` and `application-prod.properties`). For local development, update `src/main/resources/application-local.properties` (or `application.properties`) with your local settings.

Important configuration keys (examples):

```properties
# Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/recruitment_db
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD

# JPA
spring.jpa.hibernate.ddl-auto=update

# Frontend URL (CORS settings)
app.frontend.url=YOUR_FRONTEND_URL

# JWT
jwt.secret=YOUR_BASE64_SECRET
jwt.expiration-ms=3600000

# Cloudinary
cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET

# Email (Gmail SMTP example)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Tips:
- For local development, create a copy `application-local.properties` and set the profile to `local` if you use active profiles. Alternatively set `SPRING_PROFILES_ACTIVE=local`.
- or add this key and value to `application.properties`:
- Keep secrets (passwords, API keys) out of source control. Use OS environment variables or a secrets manager where possible.

---

## Default seeded credentials

The application seeds a default super-admin account when role records are created (see `PreRunner` class). These are for development/testing only.

- Username: `superAdmin`
- Email: `superAdmin@gmail.com`
- Password: `Super@dmin.018`
- Role: `ADMIN`

If you plan to run this in any staging/production-like environment, immediately change this password or remove the seeding logic.

---

## Database Setup

1. Create a local database:

```sql
CREATE DATABASE recruitment_db;
```

2. Verify JDBC URL and credentials in `application-local.properties`.

3. The application uses `spring.jpa.hibernate.ddl-auto=update` by default, which will automatically create/update schema objects based on entities. For stricter control, consider using migrations (Flyway or Liquibase) and change the JPA DDL property.

---

## API Documentation (Swagger)

Swagger UI is available once the application is running. Common paths:

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/swagger-ui/index.html
```

Inspect the controllers under `src/main/java/.../controllers` to see exposed endpoints and security constraints.

---

*Last updated: 2026-01-12*
