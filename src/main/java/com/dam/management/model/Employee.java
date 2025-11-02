package com.dam.management.model;

import java.time.LocalDateTime;

public class Employee {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    
    // Constructors
    public Employee() {}
    
    public Employee(Long id, String name, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', createdDate=" + createdDate + "}";
    }
}