# Shelfwise API reference

Base URL: `http://localhost:8080`

🔓 = public, 🔑 = needs a token, 👮 = librarian, 👑 = admin

## Auth

| Method | Path                           |     | Notes |
|--------|--------------------------------|-----|-------|
| POST   | `/api/v1/auth/register`        | 🔓 | body: `username`, `password`, `name`, `email`, `phone` |
| POST   | `/api/v1/auth/login`           | 🔓 | returns `{ token, tokenType, username, role, expiresIn }` |
| GET    | `/api/v1/auth/me`              | 🔑 | current user |
| POST   | `/api/v1/auth/change-password` | 🔑 | body: `oldPassword`, `newPassword` |

## Books

| Method | Path                         |     | Notes |
|--------|------------------------------|-----|-------|
| GET    | `/api/v1/books`              | 🔓 | `?title=` or `?author=` |
| GET    | `/api/v1/books/available`    | 🔓 | |
| GET    | `/api/v1/books/{id}`         | 🔓 | |
| POST   | `/api/v1/books`              | 👮 | |
| PUT    | `/api/v1/books/{id}`         | 👮 | |
| DELETE | `/api/v1/books/{id}`         | 👮 | |
| GET    | `/api/v2/books/search`       | 🔓 | `q`, `author`, `category` (slug), `language`, `availableOnly`, `yearFrom`, `yearTo`, `page`, `size` (max 100), `sort` (e.g. `title,desc`) |
| GET    | `/api/v2/books/{id}`         | 🔓 | includes categories and average rating |

## Authors & categories

| Method | Path                                        |     |
|--------|---------------------------------------------|-----|
| GET    | `/api/v1/authors`                           | 🔓 |
| GET    | `/api/v1/authors/{id}`                      | 🔓 |
| GET    | `/api/v1/authors/{id}/books`                | 🔓 |
| POST   | `/api/v1/authors`                           | 👮 |
| PUT    | `/api/v1/authors/{id}`                      | 👮 |
| DELETE | `/api/v1/authors/{id}`                      | 👑 |
| GET    | `/api/v1/categories/tree`                   | 🔓 |
| GET    | `/api/v1/categories/{id}/path`              | 🔓 |
| GET    | `/api/v1/categories/{id}/books`             | 🔓 |
| POST   | `/api/v1/categories?name=&parentId=`        | 👮 |
| PUT    | `/api/v1/categories/{id}/parent?parentId=`  | 👮 |
| POST   | `/api/v1/categories/{id}/books/{bookId}`    | 👮 |

## Loans

| Method | Path                               |     | Notes |
|--------|------------------------------------|-----|-------|
| POST   | `/api/v1/loans/checkout`           | 👮 | body: `memberId`, `bookId`, optional `copyBarcode` |
| POST   | `/api/v1/loans/{id}/return`        | 👮 | |
| POST   | `/api/v1/loans/{id}/renew`         | 🔑 | not implemented yet (returns 500) |
| GET    | `/api/v1/loans/me`                 | 🔑 | |
| GET    | `/api/v1/loans/member/{memberId}`  | 👮 | |
| GET    | `/api/v1/loans/overdue`            | 👮 | |
| GET    | `/api/v1/loans/{id}/fine`          | 🔑 | current fine for a loan |

## Reservations

| Method | Path                                   |     | Notes |
|--------|----------------------------------------|-----|-------|
| POST   | `/api/v1/reservations`                 | 🔑 | body: `{ "bookId": 1, "memberId": 2 }` |
| GET    | `/api/v1/reservations/book/{bookId}`   | 🔑 | queue for a book |
| GET    | `/api/v1/reservations/member/{id}`     | 🔑 | |
| DELETE | `/api/v1/reservations/{id}`            | 🔑 | |

## Fines

| Method | Path                                   |     |
|--------|----------------------------------------|-----|
| GET    | `/api/v1/fines/member/{memberId}`      | 🔑 |
| GET    | `/api/v1/fines/member/{memberId}/balance` | 🔑 |
| POST   | `/api/v1/fines/{id}/pay`               | 🔑 |
| GET    | `/api/v1/fines/{id}/payments`          | 🔑 |
| POST   | `/api/v1/fines/{id}/waive?reason=`     | 👑 |

## Branches, copies & transfers

| Method | Path                                     |     |
|--------|------------------------------------------|-----|
| GET    | `/api/v1/branches`                       | 🔓 |
| POST   | `/api/v1/branches`                       | 👑 |
| POST   | `/api/v1/copies`                         | 👮 |
| GET    | `/api/v1/copies/book/{bookId}`           | 🔑 |
| GET    | `/api/v1/copies/barcode/{barcode}`       | 🔑 |
| POST   | `/api/v1/copies/{id}/withdraw`           | 👮 |
| POST   | `/api/v1/transfers`                      | 👮 |
| POST   | `/api/v1/transfers/{id}/ship`            | 👮 |
| POST   | `/api/v1/transfers/{id}/receive`         | 👮 |

## Reviews & recommendations

| Method | Path                                          |     |
|--------|-----------------------------------------------|-----|
| GET    | `/api/v1/books/{bookId}/reviews`              | 🔓 |
| POST   | `/api/v1/books/{bookId}/reviews`              | 🔑 |
| GET    | `/api/v1/books/{bookId}/rating`               | 🔓 |
| DELETE | `/api/v1/reviews/{id}`                        | 🔑 (own reviews only) |
| GET    | `/api/v1/recommendations/me`                  | 🔑 |
| GET    | `/api/v1/recommendations/book/{id}/also-borrowed` | 🔑 |

## Events

| Method | Path                                       |     |
|--------|--------------------------------------------|-----|
| GET    | `/api/v1/events`                           | 🔓 |
| POST   | `/api/v1/events`                           | 👮 |
| POST   | `/api/v1/events/{id}/register?memberId=`   | 🔑 |
| POST   | `/api/v1/events/{id}/cancel`               | 👮 |

## Reports & data

| Method | Path                                     |     |
|--------|------------------------------------------|-----|
| GET    | `/api/v1/reports/dashboard`              | 👮 |
| GET    | `/api/v1/reports/popular-books?limit=`   | 👮 |
| GET    | `/api/v1/reports/monthly?year=`          | 👮 |
| POST   | `/api/v1/data/import/books` (multipart `file`) | 👮 |
| GET    | `/api/v1/data/export/books.csv`          | 👮 |
| GET    | `/api/v1/data/export/overdue-loans.csv`  | 👮 |

## Admin

All 👑

- `GET /api/v1/admin/users`
- `PUT /api/v1/admin/users/{id}/role?role=`
- `PUT /api/v1/admin/users/{id}/enabled?enabled=`
- `GET /api/v1/admin/audit`
- `POST /api/v1/admin/cache/clear`

## Errors

Errors come back as:

```json
{ "status": 404, "message": "Book not found with id 99", "timestamp": "..." }
```
