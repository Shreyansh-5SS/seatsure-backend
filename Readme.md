# SeatSure — Movie Ticket Booking Backend

A production-style backend for movie ticket booking, built to genuinely understand — not just implement — ACID properties, SOLID principles, layered architecture, and concurrency safety, rather than following a tutorial end to end.

**Status:** 🚧 In Progress

## Why this project

Most beginner backend projects stop at CRUD. SeatSure is deliberately centered on one hard problem: **preventing two users from booking the same seat at the same time** — a real race condition, not a theoretical one. Everything else (schema design, transactions, security, testing) is built in service of being able to explain, defend, and *prove* that this system handles concurrency correctly — not just claim it.

## Tech Stack

- **Language / Framework:** Java, Spring Boot, Spring Data JPA (Hibernate)
- **Database:** PostgreSQL
- **Migrations:** Flyway (versioned SQL, not Hibernate auto-DDL — schema is designed deliberately, not inferred from code)
- **Security:** Spring Security, JWT (planned), BCrypt password hashing
- **Testing:** JUnit, Mockito, Testcontainers (planned)
- **Infra (planned):** Docker, CI/CD via GitHub Actions, cloud deployment

## Architecture

Strict layered separation: **Controller → Service → Repository**, with DTOs at the API boundary so entities never leak over HTTP directly.

- `Controller` — handles HTTP requests/responses only, no business logic
- `Service` — business logic, validation, transaction boundaries
- `Repository` — Spring Data JPA interfaces, pure data access
- `DTO`s — immutable (Java records) request/response shapes, decoupled from entities

## Schema Overview

Entities: `Users`, `Theatres`, `Screens`, `Seats`, `Movies`, `Screenings`, `Bookings`, `Payments`, `Reviews`.

Key design decisions:
- **Theatre → Screen → Seat** hierarchy (not flat) — a seat number like "A-12" only makes sense relative to a specific screen.
- **Partial unique index** on `bookings(seat_id, screening_id) WHERE status != 'CANCELLED'` — the schema-level failsafe against double-booking, scoped so cancelled bookings don't permanently lock a seat.
- `NUMERIC(10,2)` for money (never `FLOAT` — avoids binary floating-point rounding errors in financial data).
- UUID primary keys over auto-increment integers — avoids sequential ID enumeration and works better in any future distributed setup.
- `CHECK` constraints on `status` and `role` fields — invalid states are rejected at the database level, not just the application level.

## What's implemented so far

- [x] Flyway migration (`V1__init_schema.sql`) with full schema, FKs, and constraints
- [x] JPA entity classes mapped to every table
- [x] `UserRepository` (Spring Data JPA) with a derived query method for email lookup
- [x] `UserService` + `UserController`, fully decoupled from entities via DTOs
- [x] Global exception handling (`@ControllerAdvice`) — starting with `EmailAlreadyExistsException`
- [x] Spring Security config with BCrypt `PasswordEncoder` bean
- [x] Secrets externalized via environment variables (no credentials committed)
- [ ] BCrypt actually wired into registration flow
- [ ] JWT authentication + role-based route protection
- [ ] Booking domain + the core concurrency test (double-booking prevention)
- [ ] Testcontainers-based integration tests against real Postgres
- [ ] Docker + docker-compose (app, Postgres, Redis)
- [ ] CI pipeline (GitHub Actions) running tests on every push
- [ ] Cloud deployment
- [ ] Search, pagination, caching

## Concurrency & ACID — the core of this project

The double-booking problem is being solved in layers, not with a single trick:
1. **Database layer:** the partial unique index above — the last line of defense regardless of what the application layer does.
2. **Application layer (in progress):** `@Transactional` boundaries with explicit locking (pessimistic/optimistic) once the Booking domain is built.
3. **Verification (planned):** a concurrent test (multiple threads racing to book the same seat) proving exactly one booking succeeds, run against a real Postgres instance via Testcontainers — not just claimed, demonstrated.

## Running locally

```bash
# 1. Set required environment variables (see .env.example — not committed)
# 2. Ensure PostgreSQL is running locally
# 3. Run Flyway migrations + start the app
./mvnw spring-boot:run
```

## Learning log

This project is being built while learning Spring Boot, JPA/Hibernate, PostgreSQL, and system design from scratch. Design decisions and trade-offs (e.g., movie-only scope vs. a hybrid movie+hotel model, CASCADE vs. RESTRICT on foreign keys) are being made deliberately and documented as they come up, not defaulted to tutorial conventions.