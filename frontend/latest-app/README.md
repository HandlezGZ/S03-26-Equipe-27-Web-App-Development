# Frontend — SmartTrafficFlow Dashboard

Dashboard React para visualização e interação com dados de tráfego urbano.

## Tecnologias

| Camada         | Tecnologia                          |
|----------------|-------------------------------------|
| Framework      | React 18.3.1                        |
| Linguagem      | TypeScript 5.5.4                    |
| Ferramenta de build | Vite 5.3.4                    |
| Gráficos       | Recharts 2.12.7                     |
| Mapas          | Leaflet 1.9.4 + React-Leaflet 4.2.1 |
| Runtime        | Node 20                             |

## Início Rápido

### Pré-requisitos

- Node 20+
- API backend rodando em `http://localhost:8080` (veja `backend/README.md`)

### Instalar e executar

```bash
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:5174`.

### Ambiente

Crie um arquivo `.env` (ou `.env.local`) para sobrescrever a URL da API:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Se a variável não for definida, o padrão é `http://localhost:8080/api`.

### Outros comandos

```bash
npm run build     # Build de produção (saída em dist/)
npm run preview   # Pré-visualização do build de produção na porta 4174
```

## Estrutura do Projeto

```
src/
├── main.tsx          # Raiz do React
├── App.tsx           # Componente principal (lógica e UI do dashboard)
├── styles.css        # Estilos globais
├── lib/
│   └── api.ts        # Cliente HTTP tipado (baseado em fetch)
└── types/
    └── api.ts        # Interfaces TypeScript compartilhadas
```

## Telas

### Home — Dashboard de Insights

- **Cards de resumo**: total de veículos, janela de pico, regiões mapeadas, carga média
- **Gráficos**:
  - Curva de pressão por hora (gráfico de área)
  - Distribuição por dia da semana (gráfico de barras)
  - Divisão por tipo de via (gráfico de barras)
- **Mapa**: Mapa interativo Leaflet com marcadores de pontos de tráfego
- **Painel de insights**: Resumos narrativos gerados automaticamente pelo backend

### Workspace — Operações

- **Formulário de registro**: Inserção manual de um registro de tráfego (timestamp, tipo de via, volume, região, evento, clima)
- **Formulário de simulação**: Geração de 1 a 250 registros aleatórios para um cenário nomeado
- **Tabela de registros**: Todos os registros com filtros por região, tipo de via, clima e tipo de evento
- **Botões de exportação**: Download de todos os registros em CSV ou JSON

Ambas as telas compartilham um indicador de status de conexão e um alternador de visão.

## Cliente de API

`src/lib/api.ts` expõe funções tipadas que encapsulam chamadas `fetch`:

| Função                 | Método | Endpoint                     |
|------------------------|--------|------------------------------|
| `getTrafficRecords`    | GET    | `/traffic-records`           |
| `createTrafficRecord`  | POST   | `/traffic-records`           |
| `getTrafficStats`      | GET    | `/traffic-stats?groupBy=...` |
| `getTrafficInsights`   | GET    | `/traffic-insights`          |
| `generateSimulation`   | POST   | `/simulations/generate`      |
| `getTrafficMap`        | GET    | `/traffic-map`               |
| `getExport`            | GET    | `/exports?format=...`        |

Erros são lançados como instâncias de `ApiError` com a mensagem vinda do corpo da resposta.

## Definições de Tipos

Interfaces principais em `src/types/api.ts`:

```typescript
interface TrafficRecord {
  id: string;
  timestamp: string;
  roadType: string;
  vehicleVolume: number;
  eventType?: string;
  weather?: string;
  region?: string;
}

interface TrafficStatsResponse {
  labels: string[];
  values: number[];
}

interface SimulationRequest {
  scenarioName: string;
  recordsToGenerate: number; // 1–250
}
```
