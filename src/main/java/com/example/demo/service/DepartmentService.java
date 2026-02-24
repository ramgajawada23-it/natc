package com.example.demo.service;

import com.example.demo.dto.DepartmentDTO;
import com.example.demo.entity.Department;
import com.example.demo.exception.DuplicateDepartmentException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Department business logic
 * Handles validation, transformation, and database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Create a new department
     * Validates for duplicates before saving
     */
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating new department: {}", departmentDTO.getName());

        // Validate for duplicates (case-insensitive)
        if (departmentRepository.existsByNameIgnoreCase(departmentDTO.getName())) {
            log.error("Department already exists: {}", departmentDTO.getName());
            throw new DuplicateDepartmentException(
                "Department '" + departmentDTO.getName() + "' already exists"
            );
        }

        // Create and save entity
        Department department = Department.builder()
                .name(departmentDTO.getName().trim())
                .build();

        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with ID: {}", savedDepartment.getId());

        return convertToDTO(savedDepartment);
    }

    /**
     * Get all departments
     * Returns DTOs with employee count
     */
    public List<DepartmentDTO> getAllDepartments() {
        log.debug("Fetching all departments");
        
        List<Department> departments = departmentRepository.findAll();
        
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get department by ID
     */
    public DepartmentDTO getDepartmentById(Long id) {
        log.debug("Fetching department by ID: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found with ID: " + id
                ));
        
        return convertToDTO(department);
    }

    /**
     * Update existing department
     */
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        log.info("Updating department ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found with ID: " + id
                ));

        // Check for duplicate name (excluding current department)
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(departmentDTO.getName(), id)) {
            throw new DuplicateDepartmentException(
                "Department '" + departmentDTO.getName() + "' already exists"
            );
        }

        department.setName(departmentDTO.getName().trim());
        Department updatedDepartment = departmentRepository.save(department);
        
        log.info("Department updated successfully: {}", updatedDepartment.getId());
        return convertToDTO(updatedDepartment);
    }

    /**
     * Delete department by ID
     * Cascades to employees due to entity configuration
     */
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department ID: {}", id);

        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }

        departmentRepository.deleteById(id);
        log.info("Department deleted successfully: {}", id);
    }

    /**
     * Convert Entity to DTO
     * Includes employee count
     */
    private DepartmentDTO convertToDTO(Department department) {
        Long employeeCount = employeeRepository.countByDepartmentId(department.getId());
        
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .employeeCount(employeeCount.intValue())
                .build();
    }

    /**
     * Get Department entity by ID (for internal use)
     */
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found with ID: " + id
                ));
    }
}
