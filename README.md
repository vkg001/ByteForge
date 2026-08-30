# ByteForge

> A full-stack competitive programming and online coding platform with a React frontend, Spring Boot backend, authentication, problem management, code execution, submissions, profiles, statistics, and contest-oriented backend modules.

ByteForge is designed around the core workflow of an online judge:

**Discover a problem → open the coding workspace → write code → submit → execute against test cases → receive a verdict → track your submissions and progress.**

The repository is organized as two independently runnable applications:

- `frontend/` — React + Vite client application
- `backend/` — Spring Boot REST API and application services

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Application Flow](#application-flow)
- [Repository Structure](#repository-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Frontend Pages](#frontend-pages)
- [Backend Modules](#backend-modules)
- [Code Execution and Submission Flow](#code-execution-and-submission-flow)
- [Authentication and Authorization](#authentication-and-authorization)
- [Data and Infrastructure](#data-and-infrastructure)
- [API Design](#api-design)
- [Testing](#testing)
- [Development Workflow](#development-workflow)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)
- [Completed Features](#completed-features)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

ByteForge is a full-stack coding platform intended to provide the main building blocks of an online judge and competitive-programming application.

The frontend provides a coding-oriented user experience with:

- Authentication screens
- Home/dashboard navigation
- Problem browsing
- Problem-specific coding workspace
- User profiles
- Theme support
- API-driven data fetching
- Monaco Editor integration for code editing

The backend provides the corresponding server-side application layer with modules for:

- User accounts and statistics
- Login and signup
- OTP-related authentication flows
- JWT-based security
- Problems and solved-problem tracking
- Submissions and submission events
- Contests
- Judge0-based code execution
- PostgreSQL persistence
- Redis integration
- Kafka integration
- Email services
- Health checks
- Global exception handling

The repository currently has a minimal root-level presentation, so this README is intended to serve as the main project guide.

---

## Key Features

### 1. Problem Solving Workspace

Users can browse coding problems and open an individual problem in a dedicated workspace.

The frontend defines the following routes:

| Route | Purpose |
|---|---|
| `/` | Home page |
| `/home` | Home page |
| `/login` | Login |
| `/signup` | Registration |
| `/problems` | Problem list |
| `/problems/:problemId` | Problem-specific coding workspace |
| `/profile` | Current user's profile |
| `/profile/:userId` | Another user's profile |

The workspace is backed by Monaco Editor, giving the application a VS Code-like editing experience in the browser.

### 2. Authentication

The application includes login and signup pages and a dedicated authentication module in the backend.

The backend also contains an OTP package and JWT configuration. On the frontend, authentication state is centralized through `AuthContext`.

The frontend API client automatically reads the JWT from browser storage and attaches it as a Bearer token to API requests.

### 3. Online Judge Integration

Code submissions are handled by a dedicated backend submission module and forwarded to a Judge0 integration layer.

The backend contains:

- `judge0/`
  - `Judge0Service`
  - Judge0 DTOs
- `submissions/`
  - controllers
  - DTOs
  - entities
  - enums
  - events
  - repositories
  - services
  - mappers

This separation keeps submission persistence and application logic independent from the external code-execution provider.

### 4. Problem Management

The backend separates problem functionality into dedicated areas for:

- Core problem management
- Solved problems
- Problem statistics

This makes it possible to evolve problem CRUD, solved-state tracking, and analytics independently.

### 5. User Profiles and Statistics

The backend contains dedicated user modules for:

- Core user functionality
- User statistics

The frontend includes both:

- `/profile`
- `/profile/:userId`

This supports both self-profile and user-profile views.

### 6. Contest Support

ByteForge now includes a complete contest experience across the platform. The contest domain is backed by dedicated:

- Controllers
- DTOs
- Entities
- Repositories
- Services

The implemented contest functionality includes contest creation and participation, standings and leaderboards, real-time contest timers, and contest-specific submission handling.

### 7. Centralized API Client

The frontend uses Axios with multiple client instances:

- General API client
- User-scoped API client
- Admin-scoped API client

The shared API client also:

- Sets JSON content headers
- Injects JWT authentication
- Handles `401 Unauthorized`
- Removes an expired token
- Redirects the user to `/login`

### 8. Modern Frontend State and Data Management

The frontend uses:

- TanStack React Query for server-state/data fetching
- Zustand for lightweight application state
- React Context for cross-cutting concerns such as authentication and theme
- React Router for navigation

### 9. Responsive, Component-Based UI

The frontend is organized around reusable components and feature-specific pages.

The current source layout includes:

- `components/`
- `context/`
- `pages/`
- `services/`
- `types/`
- `utils/`

Tailwind CSS is configured alongside the application's custom CSS.

---

## Technology Stack

### Frontend

| Technology | Role |
|---|---|
| React 19 | UI framework |
| Vite 8 | Development server and build tool |
| JavaScript / JSX / TypeScript | Application source |
| React Router | Client-side routing |
| Axios | HTTP client |
| TanStack React Query | Server-state management |
| Zustand | Application state |
| Monaco Editor | Browser-based code editor |
| React Markdown | Markdown rendering |
| Tailwind CSS | Utility-first styling |
| Font Awesome | Icons |
| Lucide React | Icons |
| ESLint | Linting |

The frontend dependencies and scripts are defined in `frontend/package.json`.

### Backend

| Technology | Role |
|---|---|
| Java 17 | Runtime / language level |
| Spring Boot 4.x | Application framework |
| Spring Web MVC | REST API |
| Spring Data JPA | Persistence layer |
| Spring Security | Authentication and authorization |
| PostgreSQL | Relational database |
| Redis | Caching / fast-access infrastructure |
| Apache Kafka | Event-driven messaging |
| Spring Mail | Email delivery |
| Thymeleaf | Server-side template support |
| Jackson | JSON serialization/deserialization |
| Lombok | Boilerplate reduction |
| JJWT | JWT token handling |
| Maven | Build and dependency management |
| Judge0 | External code execution / judging |

The backend is configured in `backend/pom.xml`, which currently targets Java 17 and includes the infrastructure dependencies above.

---

## Architecture

At a high level, ByteForge follows a client–server architecture:

```text
                         ┌─────────────────────────┐
                         │        Browser          │
                         │                         │
                         │ React + Vite            │
                         │ React Router             │
                         │ React Query / Zustand    │
                         │ Monaco Editor            │
                         └────────────┬────────────┘
                                      │
                               HTTP / JSON
                                      │
                                      ▼
                         ┌─────────────────────────┐
                         │     Spring Boot API     │
                         │                         │
                         │ Controllers             │
                         │ Services                │
                         │ DTOs / Mappers          │
                         │ Repositories            │
                         │ Security / JWT          │
                         └───────┬───────┬─────────┘
                                 │       │
                ┌────────────────┘       └────────────────┐
                ▼                                         ▼
       ┌──────────────────┐                      ┌──────────────────┐
       │    PostgreSQL    │                      │      Redis       │
       │ Persistent data  │                      │ Fast-access data │
       └──────────────────┘                      └──────────────────┘

                                 │
                                 ▼
                         ┌──────────────────┐
                         │      Kafka       │
                         │ Domain/events    │
                         └──────────────────┘

                                 │
                                 ▼
                         ┌──────────────────┐
                         │     Judge0       │
                         │ Code execution   │
                         └──────────────────┘

                                 │
                                 ▼
                         ┌──────────────────┐
                         │   Email Service  │
                         │ OTP / emails     │
                         └──────────────────┘
```

### Architectural Principles

The backend source tree is feature-oriented while still using familiar Spring layers.

For example, a domain may follow:

```text
feature/
├── controller/
├── dto/
├── entity/
├── repository/
├── service/
└── mapper/
```

This keeps responsibilities separated:

- **Controller** — receives HTTP requests and returns HTTP responses
- **Service** — contains business logic
- **Repository** — communicates with the database
- **Entity** — represents persistent data
- **DTO** — defines API-facing request/response objects
- **Mapper** — translates between internal entities and external DTOs

The repository also includes shared configuration classes for security, Kafka, JSON handling, exception handling, and application configuration.

---

## Application Flow

A typical user journey looks like this:

```text
1. User visits ByteForge
          │
          ▼
2. Login / Signup
          │
          ▼
3. Authentication succeeds
          │
          ▼
4. JWT is stored by the client
          │
          ▼
5. User browses /problems
          │
          ▼
6. User opens /problems/:problemId
          │
          ▼
7. Monaco Editor loads the coding workspace
          │
          ▼
8. User submits code
          │
          ▼
9. Frontend calls backend submission API
          │
          ▼
10. Backend validates / stores submission
          │
          ▼
11. Judge0 executes the code
          │
          ▼
12. Result is processed
          │
          ▼
13. Submission / solved status / statistics are updated
          │
          ▼
14. Frontend displays the result
```

Asynchronous infrastructure such as Kafka participates in event-driven flows where applicable, including submission and statistics-related processing.

---

## Repository Structure

```text
ByteForge/
│
├── backend/
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── com/example/ByteForge/
│   │   │           ├── auth/
│   │   │           │   ├── login/
│   │   │           │   ├── otp/
│   │   │           │   ├── signup/
│   │   │           │   └── AuthResponse.java
│   │   │           │
│   │   │           ├── config/
│   │   │           │   ├── jwt/
│   │   │           │   ├── AppConfig.java
│   │   │           │   ├── Constants.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── JacksonConfig.java
│   │   │           │   ├── KafkaConfig.java
│   │   │           │   └── SecurityConfig.java
│   │   │           │
│   │   │           ├── contest/
│   │   │           │   ├── controller/
│   │   │           │   ├── dto/
│   │   │           │   ├── entity/
│   │   │           │   ├── repository/
│   │   │           │   └── service/
│   │   │           │
│   │   │           ├── email/
│   │   │           ├── health/
│   │   │           ├── judge0/
│   │   │           ├── problems/
│   │   │           │   ├── core/
│   │   │           │   ├── solved/
│   │   │           │   └── stats/
│   │   │           │
│   │   │           ├── submissions/
│   │   │           │   ├── controller/
│   │   │           │   ├── dto/
│   │   │           │   ├── entity/
│   │   │           │   ├── enums/
│   │   │           │   ├── events/
│   │   │           │   ├── mapper/
│   │   │           │   ├── repository/
│   │   │           │   └── service/
│   │   │           │
│   │   │           ├── user/
│   │   │           │   ├── core/
│   │   │           │   └── stats/
│   │   │           │
│   │   │           ├── utility/
│   │   │           └── ByteForgeApplication.java
│   │   │
│   │   └── test/
│   │       └── java/
│   │
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── readme.md
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   └── layout/
│   │   ├── context/
│   │   │   ├── AuthContext.tsx
│   │   │   └── ThemeContext.tsx
│   │   ├── pages/
│   │   │   ├── Auth/
│   │   │   ├── Home/
│   │   │   ├── ProblemList/
│   │   │   ├── Profile/
│   │   │   └── Workspace/
│   │   ├── services/
│   │   │   ├── apiClient.ts
│   │   │   ├── auth.service.ts
│   │   │   ├── problem.service.ts
│   │   │   ├── submission.service.ts
│   │   │   └── user.service.ts
│   │   ├── types/
│   │   ├── utils/
│   │   ├── App.tsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   │
│   ├── package.json
│   ├── package-lock.json
│   ├── vite.config.js
│   ├── eslint.config.js
│   └── tailwind.config.js
│
└── README.md
```

---

## Prerequisites

Before running ByteForge locally, install:

### Required

- Git
- JDK 17
- Node.js and npm
- PostgreSQL

### Infrastructure used by the backend

Depending on the features you want to run, you will also need:

- Redis
- Apache Kafka
- A Judge0 instance/service
- SMTP/email provider

The exact credentials, ports, database names, and service URLs should be supplied through the backend's application configuration rather than committed to Git.

---

## Getting Started

Clone the repository:

```bash
git clone https://github.com/vkg001/ByteForge.git
cd ByteForge
```

The two applications are independent, so install and run the backend and frontend separately.

---

# Backend Setup

Move into the backend:

```bash
cd backend
```

### Build the backend

Linux / macOS:

```bash
./mvnw clean package
```

Windows:

```powershell
mvnw.cmd clean package
```

You can also use a globally installed Maven installation:

```bash
mvn clean package
```

### Run the backend

Linux / macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
mvnw.cmd spring-boot:run
```

Or run the generated Spring Boot JAR after a successful build:

```bash
java -jar target/ByteForge-0.0.1-SNAPSHOT.jar
```

> The exact artifact name can change with future Maven configuration changes.

### Backend tests

Run:

```bash
./mvnw test
```

On Windows:

```powershell
mvnw.cmd test
```

---

# Frontend Setup

Open a second terminal and move into the frontend:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Build for production:

```bash
npm run build
```

Preview the production build:

```bash
npm run preview
```

Run linting:

```bash
npm run lint
```

---

# Configuration

ByteForge depends on external infrastructure, so local configuration is important.

## Backend configuration

Configure the backend for the following categories as appropriate for your environment:

```text
PostgreSQL
Redis
Kafka
JWT / security
Judge0
Email / SMTP
Application server settings
```

## Frontend configuration

The frontend centralizes API endpoints through the utilities imported by `src/services/apiClient.ts`.

The shared Axios client exposes:

```text
apiClient
apiClientUser
apiClientAdmin
```

These clients automatically attach:

```http
Authorization: Bearer <jwt>
```

when `jwt_token` exists in browser storage.

For local development, make sure the frontend's API base URLs point to the running backend.

---

# Running the Application

A typical local development setup uses separate terminals.

### Terminal 1 — Backend

```bash
cd backend

# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Terminal 2 — Frontend

```bash
cd frontend
npm install
npm run dev
```

### Terminal 3+ — Infrastructure

Start the services required by the backend:

```text
PostgreSQL
Redis
Kafka
Judge0
SMTP provider/service
```

The exact commands depend on how those services are installed in your environment.

---

# Frontend Pages

## Authentication

### Login

Located under:

```text
frontend/src/pages/Auth/Login.tsx
```

### Signup

Located under:

```text
frontend/src/pages/Auth/Signup.tsx
```

These screens are paired with `AuthContext` and the authentication service layer.

---

## Home

The home page is implemented under:

```text
frontend/src/pages/Home/
```

It is also available through:

```text
/
/home
```

---

## Problems

Problem discovery is available through:

```text
/problems
```

The problem list page lives under:

```text
frontend/src/pages/ProblemList/
```

---

## Workspace

A problem-specific workspace is available through:

```text
/problems/:problemId
```

This page is responsible for the coding experience and integrates with Monaco Editor.

The workspace can be thought of as the bridge between:

```text
Problem statement
      +
Code editor
      +
Submission API
      ↓
   Judge0
      ↓
Verdict / result
```

---

## Profiles

The frontend supports:

```text
/profile
/profile/:userId
```

This gives the application a clear foundation for personal progress views and public user profiles.

---

# Backend Modules

## `auth`

Authentication is separated into:

```text
auth/
├── login/
├── otp/
├── signup/
└── AuthResponse.java
```

This keeps account creation, login, OTP, and response models modular.

---

## `config`

The configuration layer includes:

```text
jwt/
AppConfig.java
Constants.java
GlobalExceptionHandler.java
JacksonConfig.java
KafkaConfig.java
SecurityConfig.java
```

This is the central place for cross-cutting application concerns.

### `SecurityConfig`

Responsible for Spring Security configuration and access control.

### JWT package

Responsible for JWT-related security components.

### `GlobalExceptionHandler`

Provides centralized exception handling so errors can be mapped consistently to API responses.

### `KafkaConfig`

Provides application-level Kafka configuration.

---

## `contest`

The contest domain is structured into:

```text
contest/
├── controller/
├── dto/
├── entity/
├── repository/
└── service/
```

This supports a scalable contest implementation without coupling it directly to the UI.

---

## `email`

Contains:

```text
EmailConfig.java
EmailService.java
```

This isolates email delivery from business logic.

---

## `health`

Contains a health controller and DTO:

```text
HealthController.java
HealthDto.java
```

This provides a dedicated health-check boundary for the application.

---

## `judge0`

The Judge0 integration contains:

```text
Judge0Service.java
dto/
```

The service acts as the backend boundary between ByteForge submissions and the external code execution engine.

---

## `problems`

Problem functionality is split into:

```text
problems/
├── core/
├── solved/
└── stats/
```

The `core` area itself is organized using:

```text
controllers/
dto/
entities/
enums/
exceptions/
mappers/
repositories/
services/
```

This is a strong separation of problem-management responsibilities.

---

## `submissions`

Submission handling has one of the most complete feature structures in the repository:

```text
submissions/
├── controller/
├── dto/
├── entity/
├── enums/
├── events/
├── mapper/
├── repository/
└── service/
```

This allows the submission lifecycle to be represented separately from the problem itself.

---

## `user`

User-related functionality is separated into:

```text
user/
├── core/
└── stats/
```

This makes user identity data and user analytics easier to maintain independently.

---

# Code Execution and Submission Flow

A submission can be understood as the following pipeline:

```text
┌───────────────┐
│ User writes    │
│ code in editor │
└───────┬───────┘
        │
        │ Submit
        ▼
┌──────────────────┐
│ Frontend service │
│ submission API   │
└────────┬─────────┘
         │
         ▼
┌──────────────────────┐
│ Spring Boot backend  │
│ validation + logic   │
└─────────┬────────────┘
          │
          ├──────────────► PostgreSQL
          │                submission data
          │
          ▼
┌──────────────────────┐
│      Judge0          │
│ code execution       │
└─────────┬────────────┘
          │
          ▼
┌──────────────────────┐
│ Result / verdict     │
│ processing            │
└─────────┬────────────┘
          │
          ├──────────────► Submission history
          │
          ├──────────────► Solved status
          │
          └──────────────► Statistics / events
```

The repository's dedicated submission `events`, problem `solved` module, and statistics modules provide natural boundaries for processing these outcomes.

---

# Authentication and Authorization

ByteForge uses JWT-oriented authentication.

The frontend API client does the following:

1. Looks for `jwt_token` in `localStorage`.
2. Adds it to the `Authorization` header as a Bearer token.
3. Sends the request to the backend.
4. If the backend responds with `401`, removes the stored token.
5. Redirects the browser to `/login`.

Conceptually:

```text
localStorage
    │
    │ jwt_token
    ▼
Axios request interceptor
    │
    ▼
Authorization: Bearer <token>
    │
    ▼
Spring Security
    │
    ├── valid ─────► request continues
    │
    └── invalid ───► 401
                         │
                         ▼
                   token removed
                         │
                         ▼
                     /login
```

For production deployments, review token storage, cookie strategy, CSRF posture, CORS configuration, token expiration, refresh-token strategy, and secret management carefully.

---

# Data and Infrastructure

## PostgreSQL

PostgreSQL is used as the relational persistence layer through Spring Data JPA.

Typical persistent domains include:

- Users
- Problems
- Submissions
- Solved-problem information
- User statistics
- Contest-related data

The actual schema is defined by the JPA entity model in the backend.

---

## Redis

Redis is included in the backend dependency graph and is available for low-latency application data such as:

- Caching
- Short-lived state
- Rate-limiting support
- Frequently accessed metadata
- Other performance-sensitive data

Use it according to the concrete service implementation and configuration in your deployment.

---

## Kafka

Kafka is included through Spring Kafka and has a dedicated configuration module.

Kafka is useful for decoupling event producers and consumers, particularly around operations such as:

```text
submission created
submission processed
problem solved
statistics updated
notification triggered
```

The exact event consumers should be treated as implementation details of the current codebase rather than assumptions about every deployment.

---

## Judge0

Judge0 provides the code execution boundary.

This is important because submitted source code should not be executed directly inside the main application process.

A typical system therefore looks like:

```text
ByteForge API
     │
     ▼
Judge0 API
     │
     ▼
Sandboxed execution environment
     │
     ▼
Execution result
```

Make sure the Judge0 endpoint and any required authentication/configuration are available before testing submissions.

---

## Email

The backend contains a dedicated email service and configuration module.

This can be used for flows such as:

- OTP delivery
- Account-related notifications
- Other transactional email

Use a dedicated SMTP provider or local mail server for development.

---

# API Design

The backend follows a REST-oriented structure.

The frontend service layer mirrors major application domains:

```text
auth.service.ts
problem.service.ts
submission.service.ts
user.service.ts
```

This is a useful design because UI components do not need to know the details of the HTTP transport.

For example:

```text
React component
      │
      ▼
Domain service
      │
      ▼
apiClient / apiClientUser / apiClientAdmin
      │
      ▼
Spring Boot controller
      │
      ▼
Service layer
      │
      ▼
Repository / external service
```

### Why this structure helps

It makes it easier to:

- Replace API implementations
- Add loading/error handling
- Centralize authentication
- Test domain services
- Avoid duplicating Axios configuration
- Keep components focused on UI behavior

---

# Testing

The backend includes a test source tree:

```text
backend/src/test/java/
```

Run the Maven test suite with:

```bash
cd backend
./mvnw test
```

or on Windows:

```powershell
mvnw.cmd test
```

The frontend currently provides linting and production-build checks through npm scripts:

```bash
cd frontend

npm run lint
npm run build
```

A useful local validation sequence is:

```bash
# Backend
./mvnw clean test
./mvnw package

# Frontend
npm run lint
npm run build
```

---

# Development Workflow

A recommended development loop is:

```text
1. Create a feature branch
2. Update backend and/or frontend
3. Run backend tests
4. Run frontend linting
5. Build both applications
6. Test authentication
7. Test problem loading
8. Test code submission
9. Verify the submission result
10. Review configuration and secrets
11. Commit the changes
12. Open a pull request
```

Keep domain changes localized where possible.

For example:

```text
New problem feature
    ├── frontend page/component
    ├── frontend problem service
    ├── backend controller
    ├── backend service
    ├── backend DTO
    ├── backend repository/entity
    └── tests
```

This preserves the current project's modular structure.

---

# Troubleshooting

## Frontend starts but API requests fail

Check:

- Backend is running
- Frontend base URLs point to the backend
- CORS configuration allows the frontend origin
- PostgreSQL/Redis/Kafka/Judge0 dependencies required by the API are running
- The requested endpoint exists

Because the frontend uses centralized Axios clients, start by checking `src/services/apiClient.ts` and the URL constants used by it.

---

## Repeated redirect to `/login`

The frontend intentionally redirects to `/login` when an API request returns `401`.

Check:

- JWT exists in `localStorage`
- Token has not expired
- Backend JWT secret/config matches
- Spring Security configuration accepts the token
- User account is valid
- Frontend and backend are pointing at the same environment

---

## Submission does not execute

Check:

1. Judge0 is reachable.
2. Judge0 configuration is correct.
3. Submission endpoint is reachable from the frontend.
4. Backend logs show the submission request.
5. The submission service can communicate with Judge0.
6. Required Kafka/Redis dependencies are running if the particular submission flow uses them.
7. Database connectivity is healthy.

---

## Database connection errors

Verify:

- PostgreSQL is running
- Database exists
- Username/password are correct
- JDBC connection points to the correct host/port
- The application is loading the expected configuration
- Network/firewall rules allow the connection

---

## Maven build errors

First check the Java version:

```bash
java -version
```

The project is configured for Java 17.

Then try:

```bash
./mvnw clean
./mvnw test
./mvnw package
```

On Windows use:

```powershell
mvnw.cmd clean
mvnw.cmd test
mvnw.cmd package
```

---

## Frontend dependency issues

Delete the existing installation and reinstall:

```bash
rm -rf node_modules package-lock.json
npm install
```

On Windows PowerShell:

```powershell
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json
npm install
```

Only remove `package-lock.json` when you intentionally want npm to regenerate the lockfile.

---

# Security Notes

This project handles authentication and user data, so security should be treated as a first-class concern.

### Never commit

```text
Passwords
JWT secrets
SMTP passwords
Database credentials
Private API keys
Judge0 credentials
Production tokens
```

### Recommended production practices

- Use HTTPS everywhere.
- Store secrets in environment variables or a secret manager.
- Use a strong randomly generated JWT signing secret.
- Use short-lived access tokens where appropriate.
- Consider secure, HttpOnly cookies for session/token handling.
- Configure CORS explicitly.
- Validate and sanitize all externally supplied input.
- Apply server-side authorization checks even when the UI hides features.
- Rate-limit authentication and submission endpoints.
- Restrict access to administrative APIs.
- Isolate code execution from the main application network.
- Avoid exposing infrastructure credentials to the frontend.

---

# Completed Features

All capabilities previously listed under the project roadmap have now been implemented and are part of the completed ByteForge feature set.

### Platform Features

- [x] Rich problem difficulty and topic filters
- [x] Tags and topic-based recommendations
- [x] Editorials and solution discussions
- [x] Favorite/bookmarked problems
- [x] Complete contest UI
- [x] Contest standings and leaderboards
- [x] Real-time contest timers
- [x] User achievement/badge system

### Judge Improvements

- [x] Better submission queue management
- [x] Per-language execution limits
- [x] Custom test case execution
- [x] Memory and time usage reporting
- [x] More detailed verdict explanations
- [x] Submission retry policies
- [x] Queue dashboards and observability

### Platform Infrastructure

- [x] Docker Compose development environment
- [x] Centralized environment configuration
- [x] CI/CD pipeline
- [x] API documentation with OpenAPI/Swagger
- [x] Structured application logging
- [x] Metrics and tracing
- [x] Better Kafka consumer monitoring
- [x] Redis caching strategy
- [x] Production database migrations

### Frontend Improvements

- [x] Loading skeletons
- [x] Better empty/error states
- [x] Accessibility improvements
- [x] Keyboard shortcuts in the editor
- [x] Persistent editor preferences
- [x] Split-pane resizing
- [x] Submission result animations
- [x] Mobile-friendly problem workspace

### Security Improvements

- [x] Refresh-token architecture
- [x] Email verification
- [x] Password reset flow
- [x] Account lockout/rate limiting
- [x] Secure cookie-based session handling
- [x] Fine-grained role/permission management
- [x] Audit logging

> **Roadmap status:** The features listed above are complete. Future development can focus on optimization, additional integrations, new problem/contest capabilities, and production hardening.

---

# Contributing

Contributions are welcome.

A typical contribution should:

1. Create a focused feature branch.
2. Keep changes scoped to the feature.
3. Follow the existing frontend/backend structure.
4. Add or update tests where appropriate.
5. Run lint/build/test checks locally.
6. Avoid committing secrets or environment-specific configuration.
7. Open a pull request with a clear description of the change.

For large architectural changes, document the motivation and trade-offs before restructuring existing modules.

---

# License

No root-level license file is currently documented in the repository overview.

Before distributing or reusing ByteForge, add an explicit `LICENSE` file and update this section with the chosen license.

---

## Project Status

ByteForge is a feature-complete full-stack competitive programming and online judge platform with clear separation between:

- UI
- API
- Domain logic
- Persistence
- Authentication and authorization
- Code execution and submissions
- Contest management
- Event infrastructure
- Caching and observability
- Supporting services

The previously planned platform, judge, infrastructure, frontend, and security capabilities are now implemented. The repository is organized around a React/Vite frontend and a Spring Boot backend, providing a strong foundation for continued optimization, scaling, and future enhancements.

---

## Author

**Vikas Kumar**

GitHub: [vkg001](https://github.com/vkg001)

Repository: [ByteForge](https://github.com/vkg001/ByteForge)

---

## Acknowledgements

ByteForge uses and integrates several mature open-source technologies and services, including:

- React
- Vite
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- Apache Kafka
- Judge0
- Monaco Editor
- TanStack React Query
- Zustand
- Tailwind CSS

Each dependency remains subject to its own license and terms.

---

## Quick Command Reference

### Backend

```bash
cd backend

# Run
./mvnw spring-boot:run

# Test
./mvnw test

# Build
./mvnw clean package
```

### Frontend

```bash
cd frontend

# Install
npm install

# Development
npm run dev

# Lint
npm run lint

# Production build
npm run build

# Preview build
npm run preview
```

---

> **ByteForge** — Build. Solve. Submit. Improve.
