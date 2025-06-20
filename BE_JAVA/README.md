# Lighting Store Backend - Java Spring Boot

This is a Java Spring Boot conversion of the original Node.js Express backend for the Lighting Store application.

## 🚀 Features Converted

### ✅ Completed Features

1. **Product Management**
   - CRUD operations for products
   - Product search and filtering
   - Category-based product retrieval
   - Stock management
   - Soft delete functionality

2. **Data Models**
   - Product entity with MongoDB integration
   - Account/User entity with authentication fields
   - Order entity with embedded address and items
   - Payment entity for transaction management
   - Address entity for delivery information

3. **Database Integration**
   - MongoDB with Spring Data
   - Custom repository queries
   - Automatic auditing (createdAt, updatedAt)
   - Index management

4. **API Layer**
   - RESTful endpoints matching Node.js routes
   - Request/Response DTOs
   - Input validation
   - Error handling
   - CORS configuration

5. **Security & Configuration**
   - Spring Security setup
   - CORS configuration
   - Password encryption with BCrypt
   - JWT-ready configuration

## 🏗️ Architecture

```
BE_JAVA/
├── src/main/java/com/lighting/
│   ├── entity/           # MongoDB entities
│   ├── repository/       # Data access layer
│   ├── service/          # Business logic layer
│   ├── controller/       # REST API controllers
│   ├── dto/              # Data Transfer Objects
│   ├── config/           # Configuration classes
│   └── Application.java  # Main Spring Boot application
├── src/main/resources/
│   └── application.yml   # Application configuration
└── pom.xml              # Maven dependencies
```

## 🔧 Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Database**: MongoDB with Spring Data MongoDB
- **Security**: Spring Security + JWT
- **Caching**: Redis (configured)
- **File Storage**: MinIO (configured)
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB 4.4+
- Redis 6+
- MinIO (optional, for file storage)

## 🚀 Getting Started

### 1. Clone and Navigate
```bash
cd BE_JAVA
```

### 2. Configure Database
Update `src/main/resources/application.yml`:
```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: lights_store
```

### 3. Install Dependencies
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:3000`

## 📚 API Endpoints

### Products API
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product (soft delete)
- `GET /api/products/search` - Search products
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/type/{type}` - Get products by type
- `GET /api/products/in-stock` - Get in-stock products

### Future API Endpoints (To be implemented)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/orders` - Get orders
- `POST /api/orders` - Create order
- `GET /api/payments` - Get payments
- `POST /api/payments` - Process payment

## 🔄 Migration from Node.js

### Key Differences

| Node.js (Express) | Java (Spring Boot) |
|-------------------|-------------------|
| `app.js` | `Application.java` |
| `mongoose` schemas | JPA entities with `@Document` |
| Express routes | `@RestController` classes |
| Middleware | Spring Security filters |
| `package.json` | `pom.xml` |
| Environment variables | `application.yml` |

### Equivalent Mappings

| Node.js File | Java Equivalent |
|--------------|-----------------|
| `models/product.model.js` | `entity/Product.java` |
| `controllers/product.controller.js` | `controller/ProductController.java` |
| `services/product.service.js` | `service/ProductService.java` |
| `routers/product/index.js` | `@RequestMapping` in controllers |

## 🛠️ Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/lighting-store-0.0.1-SNAPSHOT.jar
```

### Docker Support
The application is configured to work with the existing `docker-compose.yaml` setup.

## 📊 Performance Considerations

1. **Database Indexing**: Automatic indexes on email, MongoDB ObjectIds
2. **Caching**: Redis integration ready for implementation
3. **Connection Pooling**: MongoDB connection pooling configured
4. **Validation**: Bean validation for input sanitization

## 🔜 TODO / Remaining Features

1. **Authentication & Authorization**
   - JWT token generation and validation
   - User registration and login
   - Role-based access control

2. **Order Management**
   - Order creation and processing
   - Order status management
   - Order history

3. **Payment Integration**
   - Payment processing
   - Payment status tracking
   - Payment history

4. **File Upload**
   - MinIO integration for image uploads
   - File validation and processing

5. **Email Service**
   - Email notifications
   - Email templates

6. **WebSocket Support**
   - Real-time notifications
   - Order status updates

7. **Advanced Features**
   - Redis caching implementation
   - Search optimization
   - API rate limiting
   - Comprehensive logging

## 🐛 Known Issues

1. Some repository methods need custom implementation
2. File upload functionality needs MinIO integration
3. Email service configuration pending
4. WebSocket implementation pending

## 📝 Notes

- All API responses maintain the same JSON structure as the Node.js version
- Error handling follows the same pattern as the original backend
- Database schema is compatible with the existing MongoDB data
- CORS is configured to allow frontend integration

## 🤝 Contributing

1. Follow Spring Boot best practices
2. Maintain compatibility with the existing frontend
3. Add comprehensive tests for new features
4. Update documentation for any API changes
