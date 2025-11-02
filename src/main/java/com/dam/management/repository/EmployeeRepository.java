package com.dam.management.repository;

import com.dam.management.model.Employee;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
	
@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Add employee using PL/SQL package procedure - THIS IS WORKING
    public Long addEmployee(String name) {
        try {
            System.out.println("=== REPOSITORY: Adding employee: " + name + " ===");
            
            return jdbcTemplate.execute((ConnectionCallback<Long>) conn -> {
                try (CallableStatement stmt = conn.prepareCall("{call EMPLOYEE_PKG.ADD_EMPLOYEE(?, ?)}")) {
                    stmt.setString(1, name);
                    stmt.registerOutParameter(2, Types.NUMERIC);
                    stmt.execute();
                    Long newId = stmt.getLong(2);
                    System.out.println("=== REPOSITORY: Successfully added employee with ID: " + newId + " ===");
                    return newId;
                }
            });
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error adding employee: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to add employee", e);
        }
    }

    // Get all employees - SIMPLIFIED VERSION THAT WILL WORK
    public List<Employee> getAllEmployees() {
        try {
            System.out.println("=== REPOSITORY: Getting all employees ===");
            
            // Let's try a simpler approach first - direct SQL query
            String sql = "SELECT id, name, created_date FROM employees ORDER BY created_date DESC";
            
            List<Employee> employees = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Employee emp = new Employee();
                emp.setId(rs.getLong("id"));
                emp.setName(rs.getString("name"));
                
                Timestamp timestamp = rs.getTimestamp("created_date");
                if (timestamp != null) {
                    emp.setCreatedDate(timestamp.toLocalDateTime());
                }
                return emp;
            });
            
            System.out.println("=== REPOSITORY: Retrieved " + employees.size() + " employees using direct SQL ===");
            return employees;
            
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error getting employees: " + e.getMessage() + " ===");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}