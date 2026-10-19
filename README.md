# Shelfwise 📚

Shelfwise is a small REST API for running a community library. It keeps track of
books, library members and loans (checking books out and bringing them back).

Built with Spring Boot + H2.

## Getting started

### Prerequisites

- Java 21 or newer
- That's it, the Gradle wrapper is included

### Run it

```bash
./gradlew bootRun
```

The app runs on http://localhost:8080. Some sample books and members are loaded on
startup (see `DataSeeder`).

H2 console: http://localhost:8080/h2-console
JDBC URL is `jdbc:h2:mem:shelfwise`, user `sa`, no password.

### Tests

```bash
./gradlew test
```

## API

### Books

| Method | Path                  | Description                                  |
|--------|-----------------------|----------------------------------------------|
| GET    | `/api/v1/books`       | List books. Supports `?title=` and `?author=` |
| GET    | `/api/v1/books/{id}`  | Get one book                                 |
| POST   | `/api/v1/books`       | Add a book                                   |
| PUT    | `/api/v1/books/{id}`  | Update a book                                |
| DELETE | `/api/v1/books/{id}`  | Remove a book                                |

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

## Business rules

- A member can have at most **5** books checked out at once (premium members get 10).
- The loan period is **14 days**.
- Late returns cost **$0.25 per day**.
- Every night at 2am a job marks late loans as `OVERDUE` and emails the member.
- Inactive members can't borrow books.

## Configuration

Settings are in `src/main/resources/application.yml` under `shelfwise`:

| Property                         | Default |
|----------------------------------|---------|
| `shelfwise.max-loans-per-member` | 5       |
| `shelfwise.loan-period-days`     | 14      |
| `shelfwise.fine-per-day`         | 0.25    |

## TODO

- [ ] Reservations (started, see `ReservationService`)
- [ ] Real email notifications
- [ ] Loan renewals
- [ ] Authentication - **right now every endpoint is public**
- [ ] Move to PostgreSQL
