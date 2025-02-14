meCash - Multicurrency Wallet Application

```
Overview
meCash is a financial application that enables users to send and receive money across different currencies. This project is a RESTful API built using Java and Spring Boot. It provides functionalities such as user registration, login, deposit, withdrawal, transfer, balance inquiry, and transaction history.
```
Features
```
User Management:

Create an account.

Log in to the account.

Verify user (authentication).

Wallet Operations:

Deposit money into the wallet.

Withdraw money from the wallet.

Transfer money to another user’s wallet.

Get the current account balance.

Transaction History:

View transaction history (deposits, withdrawals, transfers).

```
Technology Stack
```
Programming Language: Java

Framework: Spring Boot

ORM: JPA/Hibernate

Database: MySQL

Testing: JUnit, Mockito

Authentication: JWT (JSON Web Tokens)

Version Control: Git
```
Prerequisites
```
Before running the application, ensure you have the following installed:

Java Development Kit (JDK): Version 17 or higher.

Maven: For building the project.

MySQL: For the database.

Postman or any API testing tool: For testing the endpoints.
```
Setup Instructions
1. Clone the Repository

```
git clone https://github.com/dannyy2000/meCash.git
cd meCash
```

2. Configure the Database
Install MySQL and create a database named mecash.

Update the database configuration in src/main/resources/application.properties:

```
spring.datasource.url=jdbc:mysql://localhost:5432/mecash
spring.datasource.username=your-db-username
spring.datasource.password=your-db-password
spring.jpa.hibernate.ddl-auto=update
```
3. Build the Application
Run the following command to build the project:

```
mvn clean install
```

4. Run the Application
Start the application using:

```
mvn spring-boot:run
```
API Endpoints
1. User Management
Create Account:
```
Endpoint: POST /api/auth/createAccount
```

Log In:
```
Endpoint: POST /api/auth/signIn
```

Verify User:
```
Endpoint: GET /api/auth/verify
```

2. Wallet Operations
```
Endpoint: POST /api/wallet/deposit

Headers: Authorization: Bearer <token>
```

Withdraw Money:
```
Endpoint: POST /api/wallet/withdrawal

Headers: Authorization: Bearer <token>
```

Transfer Money:
```
Endpoint: POST /api/wallet/transfer

Headers: Authorization: Bearer <token>
```
Get Balance:
```
Endpoint: GET /api/wallet/balance

Headers: Authorization: Bearer <token>
```

3. Transaction History
Get Transaction History:

```
Endpoint: GET /api/wallet/transactionHistory

Headers: Authorization: Bearer <token>
```

Testing
Unit Tests
```
Run unit tests using: mvn test
```
Integration Tests
```
Run integration tests using: mvn verify
```
Security
```
Authentication: JWT-based authentication is used to secure endpoints.

Password Hashing: Passwords are hashed using bcrypt before storing in the database.
```

Contributing
```
Feel free to contribute to this project by opening issues or submitting pull requests.
```

License
```
This project is licensed under the MIT License. See the LICENSE file for details.

This README provides a comprehensive guide to setting up and using your multicurrency wallet application. Let me know if you need further assistance!
```



