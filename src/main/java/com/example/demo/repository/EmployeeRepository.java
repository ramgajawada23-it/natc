package com.example.demo.repository;

import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Employee entity
 * Provides CRUD operations and custom queries
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find all employees by department id
     * Fetch department eagerly to avoid N+1 query problem
     */
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.department.id = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Find all employees with department information
     * Optimized query to avoid lazy loading issues
     */
    @Query("SELECT e FROM Employee e JOIN FETCH e.department ORDER BY e.name")
    List<Employee> findAllWithDepartment();

    /**
     * Count employees in a specific department
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Long countByDepartmentId(@Param("departmentId") Long departmentId);
}
