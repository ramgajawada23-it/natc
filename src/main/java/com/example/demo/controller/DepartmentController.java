package com.example.demo.controller;

import com.example.demo.dto.DepartmentDTO;
import com.example.demo.service.DepartmentService;
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
 * Controller for Department Master Screen
 * Handles all department-related web requests
 */
@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Show department list page
     */
    @GetMapping
    public String showDepartmentPage(Model model) {
        log.debug("Displaying department page");

        // Add empty DTO for the form
        if (!model.containsAttribute("departmentDTO")) {
            model.addAttribute("departmentDTO", new DepartmentDTO());
        }

        // Load all departments
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("isEdit", false);

        return "department/list";
    }

    /**
     * Handle department creation
     */
    @PostMapping("/save")
    public String saveDepartment(
            @Valid @ModelAttribute("departmentDTO") DepartmentDTO departmentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Attempting to save department: {}", departmentDTO.getName());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors found: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.departmentDTO",
                    bindingResult);
            redirectAttributes.addFlashAttribute("departmentDTO", departmentDTO);
            return "redirect:/departments";
        }

        try {
            departmentService.createDepartment(departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Department '" + departmentDTO.getName() + "' created successfully!");
        } catch (Exception e) {
            log.error("Error creating department", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }

    /**
     * Show edit form for a department
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.debug("Showing edit form for department ID: {}", id);

        DepartmentDTO department = departmentService.getDepartmentById(id);
        model.addAttribute("departmentDTO", department);
        model.addAttribute("isEdit", true);

        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);

        return "department/list";
    }

    /**
     * Update existing department
     */
    @PostMapping("/update/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("departmentDTO") DepartmentDTO departmentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Updating department ID: {}", id);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.departmentDTO",
                    bindingResult);
            redirectAttributes.addFlashAttribute("departmentDTO", departmentDTO);
            return "redirect:/departments/edit/" + id;
        }

        try {
            departmentService.updateDepartment(id, departmentDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Department updated successfully!");
        } catch (Exception e) {
            log.error("Error updating department", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }

    /**
     * Delete department
     */
    @PostMapping("/delete/{id}")
    public String deleteDepartment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info("Deleting department ID: {}", id);

        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Department deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting department", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/departments";
    }
}
