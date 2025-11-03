package com.dam.management.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testEmployeeCreation() {
        // Given - Use unique names for each test
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Test Employee One");
        employee.setCreatedDate(LocalDateTime.now());

        // When & Then
        assertEquals(1L, employee.getId());
        assertEquals("Test Employee One", employee.getName());
        assertNotNull(employee.getCreatedDate());
    }

    @Test
    void testEmployeeEqualsAndHashCode() {
        // Given - Use different IDs but same content to test equality
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Same Name Employee");

        Employee employee2 = new Employee();
        employee2.setId(1L);  // Same ID = same employee
        employee2.setName("Same Name Employee");

        Employee employee3 = new Employee();
        employee3.setId(2L);  // Different ID = different employee
        employee3.setName("Same Name Employee");

        // When & Then
        assertEquals(employee1, employee2, "Employees with same ID should be equal");
        assertNotEquals(employee1, employee3, "Employees with different IDs should not be equal");
        assertEquals(employee1.hashCode(), employee2.hashCode());
        assertNotEquals(employee1.hashCode(), employee3.hashCode());
    }

    @Test
    void testEmployeeToString() {
        // Given
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Test Employee");

        // When
        String toString = employee.toString();

        // Then
        assertTrue(toString.contains("Test Employee"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testEmployeeWithNullValues() {
        // Given
        Employee employee = new Employee();

        // When & Then
        assertNull(employee.getId());
        assertNull(employee.getName());
        assertNull(employee.getCreatedDate());
    }
}