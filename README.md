# SmartTrafficFlow

A full-stack web application for monitoring, recording, and analyzing urban traffic data. It enables manual data entry, simulation of traffic scenarios, aggregated analytics, and map-based visualization.

## Architecture Overview

```
repository/
├── backend/          # Spring Boot REST API (Java 21)
├── frontend/
│   └── latest-app/   # React + TypeScript dashboard (Vite)
└── infra/            # Docker Compose orchestration
```

### Service Communication

```
Frontend  (http://localhost:5174)
    │  HTTP REST (CORS-enabled)
    ▼
Backend   (http://localhost:8080/api)
    │  JDBC
    ▼
PostgreSQL (localhost:5432)
```

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node 20+
- Docker & Docker Compose

### 1. Start the database and frontend via Docker

```bash
cd infra
docker compose up -d
```

This starts:
- **PostgreSQL 15** on port `5432`
- **React frontend** on port `5174`
- **pgAdmin** on port `8100`

### 2. Start the backend locally

```bash
cd backend
cp .env.example .env   # adjust if needed
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Services

| Service    | Technology              | Port  | README                              |
|------------|-------------------------|-------|-------------------------------------|
| Backend    | Java 21 / Spring Boot   | 8080  | [backend/README.md](backend/README.md) |
| Frontend   | React 18 / TypeScript   | 5174  | [frontend/latest-app/README.md](frontend/latest-app/README.md) |
| Infra      | Docker Compose          | —     | [infra/README.md](infra/README.md)  |

## Key Features

- **Traffic Records** — Create and browse traffic entries (road type, volume, weather, region, event)
- **Simulation** — Generate up to 250 random traffic records for a given scenario
- **Analytics** — Aggregate vehicle volume by hour, weekday, or road type
- **Insights** — Auto-generated peak-hour and peak-road-type summaries
- **Map View** — Region-based traffic point visualization (Leaflet)
- **Exports** — Download all records as CSV or JSON
- **API Docs** — OpenAPI 3.0 / Swagger UI included

## Environment Variables

See `.env` in the `backend/` directory for the full list. Key variables:

| Variable                    | Default                                              | Description                   |
|-----------------------------|------------------------------------------------------|-------------------------------|
| `SPRING_PROFILES_ACTIVE`    | `dev`                                                | Active Spring profile         |
| `SERVER_PORT`               | `8080`                                               | Backend HTTP port             |
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/smarttrafficflow`  | Database JDBC URL             |
| `SPRING_DATASOURCE_USERNAME`| `postgres`                                           | Database user                 |
| `SPRING_DATASOURCE_PASSWORD`| `postgres`                                           | Database password             |
| `APP_CORS_ALLOWED_ORIGINS`  | `http://localhost:5173,http://localhost:5174,...`     | Allowed frontend origins      |
| `VITE_API_BASE_URL`         | `http://localhost:8080/api`                          | Frontend API base URL         |

## Project Status

This project is at MVP stage with active development. Core features are fully integrated end-to-end. Known limitations:

- Backend is not yet containerized
- Map data is mocked (single region, hardcoded coordinates)
- No authentication or authorization
- UI and API messages are in Portuguese
