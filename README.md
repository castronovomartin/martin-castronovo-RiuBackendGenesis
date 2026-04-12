# Hotel Availability Search Service

Backend service for hotel availability search built with Java 21, Spring Boot 4 and hexagonal architecture.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 (Eclipse Temurin) |
| Spring Boot | 4.0.5 |
| Apache Kafka | 7.6.0 (Confluent) |
| Oracle XE | 21c Slim |
| Maven | 3.9.6 |
| JUnit | 6 |
| Jacoco | 0.8.12 |

---

## Prerequisites

**Docker Desktop** must be installed and running.

---

## Running the application

### 1. Extract the project

Extract the received `.zip` file and open a terminal inside the project folder:
```bash
cd martin-castronovo-RiuBackendGenesis
```

### 2. Start the application
```bash
docker compose up --build
```

This command compiles the application inside Docker and starts the full ecosystem automatically — no local Java or Maven required.

> **macOS users:** Docker Desktop may request permission to access data from other apps. Click **Allow** to proceed — this is required for Docker to function correctly.

**First time:** may take 5 to 10 minutes — Oracle needs to initialize its database.

### 3. Verify all services are running
```bash
docker ps
```

All 6 containers should appear with status `Up`:

| Container | Status |
|---|---|
| `hotel-availability` | `Up` |
| `kafdrop` | `Up` |
| `kafka` | `Up (healthy)` |
| `cloudbeaver` | `Up` |
| `zookeeper` | `Up (healthy)` |
| `oracle` | `Up (healthy)` |

The application is ready when http://localhost:8080/swagger-ui.html loads successfully.

### 4. Stop the application

Stop keeping data:
```bash
docker compose down
```

Stop and remove all data:
```bash
docker compose down -v
```

---

## Available services

| Service | URL | Description |
|---|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html | Test the endpoints |
| Kafdrop | http://localhost:9000 | View Kafka topics and messages |
| CloudBeaver | http://localhost:8978 | View Oracle database |

---

## API Endpoints

### POST /search

Creates a new hotel availability search, publishes it to Kafka and returns a unique identifier.

**Request body:**
```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}
```

**Response — HTTP 201:**
```json
{
  "searchId": "cb8c8aba-1e99-4258-95c1-37c45ba9da39"
}
```

**Validations:**
- `hotelId` must not be blank
- `checkIn` and `checkOut` must not be null
- `checkIn` must be strictly before `checkOut`
- `ages` must not be empty

**Error response — HTTP 400:**
```json
{
  "error": "VALIDATION_ERROR",
  "message": "{hotelId=hotelId must not be blank}",
  "timestamp": "12/04/2026 20:30:00"
}
```

---

### GET /count

Returns the number of searches equal to the one identified by the given `searchId`.
Age order is significant — `[30, 29, 1, 3]` and `[3, 1, 29, 30]` are considered different searches.

**Query parameter:** `searchId` (required)

**Response — HTTP 200:**
```json
{
  "searchId": "cb8c8aba-1e99-4258-95c1-37c45ba9da39",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2023",
    "checkOut": "31/12/2023",
    "ages": [30, 29, 1, 3]
  },
  "count": 5
}
```

**Error response — HTTP 404:**
```json
{
  "error": "SEARCH_NOT_FOUND",
  "message": "No search found for searchId: cb8c8aba-1e99-4258-95c1-37c45ba9da39",
  "timestamp": "12/04/2026 20:30:00"
}
```

> **Tip:** To see the count increase, execute POST /search 3 times with the same body.
> Then execute GET /count with any of the returned searchIds — the count will reflect
> all searches with the same hotelId, checkIn, checkOut and ages in the same order.

---

## Verifying the ecosystem

### Kafka messages

1. Open http://localhost:9000
2. Click on **hotel_availability_searches**
3. Click **View Messages → All Messages**
4. Each POST /search publishes a JSON message to this topic

### Oracle data

1. Open http://localhost:8978
2. First time — complete the setup wizard:
   - Click **Next**
   - Set credentials: Username `cbadmin` / Password `Admin123` / Repeat Password `Admin123`
   - Click **Next → Finish**
3. Login with `cbadmin` / `Admin123`
4. Click the CloudBeaver logo (top left) → **New Connection → Oracle**
5. Fill in:

| Field | Value |
|---|---|
| Host | `oracle` |
| Port | `1521` |
| Database | `XEPDB1` |
| Service type | `SERVICE` |
| User name | `system` |
| Password | `oracle` |

6. Click **Test → Create**
7. Open the SQL editor and run:
```sql
SELECT * FROM SYSTEM.SEARCHES;
```

---

## Architecture

The application follows strict hexagonal architecture organized in three layers:
```
com.challenge.hotel
├── domain                  ← Pure Java — no framework dependencies
│   ├── model               ← Entities and value objects
│   └── port
│       ├── input           ← Use case interfaces (implemented by application layer)
│       └── output          ← Repository and publisher interfaces (implemented by infrastructure)
├── application             ← Use cases — orchestrates domain, no infrastructure knowledge
│   └── usecase
└── infrastructure          ← Spring, Kafka, JPA, Oracle
    ├── input
    │   ├── rest            ← REST controllers and DTOs
    │   └── kafka           ← Kafka consumer
    └── output
        ├── kafka           ← Kafka producer
        └── persistence     ← JPA entities, repositories and adapters
```

The domain layer has zero dependencies on Spring, Kafka or JPA — it is pure Java.
Dependencies always point inward: `infrastructure → application → domain`.

---

## Key technical decisions

**Java 21 + Spring Boot 4**
Latest stable versions as required. Virtual threads enabled globally via `spring.threads.virtual.enabled: true` — used automatically by Kafka listeners for non-blocking Oracle persistence.

**Oracle XE over PostgreSQL**
Oracle was explicitly valued in the challenge requirements and is coherent with the target production environment (Oracle Cloud). Dockerized using `gvenzl/oracle-xe:21-slim`.

**Hexagonal architecture with packages (no Maven modules)**
The challenge explicitly prohibits separate Maven modules. Layer separation is enforced by strict package conventions — the domain layer imports nothing from Spring, Kafka or JPA.

**SearchId generated without database access**
UUID v4 generated algorithmically in memory via `SearchId.generate()` — no database round-trip required.

**Age order preserved for count matching**
Ages are stored as a serialized ordered String (`"30,29,1,3"`) in Oracle. `[30, 29, 1, 3]` and `[3, 1, 29, 30]` produce different strings and are counted as different searches.

**Immutability enforced throughout**
All domain objects and DTOs are Java records. Mutable collections use `List.copyOf()` for defensive copying. No setters anywhere in the domain or application layers.

**SQL injection prevention**
Spring Data JPA derived query methods use prepared statements automatically — no manual SQL anywhere in the codebase.

**Standardized error responses**
All API errors return a consistent `ErrorResponse` structure with `error` code, `message` and `timestamp`. Business rule violations return HTTP 404, validation errors return HTTP 400.

**Test coverage**
Minimum 80% enforced by Jacoco on all four metrics (lines, branches, methods, instructions). Current coverage: 94% instructions, 86% branches.