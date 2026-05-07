# Backend Design Document — TCG Tournament Service

**Date:** 2026-05-07  
**Version:** 1.0

---

## 1. Overview

The TCG Tournament Service backend is a **Spring Boot 3.2** application (Java 21) that manages Trading Card Game tournament data. It provides REST APIs for account management, tournament file uploads, leaderboard queries, and personal player statistics. Real-time upload progress is delivered via WebSockets (STOMP).

The application supports two deployment profiles:
- **Local** — SQLite database, in-memory cache, simple STOMP broker
- **Azure** — SQL Server database, Redis cache, Azure Service Bus for cross-instance WebSocket relay

---

## 2. Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.2 |
| Language | Java 21 (compiled to 25) |
| Build | Maven |
| REST / Web | Spring Web (spring-boot-starter-web) |
| WebSocket | Spring WebSocket (STOMP) |
| Security | Spring Security + JWT (jjwt 0.12.6) + BCrypt (jbcrypt 0.4) |
| Database (local) | SQLite (sqlite-jdbc) |
| Database (Azure) | SQL Server (mssql-jdbc 13.2.1) |
| Caching (local) | Spring Cache (ConcurrentMapCacheManager) |
| Caching (Azure) | Redis (spring-boot-starter-data-redis) |
| Messaging (Azure) | Azure Service Bus 7.17.8 |
| Monitoring | Spring Boot Actuator |

---

## 3. Architecture

The backend follows a **layered architecture**:

```
┌─────────────────────────────────────────────┐
│              Controllers (REST API)         │
├─────────────────────────────────────────────┤
│              Services (Business Logic)      │
├─────────────────────────────────────────────┤
│  Contracts (Repository Interfaces)          │
├─────────────────────────────────────────────┤
│  Database / Repositories (Data Access)      │
├─────────────────────────────────────────────┤
│  Config (Security, DB, Cache, WebSocket)    │
└─────────────────────────────────────────────┘
```

### 3.1 Package Structure

```
com.darkmarshal.tournamentservice
├── Config/
│   ├── SecurityConfig          — HTTP security, CORS, JWT filter chain
│   ├── AsyncConfig             — Thread pool for async upload processing
│   ├── Cache/
│   │   ├── LocalCacheConfig    — In-memory ConcurrentMap cache
│   │   └── AzureCacheConfig    — Redis-backed cache
│   ├── Database/
│   │   ├── DatabaseConfig      — DataSource configuration
│   │   ├── LocalDatabaseBeans  — SQLite DataSource
│   │   └── AzureDatabaseBeans  — SQL Server DataSource
│   └── WebSocket/
│       ├── WebSocketConfig          — Local STOMP broker
│       └── AzureWebSocketConfig     — Azure-compatible WebSocket config
├── Controllers/
│   ├── AccountController            — Signup, login, account CRUD, role management
│   ├── FileUploadController         — TDF file batch upload endpoint
│   ├── LeaderboardController        — Leaderboard queries by age division
│   ├── PersonalController           — Player personal stats
│   └── TournamentWebSocketController — WebSocket message handling
├── Contracts/                       — Repository interfaces
│   ├── IAccountRepository
│   ├── IEventRepository
│   ├── IPlayerRepository
│   ├── IResultsRepository
│   └── ITournamentRepository
├── Database/
│   ├── DBInitializer                — Schema creation on startup
│   └── Repositories/
│       ├── DatabaseInstance          — Shared DB access
│       ├── ConnectionUtil            — Connection helpers
│       ├── AccountRepository
│       ├── EventRepository
│       ├── PlayerRepository
│       ├── ResultsRepository
│       └── TournamentRepository
├── DTO/                             — Data Transfer Objects
│   ├── Account/   (LoginRequest, SignupRequest, AccountResponse, RoleUpdateRequest, PersonalPage/*)
│   ├── Event/     (EventSummaryDTO, EventDetailsDTO, EventDetailsRequest, DivisionDTO, PlayerDTO, PlayerMatchStats)
│   ├── Leaderboard/ (LeaderboardDTO)
│   └── Upload/    (UploadJob, UploadProgressDTO, UploadResponseDTO)
├── Models/                          — Domain entities
│   ├── Account, Event, Player, Result, Tournament
│   ├── Role (enum), AgeDivision (enum)
├── Security/
│   ├── JwtUtil                      — JWT token creation & validation
│   └── JwtAuthenticationFilter      — Per-request JWT filter
└── Services/
    ├── AccountService               — Account business logic (signup, login, roles)
    ├── CachedDataService            — Cached leaderboard & event queries
    ├── ImportService                — Event import orchestration
    ├── PlayerService                — Player-related logic
    ├── TDFParseService              — Parses .tdf tournament files into Event models
    ├── UploadProcessingService      — Async queue-based upload pipeline
    ├── Broadcast/
    │   ├── WebSocketBroadcastService — Abstraction for broadcasting messages
    │   ├── LocalBroadcastService     — Direct STOMP messaging (local profile)
    │   ├── AzureBroadcastService     — Azure-aware broadcasting
    │   └── AzureServiceBusRelay      — Cross-instance relay via Azure Service Bus
    └── Calculation/
        ├── CasualPointCalculator
        ├── ChallengePointCalculator
        └── CupPointCalculator
```

---

## 4. Database Schema

Five tables, with foreign key relationships:

