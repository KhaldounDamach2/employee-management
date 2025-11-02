package com.dam.management.controller;

import com.dam.management.model.Employee;
import com.dam.management.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    @GetMapping
    public String showEmployees(Model model) {
        System.out.println("=== CONTROLLER: showEmployees called ===");
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.println("=== CONTROLLER: Found " + employees.size() + " employees ===");
        model.addAttribute("employees", employees);
        return "employees";
    }
    
    @PostMapping("/add")
    public String addEmployee(@RequestParam String name, Model model) {
        System.out.println("=== CONTROLLER: addEmployee called with name: " + name + " ===");
        employeeService.addEmployee(name);
        return "redirect:/employees"; // Redirect to refresh the list
    }
}