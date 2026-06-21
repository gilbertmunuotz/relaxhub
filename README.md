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

**IntelliJ:** Use run configuration **Relaxhub Backend** (included in `backend/.run/`), or set **Working directory** to the `backend` folder where `.env` lives. If you open the whole monorepo in IntelliJ, set working directory to `.../relaxhub/backend`.

The API starts on `http://localhost:8080`.

Verify:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/health/db
```

### Test auth (Step 2)

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"secret12","phone":""}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"secret12"}'
```

Ensure `JWT_SECRET` is set in `backend/.env` (at least 32 characters).

### Step 3 features

- **Reset password:** Login → Forgot password? → enter email + new password
- **Logout:** Dashboard → Settings tab → Log out
- **Google Maps:** Dashboard → Maps tab — set `GOOGLE_MAPS_API_KEY` in `frontend/.env` (see below)

### Step 4 features

- **Feedback:** Settings → User feedback (rating + message)
- **Complaints:** Settings → Submit a complaint
- **Receipts:** Settings → Visit receipts (date/time pickers + history)
- **Help / Privacy:** Static content screens
- **Contact:** Call, SMS, and email intents

### Step 5 features

- **Nearby spots API:** `GET /api/spots`, `GET /api/spots/nearby?lat=&lng=&radiusKm=&type=`, `GET /api/spots/{id}` (JWT required)
- **Seed data:** 10 sample restaurants and relaxation spots around Dar es Salaam (loaded on first backend start)
- **Maps:** Dashboard → Maps tab shows markers from the API; filter by All / Restaurants / Relaxation
- **MainActivity:** Full-screen nearby places map (Home → Open nearby places map, or Maps tab → Open full map)
- **Home screen:** Welcome message, quick actions, and recent visits from the receipts API
- **Notifications:** Welcome notification on first dashboard visit; visit-saved notification after saving a receipt (Android 13+ asks for permission)

Test spots API (replace `TOKEN` with a login JWT):

```bash
curl http://localhost:8080/api/spots/nearby?lat=-6.7924&lng=39.2083&radiusKm=20 \
  -H "Authorization: Bearer TOKEN"
```

### Frontend secrets (`frontend/.env`)

```bash
cd frontend
cp .env.example .env
# Edit .env and set GOOGLE_MAPS_API_KEY=...
```

Gradle reads `frontend/.env` at build time and injects the key into the app — it is **not** stored in committed `strings.xml`.

**Important:** A Maps key inside an APK can still be extracted. Always restrict it in [Google Cloud Console](https://console.cloud.google.com/) to your app package (`com.relaxhub.frontend`) and debug/release SHA-1 fingerprint.
- **JWT protected API:** Account tab calls `GET /api/auth/me` with Bearer token

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
