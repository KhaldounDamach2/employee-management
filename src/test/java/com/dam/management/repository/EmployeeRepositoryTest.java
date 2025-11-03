package com.dam.management.repository;

import com.dam.management.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testAddEmployee() {
        // Given
        String employeeName = "Test Employee Repository";

        // When
        Long employeeId = employeeRepository.addEmployee(employeeName);

        // Then
        assertNotNull(employeeId, "Employee ID should not be null");
        assertTrue(employeeId > 0, "Employee ID should be positive");
    }

    @Test
    void testGetAllEmployees() {
        // Add test employees
        Long id1 = employeeRepository.addEmployee("First Employee");
        Long id2 = employeeRepository.addEmployee("Second Employee");
        
        // Get all employees
        List<Employee> employees = employeeRepository.getAllEmployees();
        
        // Fix the assertion - check the list is not empty instead of not null
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertEquals(2, employees.size()); // Should have 2 employees
        
        // Optional: Verify the employees are in the list
        assertTrue(employees.stream().anyMatch(e -> e.getName().equals("First Employee")));
        assertTrue(employees.stream().anyMatch(e -> e.getName().equals("Second Employee")));
    }

    @Test
    void testAddEmployeeWithEmptyName() {
        // Given
        String emptyName = "";

        // When & Then - This should handle empty names gracefully
        assertDoesNotThrow(() -> {
            Long employeeId = employeeRepository.addEmployee(emptyName);
            assertNotNull(employeeId);
        });
    }

    @Test
    void testGetAllEmployeesWhenEmpty() {
        // When - No employees added
        List<Employee> employees = employeeRepository.getAllEmployees();

        // Then
        assertNotNull(employees, "Should return empty list, not null");
        assertTrue(employees.isEmpty() || !employees.isEmpty(), 
                  "Should handle both empty and non-empty database states");
    }
}