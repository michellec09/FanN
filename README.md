<h1 align="center"> FAMILY LOST API </h1>

<p align="center">
  <em>Simplified <code>README</code> file, powered by ISTA TEAM</em>
</p>

[![Java Version](https://img.shields.io/badge/Java-17-green.svg)](https://docs.oracle.com/en/java/javase/11/)
[![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Security Version](https://img.shields.io/badge/Spring%20Security-green.svg)](https://spring.io/projects/spring-security)
[![JWT Version](https://img.shields.io/badge/JWT-0.11.5-green.svg)](https://github.com/jwtk/jjwt)
[![Swagger Version](https://img.shields.io/badge/Swagger-3.0.0-green.svg)](https://swagger.io/)
[![Lombok Version](https://img.shields.io/badge/Lombok-1.18.22-green.svg)](https://projectlombok.org/)
[![Maven Version](https://img.shields.io/badge/Maven-3.8.4-green.svg)](https://maven.apache.org/)
[![Docker Version](https://img.shields.io/badge/Docker-20.10.8-blue.svg)](https://www.docker.com/)
[![MongoDB Version](https://img.shields.io/badge/MongoDB-5.0.3-green.svg)](https://www.mongodb.com/)
[![Cloudinary Version](https://img.shields.io/badge/Cloudinary-1.0.0-green.svg)](https://cloudinary.com/)
[![Google API Email Version](https://img.shields.io/badge/Google%20API%20Email-1.0.0-green.svg)](https://developers.google.com/gmail/api)
[![WebSockets Version](https://img.shields.io/badge/WebSockets-1.0.0-green.svg)](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets)
[![Stomps Version](https://img.shields.io/badge/Stomps-1.0.0-green.svg)](https://stomp.github.io/)

This project is a simple API for a family lost application. The main goal is to provide a simple and easy-to-use API for the application.
Its main features are:
- User authentication
- User registration
- User password recovery
- User profile update
- User profile photo upload
- User can create a lost post
- User can create a found post
- User can search for lost or found posts
- User receives a notification when a post is created

## Getting Started
You can run this project in your local machine by following the steps below.
1. Clone the repository
    ```bash
   git clone "https://github.com/LeninCrdva/family-lost-api.git"
    ```
2. Install the dependencies
   ```bash
   mvn install
   ```
3. Run the project
    ```bash
   mvn spring-boot:run
   ```
Remember to have the following tools installed:
- [Java 17](https://docs.oracle.com/en/java/javase/11/)
- [Maven 3.8.4](https://maven.apache.org/)
- [Docker 20.10.8](https://www.docker.com/)
- [MongoDB 5.0.3](https://www.mongodb.com/)

And the following accounts:
- [Cloudinary](https://cloudinary.com/)

The project is running on port 8080, but the port can be changed in the `application.properties` file with the property `server.port`.
The base URL for the API is `http://localhost:8080/api/v1`.
## Table of Contents
When the project is running, you can access the API documentation by accessing the following URL:
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html)

If you want to see Online Documentation, you can access the following URL:
- [API Documentation](https://family-lost-api-production.up.railway.app/api/v1/swagger-ui/index.html)