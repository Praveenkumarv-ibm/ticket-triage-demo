# Ticket Triage Demo

A Spring Boot 3 web application providing a Customer Ticket Portal for creating and viewing customer support tickets with input validation, in-memory H2 database persistence, and Thymeleaf + Bootstrap 5 UI.

---

## Features

- **Single-Page Customer Portal (`index.html`)**: Interactive ticket submission form with validation feedback and real-time list of all submitted tickets below.
- **Data Validation**: Server-side Bean Validation (`@NotBlank`, `@Size`, `@Email` regex pattern, `@Pattern`) ensuring valid titles, descriptions, email addresses, priorities, and statuses.
- **Persistence Layer**: Spring Data JPA with H2 in-memory database (`jdbc:h2:mem:ticketdb`).
- **H2 Console**: Local developer console available at `/h2-console` with secure localhost-only binding.
- **Comprehensive Testing**: 28 automated tests covering Bean Validation rules, service business logic, Spring context bootstrapping, and MockMvc UI rendering paths.

---

## Tech Stack

- **Java**: 21
- **Framework**: Spring Boot 3.3.5
- **Web**: Spring MVC & Thymeleaf
- **Styling**: Bootstrap 5.3.3
- **ORM / Persistence**: Spring Data JPA & Hibernate
- **Database**: H2 Database Engine (in-memory)
- **Validation**: Hibernate Validator (Jakarta Bean Validation)
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test (MockMvc)
- **Build Tool**: Apache Maven 3.10+

---

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.9+ (or higher)

---

## Building and Running

### Run Tests
```powershell
mvn test
```

### Run Application
```powershell
mvn spring-boot:run
```

The application will start on `http://127.0.0.1:8080`.

---

## Key Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Renders the Customer Ticket Portal with submission form and list of all tickets |
| `POST` | `/tickets` | Submits a new ticket with server-side validation |
| `GET` | `/h2-console` | H2 in-memory database management console (`User: sa`, `Password: <blank>`) |

---

## Project Structure

```
ticket-triage-demo/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/tickettriagedemo/
    │   │   ├── TicketTriageDemoApplication.java
    │   │   ├── controller/
    │   │   │   └── TicketController.java
    │   │   ├── model/
    │   │   │   └── Ticket.java
    │   │   ├── repository/
    │   │   │   └── TicketRepository.java
    │   │   └── service/
    │   │       └── TicketService.java
    │   └── resources/
    │       ├── application.properties
    │       └── templates/
    │           └── index.html
    └── test/
        └── java/com/example/tickettriagedemo/
            ├── TicketControllerTest.java
            ├── TicketServiceTest.java
            ├── TicketTriageDemoApplicationTests.java
            └── TicketValidationTest.java
```
