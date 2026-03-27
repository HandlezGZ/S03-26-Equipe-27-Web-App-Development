# Backend — SmartTrafficFlow API

API REST para registro e análise de dados de tráfego urbano.

## Tecnologias

| Camada        | Tecnologia                           |
|---------------|--------------------------------------|
| Linguagem     | Java 21                              |
| Framework     | Spring Boot 3.4.3                    |
| Persistência  | Spring Data JPA + Hibernate          |
| Banco de dados| PostgreSQL 15                        |
| Migrações     | Flyway                               |
| Docs da API   | SpringDoc OpenAPI 3.0 (Swagger UI)   |
| Build         | Maven                                |
| Testes        | JUnit 5 + Testcontainers             |

## Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15 rodando na porta `5432` (ou use o Docker Compose em `infra/`)

### Configuração

Copie e edite o arquivo de ambiente:

```bash
cp .env.example .env
```

Variáveis principais (consulte `.env` para a lista completa):

```env
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smarttrafficflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174
```

### Executar

```bash
mvn spring-boot:run
```

URL base da API: `http://localhost:8080/api`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Build

```bash
mvn clean package
java -jar target/backend-*.jar
```

## Estrutura do Projeto

```
src/main/java/com/smarttrafficflow/backend/
├── api/
│   ├── controller/     # Endpoints REST
│   ├── dto/            # Objetos de Requisição / Resposta
│   └── exception/      # Tratamento global de erros
├── config/             # Beans Spring (CORS, OpenAPI)
└── domain/
    ├── trafficrecord/
    │   ├── entity/     # Entidade JPA TrafficRecord
    │   ├── repository/ # Repositório Spring Data
    │   └── service/    # TrafficRecordService
    ├── simulation/     # SimulationService
    ├── analytics/      # AnalyticsService
    ├── insight/        # InsightService
    └── export/         # ExportService
```

## Referência da API

Todos os endpoints têm o prefixo `/api`.

### Registros de Tráfego

| Método | Caminho               | Descrição                          |
|--------|-----------------------|------------------------------------|
| POST   | `/traffic-records`    | Criar um registro de tráfego       |
| GET    | `/traffic-records`    | Listar todos os registros          |

**Corpo da requisição (POST):**

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

Tipos de via: `LOCAL`, `ARTERIAL`, `HIGHWAY`
Condições climáticas: `SUNNY`, `RAIN`, `CLOUDY`

### Simulações

| Método | Caminho                 | Descrição                               |
|--------|-------------------------|-----------------------------------------|
| POST   | `/simulations/generate` | Gerar registros de tráfego aleatórios   |

**Corpo da requisição:**

```json
{
  "scenarioName": "teste-hora-pico",
  "recordsToGenerate": 100
}
```

`recordsToGenerate` deve ser entre 1 e 250.

### Analytics

| Método | Caminho          | Descrição                                   |
|--------|------------------|---------------------------------------------|
| GET    | `/traffic-stats` | Estatísticas agregadas de volume de veículos |

Parâmetro de consulta `groupBy`: `hour` | `weekday` | `roadType`

**Resposta:**

```json
{
  "labels": ["00", "01", "02", ...],
  "values": [340, 120, 80, ...]
}
```

### Insights

| Método | Caminho             | Descrição                              |
|--------|---------------------|----------------------------------------|
| GET    | `/traffic-insights` | Insights automáticos sobre o tráfego  |

**Resposta:**

```json
{
  "insights": [
    "Pico de tráfego às 08h com 1240 veículos.",
    "Via com maior volume: HIGHWAY com 4500 veículos."
  ]
}
```

### Dados do Mapa

| Método | Caminho        | Descrição                              |
|--------|----------------|----------------------------------------|
| GET    | `/traffic-map` | Pontos geográficos para visualização   |

### Exportações

| Método | Caminho    | Descrição                          |
|--------|------------|------------------------------------|
| GET    | `/exports` | Exportar todos os registros        |

Parâmetro de consulta `format`: `csv` | `json`

## Banco de Dados

### Schema

Gerenciado pelo Flyway. As migrações ficam em `src/main/resources/db/migration/`.

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

### Perfis

| Perfil  | Flyway    | DDL auto       | Log SQL |
|---------|-----------|----------------|---------|
| default | habilitado| validate       | off     |
| dev     | desabilitado| create-drop  | on      |

## Testes

```bash
mvn test
```

Os testes de integração utilizam Testcontainers para subir uma instância real do PostgreSQL — nenhuma configuração manual é necessária.
