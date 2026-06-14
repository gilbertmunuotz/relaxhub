# Relaxhub

Monorepo for the Relaxhub wellness app.

## Structure

| Directory   | Stack                          | Description        |
|-------------|--------------------------------|--------------------|
| `frontend/` | Android (Java, Gradle)         | Mobile client      |
| `backend/`  | Spring Boot 4.1 (Java 25, Maven) | REST API       |

## Prerequisites

- **Frontend:** Android SDK, JDK 11+
- **Backend:** JDK 25, Maven (or use the included wrapper)
- **Database:** PostgreSQL installed locally (Docker is optional)

## Run the frontend

```bash
cd frontend
./gradlew :app:assembleDebug
```

Open the project in Android Studio by selecting the `frontend/` directory.

## Run the backend

### 1. Set up PostgreSQL (no Docker required)

If PostgreSQL is installed on macOS (Homebrew, Postgres.app, etc.):

```bash
# Create the database once
createdb relaxhub
```

Use your macOS username as `DB_USERNAME` in `.env`. Leave `DB_PASSWORD` empty if your local Postgres uses peer/trust auth.

### 2. Configure environment variables

```bash
cd backend
cp .env.example .env
# Edit .env with your DB_USERNAME and other values
```

The backend loads `backend/.env` automatically at startup.

### 3. Start the API

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

Verify:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/health/db
```

### Optional: Docker PostgreSQL

If you prefer Docker and have it installed:

```bash
cd backend
docker compose up -d
```

Then set in `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/relaxhub
DB_USERNAME=relaxhub
DB_PASSWORD=relaxhub
```

## Configuration files

| File | Purpose |
|------|---------|
| `backend/.env` | Local secrets and DB credentials (gitignored) |
| `backend/.env.example` | Template to copy |
| `backend/src/main/resources/application.yaml` | Base Spring config; reads `${DB_*}` from `.env` |
| `backend/src/main/resources/application-dev.yaml` | Optional dev profile (JWT settings for later steps) |

To use the dev profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```
