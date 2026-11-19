# GitHub SDLC Automation - Ticket Triage Demo

This repository contains automated SDLC workflows and the **Customer Ticket Portal (`ticket-triage-demo`)**, a Spring Boot 3 web application with Spring Data JPA, H2 in-memory database, Thymeleaf, and Bootstrap 5.

---

## Repository Structure

```
├── .github/
│   └── workflows/
│       └── ci.yml               # Automated CI pipeline for Maven build & tests
├── ticket-triage-demo/          # Spring Boot application module
│   ├── pom.xml
│   ├── README.md
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/tickettriagedemo/
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/com/example/tickettriagedemo/
├── .gitignore                   # Ignored files (build artifacts, IDE settings)
└── README.md                    # Root project documentation
```

---

## Ticket Triage Demo Overview

The **Customer Ticket Portal** provides a single-page interface for submitting and triaging customer support tickets:

- **Single-Page Application (`index.html`)**: Interactive ticket submission form with inline validation feedback and live table of submitted tickets.
- **Robust Validation**: Server-side Bean Validation (`@NotBlank`, `@Size`, strict regex `@Email`, priority and status patterns) with client feedback.
- **Persistence Layer**: Spring Data JPA with H2 in-memory database (`jdbc:h2:mem:ticketdb`).
- **Comprehensive Test Suite**: 28 automated tests covering model validation, service business logic, Spring context loading, and MockMvc UI rendering.

---

## Quick Start

### Prerequisites
- JDK 21+
- Apache Maven 3.9+

### Build & Run Tests
```powershell
cd ticket-triage-demo
mvn clean test
```

### Start Application Locally
```powershell
cd ticket-triage-demo
mvn spring-boot:run
```

- **Web Portal**: [http://127.0.0.1:8080/](http://127.0.0.1:8080/)
- **H2 Console**: [http://127.0.0.1:8080/h2-console](http://127.0.0.1:8080/h2-console) (`JDBC URL: jdbc:h2:mem:ticketdb`, `User: sa`, `Password: <blank>`)

---

## Continuous Integration (CI)

A GitHub Actions workflow is configured in [`.github/workflows/ci.yml`](.github/workflows/ci.yml) to automatically validate all pull requests and pushes to `main` by setting up Java 21, caching Maven dependencies, and running the complete test suite.
