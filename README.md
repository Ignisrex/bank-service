# Minimal Spring Boot Banking REST Service

A comprehensive banking REST service built with Spring Boot 3.2.5, SQLite, and JWT authentication. This service provides complete banking operations including account management, money transfers, transactions, and card management.

## 🚀 Features

### Core Banking Operations

- **Account Management**: Create, read, and manage banking accounts
- **Money Transfers**: Transfer funds between accounts with transaction tracking
- **Transaction History**: Complete transaction logging and statement generation
- **Card Management**: Debit/Credit card creation and validation

### Authentication & Security

- **JWT Authentication**: Secure token-based authentication
- **User Registration**: Account holder signup with validation
- **Card Validation**: Secure card number and CVV validation
- **Password Encryption**: BCrypt password hashing

### Technical Features

- **SQLite Database**: Lightweight, embedded database with Hibernate JPA
- **Docker Support**: Complete containerization with docker-compose
- **Comprehensive Testing**: JUnit integration tests with in-memory database
- **Health Monitoring**: Actuator endpoints for service health
- **Data Validation**: Input validation with meaningful error messages

## 🏗️ Technical Stack

- **Java 17** with Spring Boot 3.2.5
- **SQLite** with Hibernate 6 JPA
- **JWT** for authentication
- **Maven** for build management
- **Docker** for containerization
- **JUnit 5** for testing

## 📋 API Endpoints

### Authentication

```
POST /auth/signup              - User registration with account creation
POST /auth/login               - User authentication (get JWT token)
POST /auth/card/validate       - Card validation (get temporary JWT)
```

### Banking Operations

```
GET  /api/accounts             - List user accounts (requires JWT)
POST /api/accounts             - Create new account (requires JWT)
POST /api/transfer             - Transfer money between accounts (requires JWT)
GET  /api/accounts/{id}/cards  - Get account cards (requires JWT)
GET  /api/accounts/{id}/statements - Get account statements (requires JWT)
```

### Monitoring

```
GET  /actuator/health          - Service health check
```

## 🐳 Running with Docker (Recommended)

### Prerequisites

- Docker and Docker Compose installed
- Git (to clone the repository)

### Quick Start

1. **Clone and navigate to the project:**

   ```bash
   git clone <repository-url>
   cd bank-service
   ```

2. **Run with Docker Compose:**

   ```bash
   docker compose up --build -d
   ```

3. **The service will be available at:** `http://localhost:8080`

4. **Check service health:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Docker Commands

```bash
# Start services
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down

# Rebuild and restart
docker compose up --build -d
```

## 💻 Running Locally (Development)

### Prerequisites

- Java 17+
- Maven 3.6+

### Steps

1. **Build the project:**

   ```bash
   mvn clean compile
   ```

2. **Run tests:**

   ```bash
   mvn test
   ```

3. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

## 🧪 Testing the API

### 📮 Postman Collection (Recommended)

For easy manual testing, import the `Bank Service API.postman_collection.json` file into Postman. This comprehensive collection includes:

- ✅ **All API endpoints** with pre-configured requests
- ✅ **Automatic JWT token management** - tokens are captured and applied automatically
- ✅ **Test scripts** for response validation
- ✅ **Environment variables** for easy configuration
- ✅ **Ready-to-use examples** with sample data

**Quick Start with Postman:**
1. Import `Bank Service API.postman_collection.json` into Postman
2. Update the `baseUrl` variable to `http://localhost:8080` (already set by default)
3. Run the "User Signup" or "User Login" request to authenticate
4. JWT token will be automatically captured for subsequent requests
5. Test all endpoints with proper authentication

### 🔧 Manual Testing with cURL

### 1. User Registration

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "accounts": [{
      "type": "CHECKING",
      "primaryFlag": true,
      "balance": 1000.0
    }]
  }'
