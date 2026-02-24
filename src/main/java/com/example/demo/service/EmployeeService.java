package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.entity.Department;
import com.example.demo.entity.Employee;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Employee business logic
 * Manages employee CRUD operations and department relationships
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    /**
     * Create a new employee
     * Validates department exists before creating
     */
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee: {} in department ID: {}", 
            employeeDTO.getName(), employeeDTO.getDepartmentId());

        // Fetch department (throws exception if not found)
        Department department = departmentService.getDepartmentEntityById(
            employeeDTO.getDepartmentId()
        );

        // Create employee entity
        Employee employee = Employee.builder()
                .name(employeeDTO.getName().trim())
                .department(department)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {}", savedEmployee.getId());

        return convertToDTO(savedEmployee);
    }

    /**
     * Get all employees with department information
     */
    public List<EmployeeDTO> getAllEmployees() {
        log.debug("Fetching all employees");
        
        List<Employee> employees = employeeRepository.findAllWithDepartment();
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     */
    public EmployeeDTO getEmployeeById(Long id) {
        log.debug("Fetching employee by ID: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found with ID: " + id
                ));
        
        return convertToDTO(employee);
    }

    /**
     * Get all employees in a specific department
     */
    public List<EmployeeDTO> getEmployeesByDepartmentId(Long departmentId) {
        log.debug("Fetching employees for department ID: {}", departmentId);
        
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update existing employee
     */
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("Updating employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found with ID: " + id
                ));

        // Fetch new department if changed
        if (!employee.getDepartment().getId().equals(employeeDTO.getDepartmentId())) {
            Department newDepartment = departmentService.getDepartmentEntityById(
                employeeDTO.getDepartmentId()
            );
            employee.setDepartment(newDepartment);
        }

        employee.setName(employeeDTO.getName().trim());
        Employee updatedEmployee = employeeRepository.save(employee);
        
        log.info("Employee updated successfully: {}", updatedEmployee.getId());
        return convertToDTO(updatedEmployee);
    }

    /**
     * Delete employee by ID
     */
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee ID: {}", id);

        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }

        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully: {}", id);
    }

    /**
     * Convert Entity to DTO
     */
    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getName())
                .build();
    }
}
