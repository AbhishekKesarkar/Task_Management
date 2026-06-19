# TaskFlow — Task Management API (Backend)

A Spring Boot REST API for a task management system with **JWT authentication** and **role-based access control (RBAC)**, where Admins create and assign tasks, and Users track and update their assigned work.

---

## Features

- **JWT Authentication** — stateless login/register with secure token-based sessions
- **Role-Based Access Control (RBAC)** — two roles: `ADMIN` and `USER`, enforced at both controller and service layers
- **Admin capabilities**
  - Create and assign tasks to any user
  - View all tasks across the system
  - Edit task details (title, description, priority, due date, reassignment)
  - Delete tasks
  - View and manage all registered users
- **User capabilities**
  - View only tasks assigned to them
  - Update the status of their own tasks (`TODO` → `IN_PROGRESS` → `DONE`)
  - View overdue tasks
- **Two-layer authorization** — `@PreAuthorize` for role-level checks, service-layer ownership checks for resource-level checks
- **Global exception handling** — consistent JSON error responses across the API
- **Password hashing** with BCrypt

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.5**
- **Spring Security**
- **JWT (JJWT 0.12.5)**
- **Spring Data JPA** / Hibernate
- **MySQL**
- **Maven**

---

## Project Structure

```
task-manager/
├── src/main/java/com/task_manager/
│   ├── config/           # Security, JWT filter, CORS config
│   ├── controller/       # REST controllers
│   ├── service/          # Business logic + RBAC enforcement
│   ├── repository/       # Spring Data JPA repositories
│   ├── entity/           # JPA entities (User, Task) + enums
│   ├── dto/
│   │   ├── request/        # Incoming request DTOs
│   │   └── response/       # Outgoing response DTOs
│   ├── security/         # JwtUtil, CustomUserDetails, UserDetailsServiceImpl
│   ├── exception/         # Custom exceptions + GlobalExceptionHandler
│   └── util/              # ApiResponse wrapper
└── src/main/resources/
    └── application.properties
```

---

## Database Schema

**users**
| Column | Type | Notes |
|---|---|---|
| id | bigint | PK, auto-increment |
| username | varchar | unique |
| email | varchar | unique |
| password | varchar | BCrypt hashed |
| full_name | varchar | |
| role | enum | `ADMIN`, `USER` |
| created_at | datetime | |

**tasks**
| Column | Type | Notes |
|---|---|---|
| id | bigint | PK, auto-increment |
| title | varchar | |
| description | varchar(1000) | |
| status | enum | `TODO`, `IN_PROGRESS`, `DONE` |
| priority | enum | `LOW`, `MEDIUM`, `HIGH` |
| due_date | date | |
| assigned_user_id | bigint | FK → users.id |
| created_by_id | bigint | FK → users.id (the admin who created it) |
| created_at / updated_at | datetime | |

---

## API Endpoints

| Endpoint | Method | Access | Description |
|---|---|---|---|
| `/api/auth/register` | POST | Public | Register a new user (always created as `USER`) |
| `/api/auth/login` | POST | Public | Login, returns JWT |
| `/api/users` | GET | ADMIN | List all users |
| `/api/tasks` | POST | ADMIN | Create and assign a task |
| `/api/tasks` | GET | ADMIN | View all tasks |
| `/api/tasks/my` | GET | Authenticated | View tasks assigned to current user |
| `/api/tasks/my/overdue` | GET | Authenticated | View current user's overdue tasks |
| `/api/tasks/{id}/status` | PATCH | Authenticated (own tasks) | Update task status |
| `/api/tasks/{id}` | PUT | ADMIN | Edit full task details |
| `/api/tasks/{id}` | DELETE | ADMIN | Delete a task |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL 8+

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/<your-username>/task-manager.git
   cd task-manager
   ```

2. Create a MySQL database
   ```sql
   CREATE DATABASE task_manager_db;
   ```

3. Configure `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/task_manager_db
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   spring.jpa.hibernate.ddl-auto=update

   jwt.secret=your-base64-encoded-256-bit-secret
   jwt.expiration=86400000
   ```

4. Run the application
   ```bash
   mvn spring-boot:run
   ```
   Server starts on `http://localhost:8080`

---

## Testing the API

A sample flow using Postman or curl:

1. `POST /api/auth/register` — create a user
   ```json
   {
     "username": "abhi",
     "email": "abhi@example.com",
     "password": "password123",
     "fullName": "Abhi Sharma"
   }
   ```

2. `POST /api/auth/login` — get a JWT token
   ```json
   {
     "username": "abhi",
     "password": "password123"
   }
   ```

3. Promote a user to `ADMIN` directly in MySQL (no self-promotion endpoint, by design):
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE username = 'abhi';
   ```

4. Log in again to get a fresh token reflecting the new role

5. `POST /api/tasks` as Admin to create and assign a task
   ```json
   {
     "title": "Build login page",
     "description": "Create the login UI with email and password fields",
     "priority": "HIGH",
     "dueDate": "2026-06-30",
     "assignedUserId": 2
   }
   ```

6. `GET /api/tasks/my` as the assigned user to view it (with `Authorization: Bearer <token>` header)

7. `PATCH /api/tasks/{id}/status` to update progress
   ```json
   {
     "status": "IN_PROGRESS"
   }
   ```

---

## Security Design Notes

- Passwords are hashed with **BCrypt** before storage
- JWTs are signed with **HS256**, validated on every request via a custom `JwtAuthenticationFilter`
- Role checks use `@PreAuthorize("hasRole('ADMIN')")` at the controller level
- Ownership checks (e.g., a user can only update their own task) are enforced in the **service layer**, not just the controller — preventing privilege escalation via direct API calls
- No self-registration as `ADMIN` — admin roles are assigned manually, by design
- Stateless sessions — no server-side session storage, JWT carries all auth state

---

## Future Improvements

- Refresh token support
- Pagination and filtering on task lists
- Email notifications for task assignment and due dates
- Swagger / OpenAPI documentation
- Deployment on Railway / Render

---

## Author

**Abhishek Kesarkar**
Java Developer
