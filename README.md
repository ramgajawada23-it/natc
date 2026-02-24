# 🏢 Department-Employee Management System

A professional **Master-Detail** enterprise application built with **Spring Boot 3.2**, **JPA/Hibernate**, and **Thymeleaf** demonstrating clean architecture and best practices.

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Database Design](#-database-design)
- [Setup Instructions](#-setup-instructions)
- [How It Works](#-how-it-works)
- [API Endpoints](#-api-endpoints)
- [Project Structure](#-project-structure)
- [Key Concepts](#-key-concepts)

---

## ✨ Features

### Department Master Module
- ✅ Create new departments
- ✅ View all departments with employee count
- ✅ Update existing departments
- ✅ Delete departments (cascades to employees)
- ✅ Duplicate validation (case-insensitive)
- ✅ Input validation with error messages

### Employee Module
- ✅ Create employees with department assignment
- ✅ **Dynamic dropdown** populated from database
- ✅ View all employees with department info
- ✅ Update employee details and department
- ✅ Delete employees
- ✅ Proper foreign key relationships

### Technical Features
- ✅ **Layered Architecture** (Controller → Service → Repository)
- ✅ **DTO Pattern** for separation of concerns
- ✅ **Global Exception Handling**
- ✅ **Bean Validation** (JSR-303)
- ✅ **Optimized Queries** (JOIN FETCH to avoid N+1)
- ✅ **Transaction Management**
- ✅ **Audit Fields** (createdAt, updatedAt)
- ✅ **Professional UI** with Bootstrap 5

---

## 🛠 Technology Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.2.0 |
| **ORM** | JPA/Hibernate |
| **Database** | MySQL 8.0+ |
| **Frontend** | Thymeleaf + Bootstrap 5 |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Maven |
| **Java Version** | 17+ |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                │
│  (Thymeleaf Templates + Controllers)        │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│            Controller Layer                 │
│  - DepartmentController                     │
│  - EmployeeController                       │
│  - Exception handling with                  │
│    @ControllerAdvice                        │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│             Service Layer                   │
│  - DepartmentService (Business Logic)       │
│  - EmployeeService (Business Logic)         │
│  - Transaction management                   │
│  - DTO conversions                          │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│          Repository Layer                   │
│  - DepartmentRepository (JpaRepository)     │
│  - EmployeeRepository (JpaRepository)       │
│  - Custom queries with @Query               │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│            Database Layer                   │
│  MySQL Database                             │
│  - department table                         │
│  - employee table                           │
│  - Foreign key relationships                │
└─────────────────────────────────────────────┘
```

---

## 🗄 Database Design

### Department Table
```sql
CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Employee Table
```sql
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES department(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

### Relationship
- **One Department → Many Employees** (One-to-Many)
- Foreign key: `department_id` in employee table
- Cascade delete: Deleting department removes all its employees

---

## 🚀 Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA / Eclipse / VS Code)

### Step 1: Clone/Download Project
```bash
cd department-employee-app
```

### Step 2: Configure Database
1. Create MySQL database:
```sql
CREATE DATABASE employee_db;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Run Database Schema (Optional)
```bash
mysql -u root -p employee_db < database-schema.sql
```
*Note: Spring Boot will auto-create tables if `spring.jpa.hibernate.ddl-auto=update`*

### Step 4: Build Project
```bash
mvn clean install
```

### Step 5: Run Application
```bash
mvn spring-boot:run
```

Or run the main class:
```bash
java -jar target/department-employee-management-1.0.0.jar
```

### Step 6: Access Application
Open browser and navigate to:
- **Home**: http://localhost:8080/
- **Departments**: http://localhost:8080/departments
- **Employees**: http://localhost:8080/employees

---

## 🔍 How It Works

### 1️⃣ Department Master Screen

**Controller Action:**
```java
@GetMapping("/departments")
public String showDepartmentPage(Model model) {
    model.addAttribute("departmentDTO", new DepartmentDTO());
    List<DepartmentDTO> departments = departmentService.getAllDepartments();
    model.addAttribute("departments", departments);
    return "department/list";
}
```

**What Happens:**
1. Controller loads empty DTO for form
2. Service fetches all departments from database
3. Data sent to Thymeleaf template
4. User fills form and clicks Save
5. POST request → Controller → Service → Repository → Database

**Validation:**
- Department name cannot be empty
- Name must be unique (case-insensitive)
- Minimum 2 characters, maximum 100

---

### 2️⃣ Employee Screen with Dropdown Binding

**🔥 This is the KEY part - How dropdown works:**

**Controller Action:**
```java
@GetMapping("/employees")
public String showEmployeePage(Model model) {
    model.addAttribute("employeeDTO", new EmployeeDTO());
    
    // *** Load departments for dropdown ***
    List<DepartmentDTO> departments = departmentService.getAllDepartments();
    model.addAttribute("departments", departments);
    
    List<EmployeeDTO> employees = employeeService.getAllEmployees();
    model.addAttribute("employees", employees);
    
    return "employee/list";
}
```

**Thymeleaf Template (Dropdown):**
```html
<select th:field="*{departmentId}">
    <option value="">-- Select Department --</option>
    <option th:each="dept : ${departments}"
            th:value="${dept.id}"
            th:text="${dept.name}">
    </option>
</select>
```

**Step-by-Step Flow:**
1. **Controller** calls `departmentService.getAllDepartments()`
2. **Service** fetches departments from database
3. **Model** receives `List<DepartmentDTO>`
4. **Thymeleaf** loops through departments using `th:each`
5. Each `<option>` has:
   - `value` = department ID (saved to database)
   - `text` = department name (shown to user)
6. User selects department → Form submits
7. **Spring MVC** automatically binds selected ID to `employeeDTO.departmentId`
8. **Service** uses ID to fetch Department entity
9. **Repository** saves Employee with foreign key

**This is called Object Binding in Spring MVC!**

---

## 📡 API Endpoints

### Department Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/departments` | Show department list page |
| POST | `/departments/save` | Create new department |
| GET | `/departments/edit/{id}` | Show edit form |
| POST | `/departments/update/{id}` | Update department |
| POST | `/departments/delete/{id}` | Delete department |

### Employee Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/employees` | Show employee list page |
| POST | `/employees/save` | Create new employee |
| GET | `/employees/edit/{id}` | Show edit form |
| POST | `/employees/update/{id}` | Update employee |
| POST | `/employees/delete/{id}` | Delete employee |

---

## 📁 Project Structure

```
department-employee-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── controller/
│   │   │   │   ├── DepartmentController.java
│   │   │   │   ├── EmployeeController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── service/
│   │   │   │   ├── DepartmentService.java
│   │   │   │   └── EmployeeService.java
│   │   │   ├── repository/
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   └── EmployeeRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── Department.java
│   │   │   │   └── Employee.java
│   │   │   ├── dto/
│   │   │   │   ├── DepartmentDTO.java
│   │   │   │   └── EmployeeDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── DuplicateDepartmentException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── DepartmentEmployeeApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           ├── index.html
│   │           ├── department/
│   │           │   └── list.html
│   │           └── employee/
│   │               └── list.html
├── database-schema.sql
├── pom.xml
└── README.md
```

---

## 🎓 Key Concepts

### 1. DTO Pattern
**Why use DTOs instead of Entities directly?**
- ✅ Decouples presentation from domain model
- ✅ Controls what data is exposed to UI
- ✅ Prevents accidental lazy loading issues
- ✅ Adds computed fields (like employeeCount)

### 2. Service Layer
**Why separate business logic?**
- ✅ Keeps controllers thin
- ✅ Enables transaction management
- ✅ Makes code reusable and testable
- ✅ Centralizes validation and business rules

### 3. Repository Pattern
**What does JpaRepository provide?**
- ✅ Built-in CRUD operations
- ✅ Custom query methods with `@Query`
- ✅ Pagination and sorting support
- ✅ No need to write SQL for basic operations

### 4. Exception Handling
**How does @ControllerAdvice work?**
- ✅ Centralized error handling
- ✅ Converts exceptions to user-friendly messages
- ✅ Redirects with flash attributes
- ✅ Maintains clean code in controllers

### 5. N+1 Query Problem Solution
```java
@Query("SELECT e FROM Employee e JOIN FETCH e.department")
List<Employee> findAllWithDepartment();
```
- ✅ Fetches employees and departments in single query
- ✅ Avoids lazy loading issues
- ✅ Improves performance

---

## 🏆 Best Practices Implemented

1. ✅ **Layered Architecture** - Clear separation of concerns
2. ✅ **DTO Pattern** - Don't expose entities directly
3. ✅ **Bean Validation** - Server-side validation
4. ✅ **Exception Handling** - Global error management
5. ✅ **Transaction Management** - ACID compliance
6. ✅ **Audit Fields** - Track creation and updates
7. ✅ **Optimized Queries** - Prevent N+1 problems
8. ✅ **Cascade Operations** - Maintain referential integrity
9. ✅ **Case-Insensitive Checks** - Better user experience
10. ✅ **Professional UI** - Bootstrap 5 with animations

---

## 📝 Sample Data

Run these SQL commands to add sample data:
```sql
INSERT INTO department (name) VALUES ('IT'), ('HR'), ('Finance');

INSERT INTO employee (name, department_id) VALUES 
('John Doe', 1),
('Jane Smith', 1),
('Bob Johnson', 2);
```

---

## 🐛 Troubleshooting

### Issue: Database connection failed
**Solution:** Check MySQL is running and credentials in `application.properties`

### Issue: Tables not created
**Solution:** Ensure `spring.jpa.hibernate.ddl-auto=update` in properties

### Issue: Port 8080 already in use
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Dropdown shows no departments
**Solution:** Create departments first before adding employees

---

## 📚 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JPA/Hibernate Guide](https://hibernate.org/orm/documentation/)
- [Thymeleaf Tutorial](https://www.thymeleaf.org/documentation.html)
- [Bootstrap 5 Docs](https://getbootstrap.com/docs/5.3/)

---

## 👨‍💻 Author

Built with ❤️ as a learning project for professional Java/Spring Boot development.

---

## 📄 License

This project is open-source and available for learning purposes.

---

**Happy Coding! 🚀**
