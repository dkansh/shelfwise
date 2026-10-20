# Shelfwise 📚

Shelfwise is a REST API for running a community library. It keeps track of
books, physical copies across branches, library members, loans, reservations,
fines, events and more.

Built with Spring Boot + H2 (Postgres in prod).

## Getting started

### Prerequisites

- Java 21 or newer
- That's it, the Gradle wrapper is included

### Run it

```bash
./gradlew bootRun
```

The app runs on http://localhost:8080. Sample books, members, loans etc. are loaded on
startup (see `DataSeeder` and `UserSeeder`).

- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shelfwise`, user `sa`, no password)
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### Run with Docker

```bash
./gradlew bootJar
docker compose up --build
```

This starts the app with the `prod` profile and a Postgres database.

### Tests

```bash
./gradlew test
```

## Authentication

Most endpoints need a JWT. Log in to get one:

```bash
curl -X POST localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"librarian","password":"librarian123"}'
```

Then send it as `Authorization: Bearer <token>`.

Default users (created on startup):

| Username    | Password       | Role      |
|-------------|----------------|-----------|
| `admin`     | `admin123`     | ADMIN     |
| `librarian` | `librarian123` | LIBRARIAN |
| `alice`     | `alice123`     | MEMBER    |

New members can sign up with `POST /api/v1/auth/register`.

## API

See [docs/API.md](docs/API.md) for the full list (or Swagger UI). The main ones:

### Books

| Method | Path                       | Description                                   |
|--------|----------------------------|-----------------------------------------------|
| GET    | `/api/v1/books`            | List books. Supports `?title=` and `?author=` |
| GET    | `/api/v1/books/{id}`       | Get one book                                  |
| POST   | `/api/v1/books`            | Add a book (librarian)                        |
| PUT    | `/api/v1/books/{id}`       | Update a book (librarian)                     |
| DELETE | `/api/v1/books/{id}`       | Remove a book (librarian)                     |
| GET    | `/api/v2/books/search`     | Search with filters, paging and sorting       |

Example body for adding a book:

```json
{
  "isbn": "978-0-13-235088-4",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "publishedYear": 2008,
  "genre": "Programming",
  "totalCopies": 3
}
```

### Members

| Method | Path                    | Description       |
|--------|-------------------------|-------------------|
| GET    | `/api/v1/members`       | List members      |
| GET    | `/api/v1/members/{id}`  | Get one member    |
| POST   | `/api/v1/members`       | Register a member |

### Loans

TODO: write this section. Look at `LoanController` for now.

### Other modules

Authors, categories, branches, copies, transfers, reservations, fines, reviews,
recommendations, events, purchase requests, notifications, reports, CSV import/export
and admin. See [docs/API.md](docs/API.md).

## Business rules

- A member can have at most **5** books checked out at once (premium members get 10).
- The loan period is **14 days**. Loans can be renewed twice.
- Late returns cost **$0.25 per day**.
- Members who owe $10 or more in fines can't borrow.
- Every night at 2am a job marks late loans as `OVERDUE` and emails the member.
- Reserved books are held at the desk for 3 days.
- The library is closed on weekends and public holidays, due dates never fall on a closed day.
- Inactive members can't borrow books.

## Configuration

Settings are in `src/main/resources/application.yml` under `shelfwise`:

| Property                          | Default |
|-----------------------------------|---------|
| `shelfwise.max-loans-per-member`  | 5       |
| `shelfwise.loan-period-days`      | 14      |
| `shelfwise.fine-per-day`          | 0.25    |
| `shelfwise.max-outstanding-fine`  | 10.00   |
| `shelfwise.jwt.secret`            | (set this in prod!) |
| `shelfwise.notifications.email.enabled` | false |

## Project layout

```
src/main/java/com/shelfwise
├── config        app config, seeders, caching, scheduling
├── controller    REST controllers
├── dto           request/response objects
├── event         spring application events + listeners
├── events        library events (book clubs etc.)
├── exception     exceptions + global handler
├── fines         fines + payments
├── mapper
├── model         JPA entities
├── repository
├── scheduler     nightly jobs
├── security      JWT + spring security
├── service
├── util
└── web           interceptors, CORS
```

More background in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## TODO

- [x] Reservations
- [x] Authentication
- [x] Loan renewals
- [ ] Real email notifications (SMTP sender is there but not tested)
- [ ] Waiting lists for events
- [ ] Move fully to PostgreSQL
- [ ] Frontend

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
