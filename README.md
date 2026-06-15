# Event Management System

A Spring Boot 4 / Java 21 REST API for organising events, selling tickets, registering attendees, and gathering feedback.

## Contents

1. [Run locally](#run-locally)
2. [Authentication](#authentication)
3. [API Endpoints](#api-endpoints)
4. [Database Schema](#database-schema)

---
## Run locally

The app uses **MySQL** in dev and **H2** in tests.

### Required env vars

Copy `.env.example` to `.env` and fill in real values, or export them in your shell:

| Variable | Default | Purpose |
|----------|---------|---------|
| `DB_URL` | `jdbc:mysql://localhost:3306/eventplatform?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | MySQL JDBC URL |
| `DB_USER` | `root` | MySQL user |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | `change-me-...` | At least 32 bytes, signs JWT tokens |
| `JWT_EXPIRATION_MS` | `86400000` | Token lifetime in ms (24h) |
| `CLOUDINARY_CLOUD_NAME` | _empty_ | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | _empty_ | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | _empty_ | Cloudinary API secret |

If the Cloudinary vars are blank the app still boots, but `POST /speakers/{id}/materials` will fail until they are set.

### Start MySQL via Docker

```bash
docker run --name eventplatform-mysql -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=eventplatform -p 3306:3306 -d mysql:8.0
```

### Run the app

```bash
./mvnw spring-boot:run
```

Swagger UI is exposed at `http://localhost:8080/swagger-ui.html`. Click *Authorize* and paste the token returned from `POST /auth/login` (without the `Bearer ` prefix).

### Run the tests

```bash
./mvnw test
```

Tests run against an in-memory H2 database with the `test` profile and do not require MySQL.


### Frontend
https://github.com/v4ni0/Event-Management-System-Ui

---
## Authentication

All endpoints except those under `/auth/**`, the GET endpoints on events/speakers/agenda/tickets/feedback summary, and `/swagger-ui/**` require a valid JWT in the `Authorization: Bearer <token>` header.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register new user, returns JWT |
| `POST` | `/auth/login` | Login, returns JWT |
| `POST` | `/auth/logout` | Invalidate token (process-local blacklist) |

---
## API endpoints

### Users `Authenticated`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/users/me` | Get current user profile |
| `PUT` | `/users/me` | Update current user profile |
| `GET` | `/users/{id}` | Get user by ID (admin) |
| `GET` | `/users` | List users (admin, paginated) |

---

### Events `Core Resource`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/events` | Create event (organizer) |
| `GET` | `/events` | List events (filterable, paginated) |
| `GET` | `/events/{id}` | Get event details |
| `PUT` | `/events/{id}` | Update event (organizer) |
| `PATCH` | `/events/{id}/status` | Change event status |
| `DELETE` | `/events/{id}` | Delete event (organizer) |
| `GET` | `/events/{id}/summary` | Event summary with stats |

#### Query Parameters for `GET /events`

| Parameter | Type | Description |
|-----------|------|-------------|
| `status` | string | Filter by status: `DRAFT`, `PUBLISHED`, `CANCELLED`, `COMPLETED` |
| `category` | string | Filter by event category |
| `city` | string | Filter by city |
| `date_from` | ISO 8601 | Events starting after this date |
| `date_to` | ISO 8601 | Events starting before this date |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 20) |
| `sort` | string | Sort field and direction, e.g. `start_date,asc` |

---

### Tickets

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/events/{eventId}/tickets` | Create ticket type |
| `GET` | `/events/{eventId}/tickets` | List ticket types for event |
| `GET` | `/events/{eventId}/tickets/{id}` | Get ticket details |
| `PUT` | `/events/{eventId}/tickets/{id}` | Update ticket type |
| `DELETE` | `/events/{eventId}/tickets/{id}` | Remove ticket type |

---

### Registrations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/events/{eventId}/registrations` | Register / purchase ticket |
| `GET` | `/events/{eventId}/registrations` | List registrations (organizer) |
| `GET` | `/registrations/{id}` | Get registration details |
| `PATCH` | `/registrations/{id}/cancel` | Cancel registration |
| `PATCH` | `/registrations/{id}/check-in` | Check in attendee |
| `GET` | `/users/me/registrations` | My registrations |

---

### Agenda & Speakers

#### Agenda Items

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/events/{eventId}/agenda` | Add agenda item |
| `GET` | `/events/{eventId}/agenda` | Get full agenda |
| `PUT` | `/events/{eventId}/agenda/{id}` | Update agenda item |
| `DELETE` | `/events/{eventId}/agenda/{id}` | Remove agenda item |
| `PATCH` | `/events/{eventId}/agenda/reorder` | Reorder agenda items |

#### Speakers

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/speakers` | Create speaker profile |
| `GET` | `/speakers` | List speakers |
| `GET` | `/speakers/{id}` | Get speaker details |
| `PUT` | `/speakers/{id}` | Update speaker |

#### Presentation Materials

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/speakers/{id}/materials` | Upload presentation file |
| `GET` | `/speakers/{id}/materials` | List materials |

---

### Feedback & Analytics

#### Feedback

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/events/{eventId}/feedback` | Submit feedback |
| `GET` | `/events/{eventId}/feedback` | List feedback (organizer) |
| `GET` | `/events/{eventId}/feedback/summary` | Aggregated ratings |

#### Analytics

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/events/{eventId}/analytics` | Event analytics dashboard |
| `GET` | `/events/{eventId}/analytics/attendance` | Attendance over time |

---

## Database Schema

![Database](drawSQL-image-export-2026-04-19.jpg)
