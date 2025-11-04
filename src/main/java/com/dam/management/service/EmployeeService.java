package com.dam.management.service;

import com.dam.management.model.Employee;
import com.dam.management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    public Employee addEmployee(String name) {
        Long id = employeeRepository.addEmployee(name);
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        return employee;
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }
    
    public Employee getEmployeeById(Long id) {
        return employeeRepository.getEmployeeById(id);
    }
    
    public void updateEmployee(Long id, String name) {
        employeeRepository.updateEmployee(id, name);
        System.out.println("=== SERVICE: Updated employee ID " + id + " to name: " + name + " ===");
    }
}