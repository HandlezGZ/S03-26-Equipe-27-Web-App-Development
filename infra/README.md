# Infra — Docker Compose

Ambiente de desenvolvimento local para o SmartTrafficFlow.

## Serviços

| Serviço               | Imagem                 | Porta        | Descrição                      |
|-----------------------|------------------------|--------------|--------------------------------|
| `smarttraffic-db`     | postgres:15-alpine     | 5432         | Banco de dados PostgreSQL      |
| `smarttraffic-frontend` | node:20-alpine       | 5174         | Servidor de desenvolvimento React |
| `pgadmin`             | dpage/pgadmin4         | 8100 → 80    | Interface de gerenciamento do banco |

> O backend Spring Boot **não** está containerizado. Ele roda localmente na porta `8080`.

## Uso

### Iniciar todos os serviços

```bash
docker compose up -d
```

### Iniciar um serviço específico

```bash
docker compose up -d smarttraffic-db
```

### Parar todos os serviços

```bash
docker compose down
```

### Parar e remover volumes (apaga os dados do banco)

```bash
docker compose down -v
```

### Ver logs

```bash
docker compose logs -f
docker compose logs -f smarttraffic-frontend
```

## Acesso

| Serviço    | URL                       | Credenciais                |
|------------|---------------------------|----------------------------|
| Frontend   | http://localhost:5174     | —                          |
| pgAdmin    | http://localhost:8100     | admin@admin.com / password |
| PostgreSQL | localhost:5432            | postgres / postgres        |

### Conectar o pgAdmin ao banco de dados

1. Abra http://localhost:8100 e faça login.
2. Adicione um novo servidor:
   - **Host**: `smarttraffic-db`
   - **Porta**: `5432`
   - **Banco de dados**: `smarttrafficflow`
   - **Usuário**: `postgres`
   - **Senha**: `postgres`

## Volumes

| Volume                  | Finalidade                                          |
|-------------------------|-----------------------------------------------------|
| `postgres_data`         | Persiste os dados do PostgreSQL entre reinicializações |
| `frontend_node_modules` | Faz cache do `node_modules` dentro do container    |

O código-fonte do frontend é montado via bind mount a partir de `../frontend/latest-app`, portanto alterações nos arquivos são refletidas imediatamente via Vite HMR sem necessidade de reconstruir o container.

## Rede

Todos os serviços compartilham uma rede bridge chamada `backend-network`. Use os nomes dos serviços (ex: `smarttraffic-db`) como hostnames na comunicação entre containers.

## Observações

- O container do frontend instala as dependências npm na inicialização (`npm install && npm run dev --host 0.0.0.0`). A primeira inicialização pode ser lenta enquanto as dependências são baixadas.
- As credenciais do banco de dados estão fixas no código para desenvolvimento local. Não utilize esses valores em produção.
