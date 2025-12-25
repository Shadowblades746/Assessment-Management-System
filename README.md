# Assessment Management System - COM2008 Team Project (Team 27)

## Overview
**Assessment Management System** is a web application designed to streamline the management of academic assessments, modules, and user roles within a university setting. The platform provides comprehensive tools for different user types to manage assessments, track feedback, and maintain academic records.

### Key Features
- **Role-Based Access Control**: Distinct interfaces and permissions for Teaching Support, Academic Staff, Exams Officers, and External Examiners.
- **Assessment Management**: Create, edit, and view assessments with detailed information including marks, feedback, and submission tracking.
- **Module Administration**: Comprehensive module management system allowing staff to organize courses, track student progress, and manage assessment portfolios.
- **User Management**: Add and manage staff members across different roles with appropriate access levels and permissions.
- **Dashboard Views**: Customized dashboards for each user role, displaying relevant assessments, modules, and notifications.
- **Secure Authentication**: JWT-based authentication with RSA encryption ensuring secure access to the platform.

The platform simplifies academic administration by providing intuitive interfaces for managing assessments, modules, and user interactions while maintaining data integrity and security.

## Requirements
- **JDK Version**: 17.0.17 or higher
- **Node.js**: Required for frontend development
- **Database**: H2 in-memory database (configured automatically)

## Test Users

**Teaching Support**
- Email: `teachingsupport1@sheffield.ac.uk`
- Password: `password`

**Academic Staff**
- Email: `academic1@sheffield.ac.uk`
- Password: `password`

**Exams Officer**
- Email: `examsofficer1@sheffield.ac.uk`
- Password: `password`

**External Examiner**
- Email: `externalexaminer1@sheffield.ac.uk`
- Password: `password`

*A full list of seeded data can be found in `Com2008Team27Application.commandLineRunner`*

## Setup & Installation

### Database Configuration
- **Type**: H2 in-memory database
- **JDBC URL**: `jdbc:h2:mem:postsdb`
- **Driver**: `org.h2.driver`
- **Username**: `sa`
- **Password**: *(empty)*
- **H2 Console URL**: http://localhost:8080/h2-console

*Note: Initial data is automatically seeded by `CommandLineRunner`*

### Public-Private Key Setup
The project requires an RSA key-pair for JWT encoding/decoding. Generate the keys using the following steps:

1. Open a terminal (Mac/Linux/WSL/Git Bash)
2. Navigate to `backend/src/main/resources`
3. Run the following commands:

```bash
mkdir certs
cd certs
openssl genrsa -out keypair.pem 2048
openssl rsa -in keypair.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
rm keypair.pem
```

### Running the Application

**Backend (Spring Boot)**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend (React + Vite)**
```bash
cd frontend/frontend
npm install
npm run dev
```
Then press `o` to open in browser.

**Access the application at**: http://localhost:52383/

## Project Structure
- `/backend` - Spring Boot server code with REST API endpoints
- `/frontend` - Visual Studio solution containing the client application
- `/frontend/frontend` - React frontend root with Node.js/Vite configuration

## My Contributions

As part of Team 27, I contributed to the backend infrastructure and testing:

### Backend Development
- Implemented **UserRepository** with advanced query methods including case-insensitive search for users by email, forename, and surname, plus existence checks to prevent duplicates.
- Created **Staff domain class** and refactored it into the **User domain class**, streamlining the user model architecture.
- Refactored **UserDTO** as part of the Staff integration, removing redundant code and resolving username/email field discrepancies across the codebase.
- Implemented **user retrieval methods** in UserService and fixed bean configurations for compatibility with updated domain models.

### Testing & Quality Assurance
- Wrote **all unit tests** for domain classes and DTOs, ensuring robust validation and complete test coverage.
- Debugged problematic code and implemented additional test cases to improve overall code quality and build stability.

This repository demonstrates my contributions in **backend development**, showcasing skills in **Java, Spring Boot, Spring Data JPA, unit testing with JUnit, and domain-driven design**.
