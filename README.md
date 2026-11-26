# ShopSphere

An online electronics store: a Spring Boot REST API with JWT security and MySQL, plus a multi-page web front end served by the same application.

## Features

- Register and log in with a BCrypt-hashed password and a JWT token
- Role-based access: `CUSTOMER` and `ADMIN`
- Products with pictures, search, sorting and pagination; admins can add, edit and delete
- Shopping cart with quantity controls, and one checkout that places all items in a single order
- Orders are processed in one transaction: stock is checked and reduced all or nothing, and the price is saved on each order line so later price changes do not alter old orders
- Order history per customer
- Input validation and clear JSON error messages through a global exception handler
- Swagger UI for API documentation
- Unit tests for the order logic (JUnit 5 and Mockito)
- Database password and JWT secret kept out of source control

## Tech stack

Java 21, Spring Boot 4, Spring Security, Spring Data JPA (Hibernate), MySQL, JWT (jjwt), Lombok, springdoc-openapi (Swagger), Maven, HTML, CSS and JavaScript

## Run it locally

1. Install Java 21 and MySQL.
2. Create the database and a user:

```sql
CREATE DATABASE shopsphere;
CREATE USER 'shopsphere_user'@'localhost' IDENTIFIED BY 'your-password';
GRANT ALL PRIVILEGES ON shopsphere.* TO 'shopsphere_user'@'localhost';
```

3. Create `src/main/resources/secrets.properties` (this file is ignored by Git):

```
spring.datasource.password=your-password
jwt.secret=a-random-secret-of-at-least-32-characters
```

4. Start the app:

```
./mvnw spring-boot:run
```

5. Open:
    - Store: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui.html

## Main endpoints

| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register | public |
| POST | /api/auth/login | public |
| GET | /api/products (supports `q`, `sort`, `page`, `size`) | public |
| GET | /api/products/{id} | public |
| POST, PUT, DELETE | /api/products | admin only |
| POST | /api/orders | logged-in user |
| GET | /api/orders | logged-in user (own orders) |

## Make a user an admin

New users are always `CUSTOMER`. To create an admin, run this in MySQL:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```

## Possible improvements

Product categories, an admin dashboard, payments, integration tests, and deployment.