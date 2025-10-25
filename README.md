# Hotel Booking Assessment - Spring Boot (Gradle)

## Overview
A simple hotel booking service implementing the assessment requirements:
- Users: CUSTOMER, HOTEL_MANAGER, ADMIN
- JWT based stateless authentication
- MySQL ready (defaults to H2 for development/tests)
- Endpoints:
  - POST /users/register
  - POST /users/login
  - GET /hotels
  - POST /hotels (ADMIN)
  - PUT /hotels/{id} (HOTEL_MANAGER)
  - DELETE /hotels/{id} (ADMIN)
  - POST /bookings/{hotelId} (CUSTOMER)
  - GET /bookings/{bookingId} (AUTH)
  - DELETE /bookings/{bookingId} (HOTEL_MANAGER)

## Run locally (with H2 in-memory)
./gradlew bootRun

## To use MySQL
Update `src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/bookingdb
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

## Tests
./gradlew test

## Notes
- Passwords are stored with BCrypt
- JWT secret is in application.properties (change for prod)
- This project aims to satisfy the provided assessment script