```

### 2. User Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

### 3. Get User Accounts (requires JWT token)

```bash
curl -X GET http://localhost:8080/api/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Transfer Money

```bash
curl -X POST http://localhost:8080/api/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 100.0,
    "description": "Payment"
  }'
```

### 5. Card Validation (Alternative Authentication)

```bash
curl -X POST http://localhost:8080/auth/card/validate \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "1234567890123456",
    "cvv": "123"
  }'
```

## 📊 Database Schema

The service uses SQLite with the following main tables:

- **account_holder**: User information and credentials
- **account**: Banking accounts with balances and types
- **bank_transaction**: Transaction history and records
- **card**: Debit/Credit card information

Database file is stored at `./data/bank.db` when using Docker.

## 🔧 Configuration

### Environment Variables

```env
# Database
SPRING_DATASOURCE_URL=jdbc:sqlite:./data/bank.db

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=600000

# Server
SERVER_PORT=8080
```

### Application Properties

Key configurations in `application.properties`:

- Database connection and JPA settings
- Logging configuration
- Security settings
- Actuator endpoints

## 🧪 Running Tests

The project includes comprehensive integration tests:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BankServiceApplicationTests

# Run specific test method
mvn test -Dtest=BankServiceApplicationTests#testAuthSignup
```

### Test Coverage

- ✅ User signup and authentication
- ✅ Login functionality with JWT generation
- ✅ Card validation system
- ✅ Error handling for invalid requests
- ✅ Health endpoint validation
- ✅ Integration tests with in-memory database

## 📁 Project Structure

```
src/
├── main/java/com/bankservice/
│   ├── controller/          # REST endpoints
│   │   ├── AuthController.java
│   │   └── AccountController.java
│   ├── service/            # Business logic
│   │   ├── AuthService.java
│   │   └── AccountService.java
│   ├── model/              # JPA entities
│   │   ├── AccountHolder.java
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   └── Card.java
│   ├── repository/         # Data access layer
│   ├── dto/               # Data transfer objects
│   ├── config/            # Configuration classes
│   └── BankServiceApplication.java
├── test/java/             # Test classes
└── resources/
    ├── application.properties
    └── data.sql           # Optional seed data
```

## 🔒 Security Features

- **Password Encryption**: BCrypt hashing for user passwords
- **JWT Authentication**: Stateless token-based authentication
- **Input Validation**: Comprehensive validation for all endpoints
- **Sensitive Data Protection**: CVV, PIN, and passwords are not exposed in responses
- **CORS Configuration**: Configurable cross-origin resource sharing

## 🚀 Deployment

### Production Considerations

1. **Security**: Replace hardcoded JWT secret with environment variable
2. **Database**: Consider PostgreSQL or MySQL for production
3. **Monitoring**: Enable detailed logging and monitoring
4. **Scaling**: Configure for horizontal scaling
5. **Backup**: Implement database backup strategy

### Environment Setup

```bash
# Production environment variables
export JWT_SECRET="your-production-secret-key"
export SPRING_PROFILES_ACTIVE="production"
export DATABASE_URL="your-production-database-url"
```

## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**

   ```bash
   # Check what's using port 8080
   netstat -ano | findstr :8080
   # Kill the process or use different port
   ```

2. **Database Locked**

   ```bash
   # Remove database file and restart
   docker compose down
   rm data/bank.db
   docker compose up -d
   ```

3. **JWT Token Expired**
   - Tokens expire in 10 minutes by default
   - Re-authenticate to get new token

### Logs and Debugging

```bash
# View application logs
docker compose logs bank-service

# Follow logs in real-time
docker compose logs -f bank-service

# Check container status
docker compose ps
```

## 📝 Development Notes

- Seed data includes a test user: `test@example.com` / `password123`
- JWT tokens have 10-minute expiration (configurable)
- Database is automatically created and seeded on first startup
- All timestamps are in UTC
- Account numbers are auto-generated
- Transaction history is immutable

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
