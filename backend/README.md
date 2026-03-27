# Backend — SmartTrafficFlow API

REST API for registering and analyzing urban traffic data.

## Tech Stack

| Layer         | Technology                           |
|---------------|--------------------------------------|
| Language      | Java 21                              |
| Framework     | Spring Boot 3.4.3                    |
| Persistence   | Spring Data JPA + Hibernate          |
| Database      | PostgreSQL 15                        |
| Migrations    | Flyway                               |
| API Docs      | SpringDoc OpenAPI 3.0 (Swagger UI)   |
| Build         | Maven                                |
| Testing       | JUnit 5 + Testcontainers             |

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 15 running on port `5432` (or use the Docker Compose setup in `infra/`)

### Configuration

Copy and edit the environment file:

```bash
cp .env.example .env
```

Key variables (see `.env` for the full list):

```env
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smarttrafficflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174
```

### Run

```bash
mvn spring-boot:run
```

API base URL: `http://localhost:8080/api`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Build

```bash
mvn clean package
java -jar target/backend-*.jar
```

## Project Structure

```
src/main/java/com/smarttrafficflow/backend/
├── api/
│   ├── controller/     # REST endpoints
│   ├── dto/            # Request / Response objects
│   └── exception/      # Global error handling
├── config/             # Spring beans (CORS, OpenAPI)
└── domain/
    ├── trafficrecord/
    │   ├── entity/     # TrafficRecord JPA entity
    │   ├── repository/ # Spring Data repository
    │   └── service/    # TrafficRecordService
    ├── simulation/     # SimulationService
    ├── analytics/      # AnalyticsService
    ├── insight/        # InsightService
    └── export/         # ExportService
```

## API Reference

All endpoints are prefixed with `/api`.

### Traffic Records

| Method | Path                  | Description                   |
|--------|-----------------------|-------------------------------|
| POST   | `/traffic-records`    | Create a traffic record       |
| GET    | `/traffic-records`    | List all traffic records      |

**Create request body:**

```json
{
  "timestamp": "2026-03-26T14:00:00Z",
  "roadType": "ARTERIAL",
  "vehicleVolume": 120,
  "eventType": "show",
  "weather": "SUNNY",
  "region": "centro"
}
```

Road types: `LOCAL`, `ARTERIAL`, `HIGHWAY`
Weather values: `SUNNY`, `RAIN`, `CLOUDY`

### Simulations

| Method | Path                    | Description                          |
|--------|-------------------------|--------------------------------------|
| POST   | `/simulations/generate` | Generate random traffic records      |

**Request body:**

```json
{
  "scenarioName": "rush-hour-test",
  "recordsToGenerate": 100
}
```

`recordsToGenerate` must be between 1 and 250.

### Analytics

| Method | Path             | Description                              |
|--------|------------------|------------------------------------------|
| GET    | `/traffic-stats` | Aggregated vehicle volume statistics     |

Query parameter `groupBy`: `hour` | `weekday` | `roadType`

**Response:**

```json
{
  "labels": ["00", "01", "02", ...],
  "values": [340, 120, 80, ...]
}
```

### Insights

| Method | Path                | Description                     |
|--------|---------------------|---------------------------------|
| GET    | `/traffic-insights` | Auto-generated traffic insights |

**Response:**

```json
{
  "insights": [
    "Pico de tráfego às 08h com 1240 veículos.",
    "Via com maior volume: HIGHWAY com 4500 veículos."
  ]
}
```

### Map Data

| Method | Path           | Description                   |
|--------|----------------|-------------------------------|
| GET    | `/traffic-map` | Geographic points for map view |

### Exports

| Method | Path       | Description                     |
|--------|------------|---------------------------------|
| GET    | `/exports` | Export all records as CSV/JSON  |

Query parameter `format`: `csv` | `json`

## Database

### Schema

Managed by Flyway. Migrations live in `src/main/resources/db/migration/`.

```sql
CREATE TABLE traffic_records (
    id             UUID PRIMARY KEY,
    timestamp      TIMESTAMPTZ  NOT NULL,
    road_type      VARCHAR(100) NOT NULL,
    vehicle_volume INTEGER      NOT NULL,
    event_type     VARCHAR(150),
    weather        VARCHAR(100),
    region         VARCHAR(150)
);
```

### Profiles

| Profile | Flyway  | DDL auto       | SQL logging |
|---------|---------|----------------|-------------|
| default | enabled | validate       | off         |
| dev     | disabled| create-drop    | on          |

## Testing

```bash
mvn test
```

Integration tests use Testcontainers to spin up a real PostgreSQL instance — no manual setup required.
