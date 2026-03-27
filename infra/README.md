# Infra — Docker Compose

Local development environment for SmartTrafficFlow.

## Services

| Service               | Image                  | Port         | Description               |
|-----------------------|------------------------|--------------|---------------------------|
| `smarttraffic-db`     | postgres:15-alpine     | 5432         | PostgreSQL database        |
| `smarttraffic-frontend` | node:20-alpine       | 5174         | React dev server           |
| `pgadmin`             | dpage/pgadmin4         | 8100 → 80    | Database management UI     |

> The Spring Boot backend is **not** containerized. It runs locally on port `8080`.

## Usage

### Start all services

```bash
docker compose up -d
```

### Start a specific service

```bash
docker compose up -d smarttraffic-db
```

### Stop all services

```bash
docker compose down
```

### Stop and remove volumes (wipes database data)

```bash
docker compose down -v
```

### View logs

```bash
docker compose logs -f
docker compose logs -f smarttraffic-frontend
```

## Access

| Service    | URL                                          | Credentials             |
|------------|----------------------------------------------|-------------------------|
| Frontend   | http://localhost:5174                        | —                       |
| pgAdmin    | http://localhost:8100                        | admin@admin.com / password |
| PostgreSQL | localhost:5432                               | postgres / postgres     |

### Connecting pgAdmin to the database

1. Open http://localhost:8100 and log in.
2. Add a new server:
   - **Host**: `smarttraffic-db`
   - **Port**: `5432`
   - **Database**: `smarttrafficflow`
   - **Username**: `postgres`
   - **Password**: `postgres`

## Volumes

| Volume                  | Purpose                                    |
|-------------------------|--------------------------------------------|
| `postgres_data`         | Persists PostgreSQL data across restarts   |
| `frontend_node_modules` | Caches `node_modules` inside the container |

The frontend source code is bind-mounted from `../frontend/latest-app`, so file changes are reflected immediately via Vite HMR without rebuilding the container.

## Network

All services share a bridge network named `backend-network`. Use container service names (e.g., `smarttraffic-db`) as hostnames when communicating between containers.

## Notes

- The frontend container installs npm dependencies on startup (`npm install && npm run dev --host 0.0.0.0`). First startup may be slow while dependencies are downloaded.
- Database credentials are hardcoded for local development. Do not use these values in production.
