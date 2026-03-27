# SmartTrafficFlow

Aplicação web full-stack para monitoramento, registro e análise de dados de tráfego urbano. Permite inserção manual de dados, simulação de cenários de tráfego, análises agregadas e visualização em mapa.

## Visão Geral da Arquitetura

```
repository/
├── backend/          # API REST com Spring Boot (Java 21)
├── frontend/
│   └── latest-app/   # Dashboard React + TypeScript (Vite)
└── infra/            # Orquestração com Docker Compose
```

### Comunicação entre Serviços

```
Frontend  (http://localhost:5174)
    │  HTTP REST (CORS habilitado)
    ▼
Backend   (http://localhost:8080/api)
    │  JDBC
    ▼
PostgreSQL (localhost:5432)
```

## Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Node 20+
- Docker & Docker Compose

### 1. Iniciar o banco de dados e o frontend via Docker

```bash
cd infra
docker compose up -d
```

Isso inicia:
- **PostgreSQL 15** na porta `5432`
- **Frontend React** na porta `5174`
- **pgAdmin** na porta `8100`

### 2. Iniciar o backend localmente

```bash
cd backend
cp .env.example .env   # ajuste se necessário
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Serviços

| Serviço    | Tecnologia              | Porta | README                              |
|------------|-------------------------|-------|-------------------------------------|
| Backend    | Java 21 / Spring Boot   | 8080  | [backend/README.md](backend/README.md) |
| Frontend   | React 18 / TypeScript   | 5174  | [frontend/latest-app/README.md](frontend/latest-app/README.md) |
| Infra      | Docker Compose          | —     | [infra/README.md](infra/README.md)  |

## Funcionalidades Principais

- **Registros de Tráfego** — Criação e listagem de entradas de tráfego (tipo de via, volume, clima, região, evento)
- **Simulação** — Geração de até 250 registros aleatórios para um cenário definido
- **Analytics** — Agregação do volume de veículos por hora, dia da semana ou tipo de via
- **Insights** — Resumos automáticos de pico por hora e por tipo de via
- **Mapa** — Visualização de pontos de tráfego por região (Leaflet)
- **Exportações** — Download de todos os registros em CSV ou JSON
- **Documentação da API** — OpenAPI 3.0 / Swagger UI incluído

## Variáveis de Ambiente

Consulte o arquivo `.env` no diretório `backend/` para a lista completa. Variáveis principais:

| Variável                    | Padrão                                               | Descrição                          |
|-----------------------------|------------------------------------------------------|------------------------------------|
| `SPRING_PROFILES_ACTIVE`    | `dev`                                                | Perfil ativo do Spring             |
| `SERVER_PORT`               | `8080`                                               | Porta HTTP do backend              |
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/smarttrafficflow`  | URL JDBC do banco de dados         |
| `SPRING_DATASOURCE_USERNAME`| `postgres`                                           | Usuário do banco de dados          |
| `SPRING_DATASOURCE_PASSWORD`| `postgres`                                           | Senha do banco de dados            |
| `APP_CORS_ALLOWED_ORIGINS`  | `http://localhost:5173,http://localhost:5174,...`     | Origens permitidas pelo CORS       |
| `VITE_API_BASE_URL`         | `http://localhost:8080/api`                          | URL base da API no frontend        |

