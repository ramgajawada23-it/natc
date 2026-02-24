package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class
 * Entry point for the Spring Boot application
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class DepartmentEmployeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepartmentEmployeeApplication.class, args);
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════════╗\n" +
            "║                                                            ║\n" +
            "║   Department-Employee Management System Started!           ║\n" +
            "║                                                            ║\n" +
            "║   Application is running at:                               ║\n" +
            "║   http://localhost:8080                                    ║\n" +
            "║                                                            ║\n" +
            "║   Endpoints:                                               ║\n" +
            "║   • Home:        http://localhost:8080/                    ║\n" +
            "║   • Departments: http://localhost:8080/departments         ║\n" +
            "║   • Employees:   http://localhost:8080/employees           ║\n" +
            "║                                                            ║\n" +
            "╚════════════════════════════════════════════════════════════╝\n"
        );
    }
}
