package com.dam.management.repository;

import com.dam.management.model.Employee;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import java.util.HashMap;
import java.util.Map;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long addEmployee(String name) {
        try {
            System.out.println("=== REPOSITORY: Adding employee: " + name + " ===");
            
            // Check if we're using H2 (for tests) or Oracle
            String url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            System.out.println("=== Database URL: " + url + " ===");
            
            if (url.contains("h2:mem")) {
                // Use direct INSERT for H2 tests
                System.out.println("=== Using H2 database - direct INSERT ===");
                return addEmployeeWithH2(name);
            } else {
                // Use package call for Oracle
                System.out.println("=== Using Oracle database - package call ===");
                return addEmployeeWithOracle(name);
            }
            
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error adding employee: " + e.getMessage() + " ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to add employee", e);
        }
    }

    

    private Long addEmployeeWithH2(String name) {
        try {
            // First, let's check if table exists and create it if not
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            
            // Use SimpleJdbcInsert - this handles the key retrieval automatically
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("employees")
                .usingGeneratedKeyColumns("id");
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", name);
            // Don't include created_date - let the default value handle it
            
            Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
            System.out.println("=== H2: Successfully added employee with ID: " + newId + " ===");
            return newId.longValue();
            
        } catch (Exception e) {
            System.out.println("=== H2: Error in addEmployeeWithH2: " + e.getMessage() + " ===");
            throw e;
        }
    }

    private Long addEmployeeWithOracle(String name) {
        return jdbcTemplate.execute((ConnectionCallback<Long>) conn -> {
            try (CallableStatement stmt = conn.prepareCall("{call EMPLOYEE_MANAGEMENT_PKG.ADD_EMPLOYEE(?, ?)}")) {
                stmt.setString(1, name);
                stmt.registerOutParameter(2, Types.NUMERIC);
                stmt.execute();
                Long newId = stmt.getLong(2);
                System.out.println("=== Oracle: Successfully added employee with ID: " + newId + " ===");
                return newId;
            }
        });
    }

    // Get all employees - keep as is (it's working)
    public List<Employee> getAllEmployees() {
        try {
            System.out.println("=== REPOSITORY: Getting all employees ===");
            
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