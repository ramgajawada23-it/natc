package com.example.demo.controller;

import com.example.demo.dto.DepartmentDTO;
import com.example.demo.dto.EmployeeDTO;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for Employee Screen
 * Handles employee CRUD operations and department dropdown binding
 */
@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    /**
     * Show employee list page with department dropdown
     * This demonstrates dynamic dropdown population
     */
    @GetMapping
    public String showEmployeePage(Model model) {
        log.debug("Displaying employee page");

        // Add empty DTO for the form
        if (!model.containsAttribute("employeeDTO")) {
            model.addAttribute("employeeDTO", new EmployeeDTO());
        }

        // Load all employees
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);

        // *** KEY PART: Load departments for dropdown ***
        // This is how dropdown binding works in Spring MVC
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("isEdit", false);

        return "employee/list";
    }

    /**
     * Handle employee creation
     * Department is automatically bound from dropdown selection
     */
    @PostMapping("/save")
    public String saveEmployee(
            @Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Attempting to save employee: {} in department ID: {}",
                employeeDTO.getName(), employeeDTO.getDepartmentId());

        // Validation check
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors found: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.employeeDTO",
                    bindingResult);
            redirectAttributes.addFlashAttribute("employeeDTO", employeeDTO);
            return "redirect:/employees";
        }

        try {
            employeeService.createEmployee(employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Employee '" + employeeDTO.getName() + "' created successfully!");
        } catch (Exception e) {
            log.error("Error creating employee", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees";
    }

    /**
     * Show edit form for an employee
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.debug("Showing edit form for employee ID: {}", id);

        EmployeeDTO employee = employeeService.getEmployeeById(id);
        model.addAttribute("employeeDTO", employee);
        model.addAttribute("isEdit", true);

        // Load all employees and departments
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);

        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);

        return "employee/list";
    }

    /**
     * Update existing employee
     */
    @PostMapping("/update/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employeeDTO") EmployeeDTO employeeDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Updating employee ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.employeeDTO",
                    bindingResult);
            redirectAttributes.addFlashAttribute("employeeDTO", employeeDTO);
            return "redirect:/employees/edit/" + id;
        }

        try {
            employeeService.updateEmployee(id, employeeDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Employee updated successfully!");
        } catch (Exception e) {
            log.error("Error updating employee", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees";
    }

    /**
     * Delete employee
     */
    @PostMapping("/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("Deleting employee ID: {}", id);

        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Employee deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting employee", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employees";
    }
}
