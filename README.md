# ChâTop Backend Application

ChâTop is a backend API developed for a rentalEntity portal that connects potential tenants with property owners. The API is built with Spring Boot and secured with JWT authentication.

## Table of Contents

1. [Features](#features)
2. [Technologies](#technologies)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Running the Application](#running-the-application)
6. [Routes](#routes)

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

### 3. Environment Configuration

The application requires an ```.env``` file to manage environment variables. Below is a detailed explanation of the variables you need to configure:

#### Application Configuration

* **APP_PORT** (optional): The port on which the application will run. Default: ```3001```.

#### Database Configuration

* **DB_USERNAME** (required): The username to conntect to the MySQL database.
* **DB_PASSWORD** (required): The password associated with the database user.
* **DB_NAME** (required): The name of the MySQL database.
* **DB_PORT** (optional): The port used by the MySQL database. Default: ```3306```.

#### Security Configuration

* **ENCRYPTION_KEY** (required): A Base64-encoded 256-bit key used for cryptographic operations for JWT signing and verification.

#### File Upload Configuration

* **UPLOAD_DIR** (optional): The directory where uploaded files will be stored. Default: ```uploads/rentals```.

#### Setting Up the ```.env``` File

```bash
   cp .env.example .env
```

Edit the ``` .env ``` file and set your own values for :
* Database connection details (```DB_USERNAME```, ```DB_PASSWORD```, ```DB_NAME```).
* Security Key (```ENCRYPTION_KEY```). Use the provided key generator to create a secure encryption key : [https://randomgenerate.io/encryption-key-generator](https://randomgenerate.io/encryption-key-generator).

For testing purposes, you can use the following key in your ```.env``` file : 
```txt
ENCRYPTION_KEY=cce37e45add6e72167eddddc7891504bdcb6a12ce4c9ea4f77160395f4a4ff1f
```


## Running the application

### 1. Build the Project

Navigate to the project directory and run the following command to compile the application and download all required dependencies:
```bash
  mvn clean install
```

### 2. Start the Application

```bash
  mvn spring-boot:run
```

### 3. Verify the Application

Once the application is running, you can access it at:

* **Base URL:** http://localhost:3001 (or the port configured in ```APP_PORT```).
* **Swagger UI:** http://localhost:3001/api/swagger-ui (for interactive API documentation).

## Routes

Below is a complete list of the API routes available in the application. For detailed information on how to use them, refer to the ***Swagger UI*** or test them using ***Postman***.

### Authentication Routes
| HTTP Method | Endpoint             | Description                             | Authentication Required |
|-------------|----------------------|-----------------------------------------|--------------------------|
| POST        | `/api/auth/register` | Register a new user                     | No                       |
| POST        | `/api/auth/login`    | Login and obtain a JWT                  | No                       |
| GET         | `/api/auth/me`       | Retrieve authenticated user information | Yes                     |

### Users
| HTTP Method | Endpoint           | Description           | Authentication Required |
|-------------|--------------------|-----------------------|-------------------------|
| GET         | `/api/user/{id}`   | Get a specific user   | Yes                     |

### Rental Property Routes
| HTTP Method | Endpoint            | Description                      | Authentication Required |
|-------------|---------------------|----------------------------------|--------------------------|
| GET         | `/api/rentals`      | Get all rentals                  | Yes                     |
| POST        | `/api/rentals`      | Create a new rental              | Yes                     |
| GET         | `/api/rentals/{id}` | Get details of a specific rental | Yes                     |
| PUT         | `/api/rentals/{id}` | Update a rental                  | Yes                     |

### Messaging Routes
| HTTP Method | Endpoint        | Description          | Authentication Required |
|-------------|-----------------|----------------------|--------------------------|
| POST        | `/api/messages` | Create a new message | Yes                     |

### Notes

- **Authentication Required**: Routes marked as "Yes" require a valid JWT token in the `Authorization` header.
- The full list of query parameters, request bodies, and response formats can be explored using the **Swagger UI**.