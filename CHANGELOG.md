# Changelog

## [Unreleased] - 0.2.0

### Added
- JWT authentication with ADMIN / LIBRARIAN / MEMBER roles
- Authors and nested categories
- Library branches, physical book copies and transfers between branches
- Fines and payments (`fines` module)
- Library events with registration (`events` module)
- Reviews, ratings and recommendations
- Purchase requests
- In-app notifications + email templates
- v2 book search with filters, paging and sorting
- Reports dashboard, CSV import/export
- Audit log, rate limiting, caching
- Swagger UI, actuator
- Loan renewals
- Dockerfile + docker-compose with Postgres

### Changed
- Reservations are now moved to READY when a book comes back
- Members get a membership expiry date

### Fixed
- Cancelling a reservation no longer throws

## [0.1.0] - 2026-10-19

### Added
- Books, members, loans
- Basic reservations
- Overdue loan job
