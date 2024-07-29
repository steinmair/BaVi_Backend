# Band View Backend

## Project Overview

This project is a robust and scalable backend application designed to manage music files and their associated metadata. The backend leverages modern technologies to ensure security, efficiency, and ease of use. Key functionalities include user authentication and authorization, CRUD operations for music files, and comprehensive API documentation. The application is containerized for seamless deployment and includes data backup and restoration capabilities.

## Key Features

- **User Authentication and Authorization**: Uses JWT tokens and Spring Boot Web Security to secure user login and authorize access to resources.
- **RESTful API**: Provides endpoints for creating, updating, and deleting songs, all documented with Swagger for easy integration and testing.
- **S3-Compatible Storage**: Utilizes MinIO for storing music files, offering endpoints to manage buckets and file operations.
- **Database Management**: Employs MySQL for reliable and efficient data storage.
- **Reverse Proxy and Load Balancing**: Implements Traefik to handle routing and load balancing.
- **Containerization**: Uses Docker and Docker-Compose to containerize the application, ensuring consistent environments across different stages of development and deployment.
- **Data Backup**: Includes PowerShell scripts for backing up and restoring data, safeguarding against data loss.

This backend system is built with scalability and maintainability in mind, making it an ideal solution for managing large volumes of music files and metadata.


## Technologies Used

### JWT Token
JWT (JSON Web Token) is used for authentication and authorization.

### Spring Boot
Spring Boot serves as the basis for the backend and enables the rapid development of Java applications.

### Swagger
Swagger is used for documenting the RESTful APIs.

### MinIO
MinIO is used as an S3-compatible storage for music files.

### MySQL
MySQL is used as a relational database.

### Traefik
Traefik acts as a reverse proxy and load balancer.

### Docker
Docker is used for containerizing the application.

## Authentication and Authorization with Spring Boot Web Security and JWT Token

### Login
Implementation of a secure login mechanism.

### Generating the JWT Token
JWT tokens are generated upon successful login.

### Reading the JWT Token
JWT tokens are used for authorization in requests.

## Spring Boot RESTful API

### Creating a Song
Endpoint for adding new songs.

### Updating a Song
Endpoint for updating existing songs.

### Deleting a Song
Endpoint for deleting songs.

## Swagger Documentation

### Annotations
Using Swagger annotations for API documentation.

## MinIO S3 Storage

### MinIO Controller
Controller for managing MinIO.

### Creating the MinIO Service
Setup of the MinIO service.

### Creating a Bucket
Endpoint for creating buckets.

### Deleting a Bucket
Endpoint for deleting buckets.

### Uploading Files
Endpoint for uploading files.

### Downloading Files
Endpoint for downloading files.

### Deleting Files
Endpoint for deleting files.

### Listing Files
Endpoint for listing files.

## MySQL Database
Using a MySQL database to store application data.

## Docker

### Docker-Compose
Using Docker-Compose for container orchestration.

#### Traefik Service
Configuration of the Traefik service.

#### Bavi Service
Configuration of the main service for the project.

#### MinIO Service
Configuration of the MinIO service.

#### MySQL Server
Configuration of the MySQL server.

### Init.sql
SQL script for initializing the database.

## Backup
PowerShell script for backup and a script for restoration.




