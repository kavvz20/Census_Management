# Census Management System (Java Spring Boot + Thymeleaf + SQLite / MySQL)

[![Live Demo](https://img.shields.io/badge/Live%20Demo-AWS%20EC2-orange?style=for-the-badge&logo=amazonec2)](http://13.54.219.4)
[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![SQLite](https://img.shields.io/badge/Database-SQLite%20%7C%20MySQL-blue?style=for-the-badge&logo=sqlite)](https://www.sqlite.org/)

---

### 🌐 Live Cloud Deployment
The project is hosted and running live on AWS EC2 (Free Tier):
👉 **[http://13.54.219.4](http://13.54.219.4)**

#### Quick Demo Login Credentials:
| Portal | Username | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **National Admin** | `admin` | `admin123` | Full demographic charts, regions, enumerators, CSV export |
| **Field Enumerator** | `enum_mumbai` | `enum123` | Mumbai jurisdiction (`/enumerator-home`, register households/citizens) |
| **Field Enumerator** | `enum_bangalore` | `enum123` | Bengaluru jurisdiction |

---

## 📌 Executive Overview

The Census Management System enables municipal and governmental administrative bodies to efficiently digitize, manage, and analyze national population demographics. 
- **Admins** manage hierarchical geographic jurisdictions (*State &rarr; District &rarr; City*), create and provision field enumerator accounts, monitor account status, view real-time graphical population statistics, and export demographic data to CSV.
- **Enumerators** are strictly restricted to their designated geographic region. They register households, record infrastructure and utility access, and manage family member census records.

> **Key Academic / Viva Highlight**: Unlike client-only permission models, all security, role checks, and regional isolation boundaries are strictly enforced in the Java Spring Boot service layer and database query filters.

---

## 🚀 1-Click Ultra-Easy Startup

You can start the entire application with a single click:

### Double-Click `run.bat` or `start.bat` (Windows - Recommended)
Simply **double-click** [run.bat](file:///e:/cencus-management-system/run.bat) or [start.bat](file:///e:/cencus-management-system/start.bat) in the project root:
- **Zero Server Setup**: Automatically creates and uses the embedded **SQLite** single-file database (`census.db`). No MySQL server, no usernames, no passwords needed!
- **Auto-Detects Java**: Automatically discovers JDK 17+ on your machine.
- **Instant Launch**: Starts the pre-packaged executable JAR in ~3 seconds.
- **Auto-Browser Launch**: Automatically opens your default web browser to **`http://localhost:8080/login`**.
- **Auto-Seeded Data**: Pre-seeds 4 regions, users, 5 households, and 15 citizens on first boot.

### Linux / macOS / Git Bash
```bash
./run.sh
```

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 17+ (Fully compatible with Java 17, 21, and 26) |
| **Framework** | Spring Boot 3.3.4 |
| **Web & Templating** | Spring Web MVC, Thymeleaf, Bootstrap 5, Bootstrap Icons, Chart.js |
| **Security** | Spring Security 6 with BCrypt password hashing, Session management, and JWT token authentication |
| **Persistence** | Spring Data JPA (Hibernate 6) |
| **Validation** | Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Past`, `@Size`, etc.) |
| **Databases** | **SQLite** (Default zero-config persistent file `census.db`) & **MySQL 8.0+** (Optional profile: `--mysql`) |
| **Build Tool** | Apache Maven (Maven wrapper `mvnw` & `mvnw.cmd` included) |

---

## 👥 Users & Roles

| Role | Access Permissions | Security Enforcement |
| :--- | :--- | :--- |
| **ADMIN** | Full nationwide access. Manages regions, enumerators (create, activate, deactivate), views demographic charts, and exports CSV. | Protected by `@PreAuthorize("hasRole('ADMIN')")` and `SecurityFilterChain` |
| **ENUMERATOR** | Restricted exclusively to their assigned region (`region_id`). Can only register households and citizens within their jurisdiction. | Filtered in Spring Boot service layer using authenticated `UserPrincipal.getRegionId()`. Accessing other regions yields **HTTP 403 Forbidden**. |

---

## 🚀 Quick Start Guide

### 1. Prerequisites
- **Java Development Kit (JDK)**: JDK 17 or newer installed.
- (Optional) **MySQL Server 8.0+** (If running MySQL profile; H2 runs out of the box with zero installations).

### 2. Run Instantly with In-Memory / File Database (Zero-Configuration)
No database installation required! Simply open a terminal in the project directory and run:

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on **`http://localhost:8080`**.
On startup, `DataInitializer` automatically seeds the database with administrative regions, demo accounts, households, and family members.

---

### 3. Running with MySQL Server

1. **Create the Database and Tables**:
   Open MySQL Workbench or MySQL CLI and run the included SQL script:
   ```cmd
   mysql -u root -p < census_database.sql
   ```
2. **Configure Database Credentials**:
   Inspect `src/main/resources/application-mysql.properties` and verify your username and password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/census_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=root
   ```
3. **Launch with MySQL Profile**:
   ```cmd
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 🔑 Default Credentials

| Username | Password | Role | Region Jurisdiction | Notes |
| :--- | :--- | :--- | :--- | :--- |
| `admin` | `admin123` | **ADMIN** | All Regions | Full administrative control & charts |
| `enum_mumbai` | `enum123` | **ENUMERATOR** | Andheri, Mumbai Suburban | Active field officer |
| `enum_bangalore` | `enum123` | **ENUMERATOR** | Koramangala, Bengaluru | Active field officer |
| `enum_delhi` | `enum123` | **ENUMERATOR** | Connaught Place, Delhi | **Deactivated** (Demo account to test 401/403 deactivation) |

---

## 🌐 Application Navigation & Pages

- **Sign In (`/login`)**: Secure authentication with role-based redirection.
- **Admin Dashboard (`/dashboard`)**:
  - Live demographic KPI cards (Total Population, Households, Male/Female counts, Gender Ratio, Literacy Rate).
  - Dynamic Chart.js visualizations:
    - *Gender Distribution* (Doughnut)
    - *Age Groups Breakdown: <18, 18-35, 36-60, 60+* (Bar Chart)
    - *Literacy vs Illiteracy* (Pie Chart)
  - Recent households and rapid action toolbar.
- **Enumerator Home (`/enumerator-home`)**:
  - Jurisdiction display with state/district/city coordinates.
  - Regional statistics and quick access to record households.
- **Regions Management (`/regions`)**:
  - Add, edit, search, and delete administrative boundaries.
  - Enforces `UNIQUE(state, district, city)` constraint.
- **Enumerator Management (`/enumerators`)**:
  - Register enumerator accounts and bind them to regions.
  - One-click **Activate / Deactivate** toggle with instant security propagation.
- **Household Registry (`/households`)**:
  - Paginated list with keyword and regional search.
  - Infrastructure tracking: dwelling type, treated water, electricity connection, contact number.
- **Household Detail & Family Tree (`/households/{id}`)**:
  - Individual household profile.
  - Family members registry with relationship to head, occupation, education, and marital status.
  - Friendly duplicate detection if National ID is already registered.
- **Citizens Directory (`/persons`)**:
  - Searchable list of all registered citizens.
- **Analytical Reports (`/reports`)**:
  - Interactive regional filter dropdown.
  - Real-time recalculation of gender ratios and age pyramids.
  - **One-click CSV Export** (`/reports/export`) for external demographic reporting.

---

## 📡 REST API Reference

The project provides REST endpoints matching the specification:

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Anyone | Authenticate with username & password, returns JWT token |
| `GET` | `/api/regions` | Admin | Retrieve paginated or full region list (`?all=true`) |
| `POST` | `/api/regions` | Admin | Create a new geographic region |
| `PUT` | `/api/regions/{id}` | Admin | Update existing region coordinates |
| `DELETE` | `/api/regions/{id}` | Admin | Delete region |
| `GET` | `/api/admin/enumerators`| Admin | List enumerators with search & pagination |
| `POST` | `/api/admin/enumerators`| Admin | Create field enumerator with assigned region |
| `PATCH`| `/api/admin/enumerators/{id}/status` | Admin | Activate or deactivate enumerator account |
| `GET` | `/api/households` | Admin / Enumerator (Own region) | Paginated list of households with keyword search |
| `POST` | `/api/households` | Admin / Enumerator (Own region) | Register a new household |
| `GET` | `/api/households/{id}` | Admin / Enumerator (Own region) | Retrieve household details |
| `GET` | `/api/persons` | Admin / Enumerator (Own region) | Search citizens (`?name=&householdId=&page=&size=`) |
| `POST` | `/api/persons` | Admin / Enumerator (Own region) | Add citizen with National ID uniqueness check |
| `GET` | `/api/reports/summary` | Admin | Returns demographic metrics (`?regionId=`) |
| `GET` | `/api/reports/age-groups` | Admin | Returns age distribution (`?regionId=`) |
| `GET` | `/api/reports/export` | Admin | Downloads comprehensive CSV dataset (`?regionId=`) |

### Example REST API Call (cURL)

**1. Login & Obtain JWT Token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "admin123"}'
```

**2. Access Protected Endpoint with Token:**
```bash
curl -X GET http://localhost:8080/api/reports/summary \
     -H "Authorization: Bearer <TOKEN>"
```

---

## 🔒 Security Architecture Highlights

1. **Password Encryption**: All passwords stored as one-way salted hashes using BCrypt (strength 10).
2. **Deactivation Filter**: Every incoming request (Session or JWT) checks `user.isActive()` against the database. Deactivated accounts are rejected immediately even if their token has not expired.
3. **Region Boundary Enforcement**:
   - An enumerator's allowed jurisdiction is extracted exclusively from the authenticated security principal (`UserPrincipal.getRegionId()`), never trusted from client parameters.
   - If an enumerator modifies the URL to inspect or edit a record in another region, Spring Boot intercepts the call and terminates with **403 Forbidden**.
4. **Data Integrity & Duplicate Protection**:
   - National ID (`id_number`) is guarded with a database `UNIQUE` constraint and pre-checked in the service layer, displaying user-friendly feedback (`DuplicateResourceException` &rarr; 409 Conflict).
   - SQL injection is completely prevented via parameterized queries in Spring Data JPA and Hibernate.
