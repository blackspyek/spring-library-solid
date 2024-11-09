# Library Management System

This is a simple Library Management System built using **Spring Framework**. The system allows for the management of books, users, and transactions like issuing and returning books. The architecture follows **SOLID principles** to ensure scalability, maintainability, and testability.

## Features

- **Book Management**: Add, update, delete, and view books.
- **User Management**: Register, update, and view library users.
- **Transaction Management**: Issue and return books, track due dates, and fines.

## Architecture

The system follows **Clean Architecture** and **SOLID principles** for code organization, ensuring that the code is modular, testable, and easily maintainable.

### SOLID Principles Applied:

1. **Single Responsibility Principle (SRP)**: 
   - Each class has a single responsibility (e.g., BookService is only responsible for book-related operations).
   
2. **Open/Closed Principle (OCP)**:
   - The system is open for extension but closed for modification. You can easily add new features (like new types of transactions) without modifying existing code.

3. **Liskov Substitution Principle (LSP)**:
   - Derived classes can be substituted for their base classes without affecting the behavior of the system.

4. **Interface Segregation Principle (ISP)**:
   - We split large interfaces into smaller, more specific ones (e.g., `BookRepository` vs. `TransactionRepository`) so that clients only implement what they need.

5. **Dependency Inversion Principle (DIP)**:
   - High-level modules (e.g., `LibraryService`) depend on abstractions (interfaces), not concrete implementations (e.g., `BookRepository`).

## Technology Stack

- **Java 23**
- **Spring Boot** - Core framework for building RESTful APIs and managing the application lifecycle.
- **Spring Data JPA** - Simplifies database interactions using repositories.
- **MySQL Database** (for demo purposes, can be replaced with any RDBMS).
- **JUnit** - For unit testing and integration testing.
- **Maven** - Build and dependency management.


