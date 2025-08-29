package com.bankservice.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Base test configuration that can be extended by other test classes.
 * Provides common Spring Boot test setup with in-memory SQLite database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite::memory:",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class BaseTestConfiguration {
    
    protected String buildUrl(int port, String path) {
        return "http://localhost:" + port + path;
    }
}
