# Banking REST Service - Solution Documentation

## Project Overview
A comprehensive banking REST service built with Spring Boot 3.x, SQLite database, and JWT authentication. This service provides core banking functionality including account management, transactions, money transfers, and card operations.

## Technology Stack
- **Framework**: Spring Boot 3.2.5
- **Database**: SQLite with Hibernate 6
- **Security**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Java Version**: 17
- **Additional Libraries**: Lombok, Spring Validation, Spring Actuator

## Architecture

### Domain Model
```
AccountHolder (1) ──── (*) Account (1) ──── (*) Transaction
                                   │
                                   └── (*) Card
```

### Package Structure
```
com.bankservice/
├── config/           # Security and database configuration
├── controller/       # REST endpoints
├── dto/             # Data Transfer Objects
├── model/           # JPA entities
├── repository/      # Data access layer
└── service/         # Business logic
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (recommended)
- Git

### Option 1: Docker Deployment (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd bank-service
   ```

2. **Build and run with Docker Compose**
   ```bash
   docker compose up --build -d
   ```

3. **Verify deployment**
   ```bash
   # Check application status
   curl http://localhost:8080/actuator/health
   
   # Expected response:
   # {"status":"UP"}
   ```

4. **View logs**
   ```bash
   docker compose logs -f app
   ```

### Option 2: Local Development

1. **Clone and build**
   ```bash
   git clone <repository-url>
   cd bank-service
   mvn clean install
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Application will be available at**
   - Main service: http://localhost:8080
   - Health check: http://localhost:8080/actuator/health

## API Documentation

### Authentication Endpoints

#### Sign Up
```http
POST /auth/signup
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "securePassword123"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
    "email": "john.doe@example.com",
    "password": "securePassword123"
}
```

### Account Management

#### Get All Accounts
```http
GET /api/accounts
Authorization: Bearer <jwt-token>
```

#### Get Account by ID
```http
GET /api/accounts/{accountId}
Authorization: Bearer <jwt-token>
```

#### Create Account
```http
POST /api/accounts
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
    "accountType": "CHECKING",
    "initialBalance": 1000.00
}
```

### Transaction Operations

#### Get Account Transactions
```http
GET /api/accounts/{accountId}/transactions
Authorization: Bearer <jwt-token>
```

#### Money Transfer
```http
POST /api/transfer
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 500.00,
    "description": "Payment for services"
}
```

### Card Management

#### Get Account Cards
```http
GET /api/accounts/{accountId}/cards
Authorization: Bearer <jwt-token>
```

#### Issue New Card
```http
POST /api/accounts/{accountId}/cards
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
    "cardType": "DEBIT"
}
```

### Statements

#### Generate Account Statement
```http
GET /api/accounts/{accountId}/statements
Authorization: Bearer <jwt-token>
Query Parameters:
- startDate: yyyy-MM-dd (optional)
- endDate: yyyy-MM-dd (optional)
```

## Database Schema

### Tables Created
- `account_holder` - Customer information
- `account` - Bank accounts
- `bank_transaction` - Financial transactions
- `card` - Payment cards

### Sample Data
The application includes seed data for testing:
- Default account holder: "John Doe"
- Sample checking account with initial balance
- Test transactions for demonstration

## Security Considerations

### Current Implementation
1. **JWT Authentication**: Stateless token-based authentication
2. **Password Encoding**: BCrypt hashing for user passwords
3. **SQL Injection Protection**: JPA/Hibernate parameter binding
4. **CORS Configuration**: Configurable for different environments

### Production Recommendations
1. **Environment Variables**: Use for sensitive configuration
2. **HTTPS Only**: Enforce SSL/TLS in production
3. **Rate Limiting**: Implement API rate limiting
4. **Input Validation**: Enhanced validation for all endpoints
5. **Audit Logging**: Track all financial operations
6. **Token Expiration**: Implement refresh token mechanism

## Configuration

### Environment Variables
```env
# Database
DB_PATH=/app/data/bank.db

# JWT Security
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Application Properties
```properties
# SQLite Database
spring.datasource.url=jdbc:sqlite:${DB_PATH:./bank.db}
spring.datasource.driver-class-name=org.sqlite.JDBC

# Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.show-sql=true

# Security
banking.jwt.secret=${JWT_SECRET:mySecretKey}
banking.jwt.expiration=${JWT_EXPIRATION:86400000}
```

## Testing

### Running Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test coverage report
mvn jacoco:report
```

### Manual Testing Flow
1. Start the application
2. Sign up a new user via `/auth/signup`
3. Login to get JWT token via `/auth/login`
4. Use token to access protected endpoints
5. Test account creation, transactions, and transfers

## Troubleshooting

### Common Issues

#### Database Connection
```bash
# Check if SQLite file is created
ls -la bank.db

# Verify database schema
sqlite3 bank.db ".schema"
```

#### Docker Issues
```bash
# Rebuild without cache
docker compose build --no-cache

# Check container logs
docker compose logs app
```

#### JWT Token Issues
- Ensure token is included in Authorization header: `Bearer <token>`
- Check token expiration (default 24 hours)
- Verify JWT secret configuration

## Development Notes

### Next Phase Implementation
- Complete REST endpoint implementation
- Enhanced security features
- Comprehensive API testing
- Performance optimization
- Production-ready configuration

### Known Limitations
- Single-node deployment (no clustering)
- Basic JWT implementation (no refresh tokens)
- SQLite limitations for high concurrency
- Minimal input validation

## Monitoring and Health Checks

### Health Endpoint
```http
GET /actuator/health
```

### Application Metrics
```http
GET /actuator/metrics
GET /actuator/info
```

## Deployment

### Production Deployment
1. Use environment-specific configuration
2. Implement proper logging configuration
3. Set up monitoring and alerting
4. Configure backup strategy for SQLite database
5. Implement CI/CD pipeline

### Scaling Considerations
- Consider PostgreSQL for production
- Implement database connection pooling
- Add caching layer (Redis)
- Load balancing for multiple instances
