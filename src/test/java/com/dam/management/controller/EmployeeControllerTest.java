package com.dam.management.controller;

import com.dam.management.model.Employee;
import com.dam.management.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void testShowEmployees() throws Exception {
        // Given
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("John Doe");
        employee1.setCreatedDate(LocalDateTime.now());

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Smith");
        employee2.setCreatedDate(LocalDateTime.now());

        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("employees", employees));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testAddEmployee() throws Exception {
        // Given
        String employeeName = "New Test Employee";
        Employee mockEmployee = new Employee(1L, employeeName, LocalDateTime.now());

        when(employeeService.addEmployee(anyString())).thenReturn(mockEmployee);

        // When & Then
        mockMvc.perform(post("/employees/add")
                .param("name", employeeName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService, times(1)).addEmployee(employeeName);
    }

    @Test
    void testAddEmployeeWithEmptyName() throws Exception {
        // Given
        String emptyName = "";
        Employee mockEmployee = new Employee(1L, emptyName, LocalDateTime.now());

        when(employeeService.addEmployee(anyString())).thenReturn(mockEmployee);

        // When & Then - Should handle empty names gracefully
        mockMvc.perform(post("/employees/add")
                .param("name", emptyName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));

        verify(employeeService, times(1)).addEmployee(emptyName);
    }

    @Test
    void testShowEmployeesWhenEmpty() throws Exception {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(model().attributeExists("employees"))
                .andExpect(model().attribute("employees", Arrays.asList()));

        verify(employeeService, times(1)).getAllEmployees();
    }
}