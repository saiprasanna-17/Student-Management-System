# Student Management System

## Project Overview
A Java-based web application developed to manage student records efficiently. The application allows users to add, view, search, update, and delete student information through a web interface.

## Technologies Used
- Java
- Java Servlets
- JDBC
- MySQL
- HTML
- CSS
- JavaScript
- Apache Tomcat 9
- Eclipse IDE

## Features
- Add new student records
- View all students
- Search students by ID
- Edit and update student details
- Delete student records
- Dashboard statistics
- Form validation
- Search by name and department
- Sort student records
- Pagination

## Project Structure
- `Student.java` – Student model
- `StudentDAO.java` – Database operations
- `DBConnection.java` – MySQL connection
- `StudentServlet.java` – Handles HTTP requests
- `index.html` – User interface
- `style.css` – Page styling
- `script.js` – Frontend functionality

## Database Setup

Create a MySQL database:

```sql
CREATE DATABASE studentdb;
USE studentdb;

CREATE TABLE student (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    department VARCHAR(50),
    marks DOUBLE
);
```

Configure your own MySQL username and password in `DBConnection.java`.

## How to Run
1. Install Java, Eclipse, MySQL, and Apache Tomcat 9.
2. Import the project into Eclipse.
3. Configure the MySQL Connector/J library.
4. Update database credentials in `DBConnection.java`.
5. Start MySQL and Tomcat.
6. Open the application in your browser.

## Author
Sai Prasanna
