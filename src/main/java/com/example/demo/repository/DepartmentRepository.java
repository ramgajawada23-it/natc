package com.example.demo.repository;

import com.example.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Department entity
 * Extends JpaRepository for CRUD operations
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find department by name (case-insensitive)
     * Used for duplicate checking
     */
    @Query("SELECT d FROM Department d WHERE LOWER(d.name) = LOWER(:name)")
    Optional<Department> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Check if department exists by name (case-insensitive)
     * Used for duplicate check during creation
     */
    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE LOWER(d.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    /**
     * Check if department exists by name excluding specific id
     * Useful for update operations
     */
    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE LOWER(d.name) = LOWER(:name) AND d.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
}
