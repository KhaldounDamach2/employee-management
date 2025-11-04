-- Create employee management package specification
CREATE OR REPLACE PACKAGE employee_management_pkg AS
    
    -- Procedure to add a new employee and return the generated ID
    PROCEDURE add_employee(
        p_employee_name IN VARCHAR2,
        p_generated_id OUT NUMBER
    );
    
    -- Function to get all employees ordered by creation date
    FUNCTION get_employees RETURN SYS_REFCURSOR;
    
    -- Procedure to update an existing employee
    PROCEDURE update_employee(
        p_employee_id IN NUMBER,
        p_employee_name IN VARCHAR2
    );
    
    -- Function to get employee by ID
    FUNCTION get_employee_by_id(p_employee_id IN NUMBER) RETURN SYS_REFCURSOR;
    
END employee_management_pkg;
/

-- Create employee management package body
CREATE OR REPLACE PACKAGE BODY employee_management_pkg AS

    PROCEDURE add_employee(
        p_employee_name IN VARCHAR2,
        p_generated_id OUT NUMBER
    ) AS
    BEGIN
        -- Get next sequence value for employee ID
        SELECT employee_id_seq.NEXTVAL INTO p_generated_id FROM dual;
        
        -- Insert new employee record
        INSERT INTO employees (id, name) VALUES (p_generated_id, p_employee_name);
        
        COMMIT;
    END add_employee;

    FUNCTION get_employees RETURN SYS_REFCURSOR AS
        employee_cursor SYS_REFCURSOR;
    BEGIN
        OPEN employee_cursor FOR
            SELECT id, name, created_date
            FROM employees
            ORDER BY created_date DESC;
            
        RETURN employee_cursor;
    END get_employees;

    PROCEDURE update_employee(
        p_employee_id IN NUMBER,
        p_employee_name IN VARCHAR2
    ) AS
    BEGIN
        UPDATE employees 
        SET name = p_employee_name
        WHERE id = p_employee_id;
        
        IF SQL%ROWCOUNT = 0 THEN
            RAISE NO_DATA_FOUND;
        END IF;
        
        COMMIT;
    END update_employee;

    FUNCTION get_employee_by_id(p_employee_id IN NUMBER) RETURN SYS_REFCURSOR AS
        employee_cursor SYS_REFCURSOR;
    BEGIN
        OPEN employee_cursor FOR
            SELECT id, name, created_date
            FROM employees
            WHERE id = p_employee_id;
            
        RETURN employee_cursor;
    END get_employee_by_id;

END employee_management_pkg;
/