```
accounts
├── id              INTEGER PK AUTOINCREMENT
├── username        TEXT UNIQUE NOT NULL
├── player_id       INTEGER NOT NULL
├── date_of_birth   TEXT NOT NULL
├── password_hash   TEXT NOT NULL
└── role            TEXT NOT NULL DEFAULT 'PLAYER'

players
├── id                  INTEGER PK
├── name                TEXT NOT NULL
├── ageDivision         TEXT
└── championship_points INTEGER DEFAULT 0

events
├── id          TEXT PK
├── name        TEXT NOT NULL
└── uploader_id INTEGER

tournaments
├── event_id        TEXT        ─FK→ events.id
├── age_division    TEXT
├── tournament_type TEXT
└── PK(event_id, age_division)

results
├── event_id                         TEXT     ─FK→ tournaments(event_id, age_division)
├── age_division                     TEXT
├── player_id                        INTEGER  ─FK→ players.id
├── placement                        INTEGER
├── points                           INTEGER DEFAULT 0
├── match_points                     INTEGER DEFAULT 0
├── opponent_win_percentage          REAL DEFAULT 0.0
├── opponent_opponent_win_percentage REAL DEFAULT 0.0
└── PK(event_id, age_division, player_id)
```

**Relationships:**
- `tournaments.event_id` → `events.id` (CASCADE delete)
- `results.(event_id, age_division)` → `tournaments.(event_id, age_division)` (CASCADE delete)
- `results.player_id` → `players.id` (CASCADE delete)

---

## 5. API Endpoints

### 5.1 Authentication & Accounts

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/accounts/signup` | Public | Register a new account |
| POST | `/api/accounts/login` | Public | Login, returns JWT |
| GET | `/api/accounts` | Admin | List all accounts |
| GET | `/api/accounts/{id}` | Authenticated | Get account by ID |
| PUT | `/api/accounts/{id}/role` | Admin | Update account role |

### 5.2 Events & Leaderboard

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/events/**` | Public | Event summaries and details |
| GET | `/api/leaderboard` | Public | All leaderboards |
| GET | `/api/leaderboard/{ageDivision}` | Public | Leaderboard by age division |

### 5.3 File Upload

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/upload` | Admin | Batch upload .tdf tournament files |

### 5.4 Personal Data

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/personal/{playerId}` | Authenticated | Player's personal stats, events, results |

### 5.5 WebSocket

| Path | Direction | Description |
|---|---|---|
| `/ws/**` | Connect | STOMP WebSocket handshake (public) |
| `/topic/events` | Server → All | Broadcast updated event list after import |
| `/queue/upload-progress` | Server → User | Per-user upload job progress updates |

---

## 6. Security

- **Authentication:** Stateless JWT-based. Tokens are issued on login and validated per-request by `JwtAuthenticationFilter`.
- **Password Storage:** BCrypt hashing via jbcrypt.
- **Authorization:** Role-based (`PLAYER`, `ADMIN`) enforced at the security filter chain level.
- **CORS:** Configured to allow the frontend origin (defaults to `http://localhost:5173`, overridable via `FRONTEND_URL` env var).
- **Session Policy:** `STATELESS` — no server-side sessions.
- **CSRF:** Disabled (appropriate for stateless JWT APIs).

---

## 7. Key Workflows

### 7.1 TDF File Upload & Processing

```
Admin uploads .tdf files via POST /api/upload
        │
        ▼
FileUploadController
  ├── Validates files, saves to temp directory
  ├── Creates UploadJob per file
  └── Enqueues jobs into UploadProcessingService
        │
        ▼
UploadProcessingService (async, thread pool)
  ├── Dequeues job from BlockingQueue (max 200)
  ├── PARSING  → TDFParseService.parseEventFile()
  ├── SAVING   → EventRepository.saveEvent()
  ├── Cache eviction via CachedDataService
  ├── COMPLETE → Broadcast updated events to /topic/events
  └── Progress updates sent to user via /queue/upload-progress
```

**Job statuses:** `QUEUED → PROCESSING → PARSING → SAVING → COMPLETE | FAILED`

### 7.2 Championship Point Calculation

Three calculator implementations based on tournament type:
- **CasualPointCalculator** — Points for casual events
- **ChallengePointCalculator** — Points for challenge events
- **CupPointCalculator** — Points for cup events

Each implements `IChampionshipPointCalculator` and is selected based on the `tournament_type` field.

### 7.3 Caching Strategy

`CachedDataService` provides cached access to leaderboard and event summary data. Caches are evicted after each successful file import to ensure data freshness.

- **Local profile:** `ConcurrentMapCacheManager` (in-memory)
- **Azure profile:** Redis-backed cache for shared state across instances

---

## 8. Deployment Profiles

### Local (`application-local.properties`)
- SQLite file-based database (`database.sqlite`)
- In-memory cache
- Simple STOMP broker for WebSocket
- Redis auto-configuration disabled

### Azure (`application-azure.properties`)
- SQL Server database
- Redis cache
- Azure Service Bus for cross-instance WebSocket message relay (`AzureServiceBusRelay`)
- Azure-compatible WebSocket configuration

---

## 9. Configuration Summary

| Property | Default |
|---|---|
| `spring.profiles.active` | `local` |
| `spring.servlet.multipart.max-file-size` | 10MB |
| `spring.servlet.multipart.max-request-size` | 500MB |
| `FRONTEND_URL` (env) | `http://localhost:5173` |

---

## 10. Diagrams

Existing architecture diagrams are available in the `/docs` directory:
- `Backend_Class_Diagram-0.png` — Class diagram
- `Backend___Layered_Architecture-0.png` — Layered architecture view
- `Sequence.png` / `Sequence.puml` — Sequence diagram
- `UML.plantuml` / `UML_Layered.plantuml` — PlantUML sources
