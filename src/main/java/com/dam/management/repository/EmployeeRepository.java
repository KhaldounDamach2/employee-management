package com.dam.management.repository;

import com.dam.management.model.Employee;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Employee RowMapper for reuse
    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> {
        Employee emp = new Employee();
        emp.setId(rs.getLong("id"));
        emp.setName(rs.getString("name"));
        Timestamp timestamp = rs.getTimestamp("created_date");
        if (timestamp != null) {
            emp.setCreatedDate(timestamp.toLocalDateTime());
        }
        return emp;
    };

    public Long addEmployee(String name) {
        try {
            System.out.println("=== REPOSITORY: Adding employee: " + name + " ===");
            
            String url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            System.out.println("=== Database URL: " + url + " ===");
            
            if (url.contains("h2:mem")) {
                return addEmployeeWithH2(name);
            } else {
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
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("employees")
                .usingGeneratedKeyColumns("id");
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", name);
            
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

    public List<Employee> getAllEmployees() {
        try {
            System.out.println("=== REPOSITORY: Getting all employees ===");
            
            String sql = "SELECT id, name, created_date FROM employees ORDER BY created_date DESC";
            
            List<Employee> employees = jdbcTemplate.query(sql, employeeRowMapper);
            
            System.out.println("=== REPOSITORY: Retrieved " + employees.size() + " employees using direct SQL ===");
            return employees;
            
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error getting employees: " + e.getMessage() + " ===");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Employee getEmployeeById(Long id) {
        try {
            System.out.println("=== REPOSITORY: Getting employee by ID: " + id + " ===");
            
            String url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            
            if (url.contains("h2:mem")) {
                String sql = "SELECT id, name, created_date FROM employees WHERE id = ?";
                return jdbcTemplate.queryForObject(sql, employeeRowMapper, id);
            } else {
                return jdbcTemplate.execute((ConnectionCallback<Employee>) conn -> {
                    try (CallableStatement stmt = conn.prepareCall("{ ? = call EMPLOYEE_MANAGEMENT_PKG.GET_EMPLOYEE_BY_ID(?) }")) {
                        stmt.registerOutParameter(1, Types.REF_CURSOR);
                        stmt.setLong(2, id);
                        stmt.execute();
                        
                        try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                            if (rs.next()) {
                                return employeeRowMapper.mapRow(rs, 1);
                            } else {
                                throw new RuntimeException("Employee not found with ID: " + id);
                            }
                        }
                    }
                });
            }
            
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error getting employee by ID: " + e.getMessage() + " ===");
            throw new RuntimeException("Employee not found with ID: " + id, e);
        }
    }

    public void updateEmployee(Long id, String name) {
        try {
            System.out.println("=== REPOSITORY: Updating employee ID: " + id + " with name: " + name + " ===");
            
            String url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            
            if (url.contains("h2:mem")) {
                String sql = "UPDATE employees SET name = ? WHERE id = ?";
                int updated = jdbcTemplate.update(sql, name, id);
                if (updated == 0) {
                    throw new RuntimeException("Employee not found with ID: " + id);
                }
            } else {
                jdbcTemplate.execute((ConnectionCallback<Void>) conn -> {
                    try (CallableStatement stmt = conn.prepareCall("{call EMPLOYEE_MANAGEMENT_PKG.UPDATE_EMPLOYEE(?, ?)}")) {
                        stmt.setLong(1, id);
                        stmt.setString(2, name);
                        stmt.execute();
                    }
                    return null;
                });
            }
            
            System.out.println("=== REPOSITORY: Successfully updated employee ===");
            
        } catch (Exception e) {
            System.out.println("=== REPOSITORY: Error updating employee: " + e.getMessage() + " ===");
            throw new RuntimeException("Failed to update employee", e);
        }
    }
}