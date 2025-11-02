-- Create sequence for employee IDs (if not exists)
CREATE SEQUENCE employee_id_seq START WITH 1 INCREMENT BY 1;

-- Create employees table (matching your local structure)
CREATE TABLE employees (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    created_date DATE DEFAULT SYSDATE
);

-- Create index for better performance on date queries
CREATE INDEX idx_employees_created_date ON employees(created_date);