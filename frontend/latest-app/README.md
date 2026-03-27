# Frontend — SmartTrafficFlow Dashboard

React dashboard for visualizing and interacting with urban traffic data.

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Framework    | React 18.3.1                        |
| Language     | TypeScript 5.5.4                    |
| Build tool   | Vite 5.3.4                          |
| Charts       | Recharts 2.12.7                     |
| Maps         | Leaflet 1.9.4 + React-Leaflet 4.2.1 |
| Runtime      | Node 20                             |

## Getting Started

### Prerequisites

- Node 20+
- Backend API running on `http://localhost:8080` (see `backend/README.md`)

### Install and run

```bash
npm install
npm run dev
```

The app runs on `http://localhost:5174`.

### Environment

Create a `.env` file (or `.env.local`) to override the API URL:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

If this variable is not set, the app defaults to `http://localhost:8080/api`.

### Other commands

```bash
npm run build     # Production build (outputs to dist/)
npm run preview   # Preview production build on port 4174
```

## Project Structure

```
src/
├── main.tsx          # React root
├── App.tsx           # Main component (dashboard logic and UI)
├── styles.css        # Global styles
├── lib/
│   └── api.ts        # Typed HTTP client (fetch-based)
└── types/
    └── api.ts        # Shared TypeScript interfaces
```

## Views

### Home — Insights Dashboard

- **Summary cards**: total vehicles, peak window, mapped regions, average load
- **Charts**:
  - Hourly pressure curve (area chart)
  - Weekday distribution (bar chart)
  - Road type division (bar chart)
- **Map**: Interactive Leaflet map with traffic point markers
- **Insights panel**: Auto-generated narrative summaries from the backend

### Workspace — Operations

- **Record form**: Manual entry of a traffic record (timestamp, road type, volume, region, event, weather)
- **Simulation form**: Generate 1–250 random records for a named scenario
- **Records table**: All traffic records with client-side filtering by region, road type, weather, and event type
- **Export buttons**: Download all records as CSV or JSON

Both views share a live connection status indicator and a view switcher.

## API Client

`src/lib/api.ts` exposes typed functions that wrap `fetch` calls:

| Function               | Method | Endpoint                     |
|------------------------|--------|------------------------------|
| `getTrafficRecords`    | GET    | `/traffic-records`           |
| `createTrafficRecord`  | POST   | `/traffic-records`           |
| `getTrafficStats`      | GET    | `/traffic-stats?groupBy=...` |
| `getTrafficInsights`   | GET    | `/traffic-insights`          |
| `generateSimulation`   | POST   | `/simulations/generate`      |
| `getTrafficMap`        | GET    | `/traffic-map`               |
| `getExport`            | GET    | `/exports?format=...`        |

Errors are surfaced as `ApiError` instances with a message from the response body.

## Type Definitions

Key interfaces in `src/types/api.ts`:

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
