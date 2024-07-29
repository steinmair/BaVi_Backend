# Band View Backend

## Project Overview

This project is a scalable backend application designed to manage music files and their associated metadata. The backend leverages modern technologies to ensure security, efficiency, and ease of use. Key functionalities include user authentication and authorization, CRUD operations for music files, and comprehensive API documentation. The application is containerized for seamless deployment and includes data backup and restoration capabilities.

## Key Features

- **User Authentication and Authorization**: Uses JWT tokens and Spring Boot Web Security to secure user login and authorize access to resources.
- **RESTful API**: Provides endpoints for creating, updating, and deleting songs, all documented with Swagger for easy integration and testing.
- **S3-Compatible Storage**: Utilizes MinIO for storing music files, offering endpoints to manage buckets and file operations.
- **Database Management**: Employs MySQL for reliable and efficient data storage.
- **Reverse Proxy and Load Balancing**: Implements Traefik to handle routing and load balancing.
- **Containerization**: Uses Docker and Docker-Compose to containerize the application, ensuring consistent environments across different stages of development and deployment.
- **Data Backup**: Includes PowerShell scripts for backing up and restoring data, safeguarding against data loss.

This backend system is built with scalability and maintainability in mind, making it an ideal solution for managing large volumes of music files and metadata.


