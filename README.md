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

## Run the frontend

```bash
cd frontend
./gradlew :app:assembleDebug
```

Open the project in Android Studio by selecting the `frontend/` directory.

## Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` by default.

## Local configuration

Copy environment-specific settings locally (these files are gitignored):

```bash
# backend — optional overrides
cp backend/src/main/resources/application-local.yaml.example \
   backend/src/main/resources/application-local.yaml
```
