# ChâTop Backend Application

ChâTop is a backend API developed for a rentalEntity portal that connects potential tenants with property owners. The API is built with Spring Boot and secured with JWT authentication.

## Table of Contents

1. [Features](#features)
2. [Technologies](#technologies)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Running the Application](#running-the-application)
6. [API Documentation](#api-documentation)
7. [Routes](#routes)

---

## Features

- **Secure Authentication**: Implements JWT for login and registration security.
- **Rental Listings Management**: Create, view, and update rental properties.
- **Messaging**: Send messages.
- **Image Upload**: Supports image uploads for rental properties.
- **Security**: Passwords are hashed using BCrypt.
- **API Documentation**: Interactive Swagger UI for testing API endpoints.

---

## Technologies

- **Java 17**
- **Spring Boot (3.3.5)**
- **MySQL**
- **JWT Authentication**
- **Swagger for API documentation**

---

## Prerequisites

1. **Java**: Ensure you have Java 17 installed.
2. **MySQL**: Install MySQL (version 8.0 or higher is recommended).

---

## Installation

### 1. MySQL database

To set up the MySQL database required for this application, you need to:
1. Install MySQL on your system.
2. Create a database, user and password for this application.

For detailed instructions on how to configure MySQL, refer to the [MySQL Setup Guide](MYSQLREADME.md)

### 2. Clone the Repository

First, clone the project repository from GitHub to your local machine:

```bash 
   git clone https://github.com/MatthieuSKRZYPCZAK/P03_Chatop.git
```

Navigate to the project directory:

```bash
   cd P03_Chatop
```
