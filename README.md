# ShopSphere

A small e-commerce REST API with a simple web front end, built with Java 21, Spring Boot 4, Spring Security (JWT) and MySQL.

## Features

- Register and log in with a hashed password (BCrypt) and a JWT token
- Role-based access: `CUSTOMER` and `ADMIN`
- Products: public list with pagination; admins can add, edit and delete
- Orders: customers place orders, stock is checked and reduced in one transaction (all or nothing), and order history is kept per customer
- The price is saved on each order line, so later price changes do not alter old orders
- Input validation and clear JSON error messages
- Swagger UI for API documentation
- A single-page front end (HTML, CSS and JavaScript) served by the same application

## Tech stack

Java 21, Spring Boot 4, Spring Security, Spring Data JPA (Hibernate), MySQL, JWT (jjwt), Lombok, springdoc-openapi (Swagger), Maven

## Project structure

```
controller   REST endpoints
service      business logic
repository   database access
entity       database tables
dto          request and response objects
security     JWT and security configuration
exception    global error handler
config       Swagger configuration
```

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
    - Web page: http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui.html

## Main endpoints

| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register | public |
| POST | /api/auth/login | public |
| GET | /api/products | public |
| GET | /api/products/{id} | public |
| POST, PUT, DELETE | /api/products | admin only |
| POST | /api/orders | logged-in user |
| GET | /api/orders | logged-in user (own orders) |

## Make a user an admin

New users are always `CUSTOMER`. To create an admin, run this in MySQL:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```