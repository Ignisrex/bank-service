# Minimal Spring Boot Banking REST Service

## Features

- SQLite database
- REST endpoints for account and transaction management
- JWT authentication
- Seed test case for signup

## Running Locally with Docker

### Prerequisites

- Docker and Docker Compose installed
- Git (to clone the repository)

### Quick Start

1. **Clone and navigate to the project directory:**

   ```bash
   cd bank-service
   ```

2. **Run with Docker Compose:**

   ```bash
   docker-compose up --build
   ```

3. **The service will be available at:** `http://localhost:8080`

### Manual Docker Build (Alternative)

1. **Build the Docker image:**

   ```bash
   docker build -t bank-service .
   ```

2. **Run the container:**
   ```bash
   docker run -p 8080:8080 -v $(pwd)/data:/app/data bank-service
   ```

## Running Without Docker

1. Ensure Java 17+ and Maven are installed.
2. Build and run:
   ```shell
   mvn spring-boot:run
   ```

## Testing the API

### 1. Test Signup Endpoint

```bash
curl -X POST http://localhost:8080/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "accounts": [{
      "type": "DEBIT",
      "primaryFlag": true,
      "balance": 1000.0
    }]
  }'
```

### 2. Test Card Validation (Get JWT Token)

```bash
curl -X POST http://localhost:8080/auth/card/validate \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "1234567890123456",
    "cvv": "123"
  }'
```

### 3. Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Project Structure

- `controller/` - REST endpoints
- `service/` - Business logic
- `model/` - JPA entities
- `repository/` - Data access layer
- `config/` - Configuration classes
- `dto/` - Data transfer objects

## Database

- SQLite database file stored in `./data/bank.db` (when using Docker)
- Database is automatically created and seeded on startup

## Development Notes

- JWT tokens expire in 10 minutes
- Seed data includes a test user with a DEBIT account
- For production, replace the hardcoded JWT secret key

## Stopping the Service

```bash
docker-compose down
```

## Logs

```bash
docker-compose logs -f bank-service
```
