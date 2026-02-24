# ⚡ Quick Start Guide

Get the application running in 5 minutes!

---

## 📋 Prerequisites Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] MySQL 8.0+ running
- [ ] IDE or text editor

---

## 🚀 Setup Steps

### 1️⃣ Create Database
```sql
mysql -u root -p
CREATE DATABASE employee_db;
exit;
```

### 2️⃣ Configure Database Connection
Edit: `src/main/resources/application.properties`

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3️⃣ Build & Run
```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

### 4️⃣ Access Application
Open browser: **http://localhost:8080**

---

## 🎯 First Steps

### Step 1: Create Departments
1. Navigate to **Departments** page
2. Add departments:
   - IT Department
   - HR Department
   - Finance

### Step 2: Create Employees
1. Navigate to **Employees** page
2. Notice dropdown is populated!
3. Select department from dropdown
4. Enter employee name
5. Save

### Step 3: Test Features
- ✅ Edit a department
- ✅ Edit an employee
- ✅ Try creating duplicate department (should fail)
- ✅ Delete an employee
- ✅ Delete a department (employees cascade delete)

---

## 🐛 Quick Troubleshooting

### Port 8080 in use?
```properties
# In application.properties, add:
server.port=8081
```

### MySQL connection failed?
- Check MySQL is running: `sudo service mysql status`
- Verify credentials in `application.properties`
- Test connection: `mysql -u root -p`

### Tables not created?
- Check: `spring.jpa.hibernate.ddl-auto=update`
- Or run SQL manually: `mysql -u root -p employee_db < database-schema.sql`

---

## 📁 Project URLs

| Page | URL |
|------|-----|
| Home | http://localhost:8080/ |
| Departments | http://localhost:8080/departments |
| Employees | http://localhost:8080/employees |

---

## 🔍 Verify Database

```sql
-- Check tables were created
USE employee_db;
SHOW TABLES;

-- View departments
SELECT * FROM department;

-- View employees with departments
SELECT e.id, e.name as employee, d.name as department 
FROM employee e 
JOIN department d ON e.department_id = d.id;
```

---

## 📚 Next Steps

1. ✅ Read **README.md** for complete documentation
2. ✅ Read **DROPDOWN_EXPLANATION.md** to understand how dropdowns work
3. ✅ Explore the code structure
4. ✅ Modify and experiment!

---

**You're all set! Happy coding! 🎉**
