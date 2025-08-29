package com.bankservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test to verify the Spring Boot application starts correctly.
 * Specific functionality tests are organized by controller in separate test classes:
 * - AuthControllerTest: Authentication and user management
 * - AccountControllerTest: Account operations
 * - TransferControllerTest: Money transfer operations  
 * - HealthControllerTest: Health monitoring endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite::memory:",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BankServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // All beans are created and dependencies are properly wired
    }

    @Test
    void applicationStarts() {
        // This test verifies that the application starts without errors
        // Database connections, JPA entities, and security configuration work properly
    }
}
