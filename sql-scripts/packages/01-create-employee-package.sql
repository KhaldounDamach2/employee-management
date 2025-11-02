-- Create employee management package specification
CREATE OR REPLACE PACKAGE employee_management_pkg AS
    
    -- Procedure to add a new employee and return the generated ID
    PROCEDURE create_employee(
        p_employee_name IN VARCHAR2,
        p_generated_id OUT NUMBER
    );
    
    -- Function to get all employees ordered by creation date
    FUNCTION get_all_employees RETURN SYS_REFCURSOR;
    
END employee_management_pkg;
/

-- Create employee management package body
CREATE OR REPLACE PACKAGE BODY employee_management_pkg AS

    PROCEDURE create_employee(
        p_employee_name IN VARCHAR2,
        p_generated_id OUT NUMBER
    ) AS
    BEGIN
        -- Get next sequence value for employee ID
        SELECT employee_id_seq.NEXTVAL INTO p_generated_id FROM dual;
        
        -- Insert new employee record
        INSERT INTO employees (id, name) VALUES (p_generated_id, p_employee_name);
        
        COMMIT;
    END create_employee;

    FUNCTION get_all_employees RETURN SYS_REFCURSOR AS
        employee_cursor SYS_REFCURSOR;
    BEGIN
        OPEN employee_cursor FOR
            SELECT id, name, created_date
            FROM employees
            ORDER BY created_date DESC;
            
        RETURN employee_cursor;
    END get_all_employees;

END employee_management_pkg;
/