package com.dam.management;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
//@Disabled("Temporarily disabled until Oracle connection is fixed")
class EmployeeManagementApplicationTests {

    @Test
    void contextLoads() {
        // Test that context loads
    }
}