package com.bankservice.controller;

import com.bankservice.config.BaseTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Health/Actuator endpoints.
 * Tests application health monitoring and status endpoints.
 */
public class HealthControllerTest extends BaseTestConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testHealthEndpoint_Success() {
        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(
            buildUrl(port, "/actuator/health"), 
            Map.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));
    }

    @Test
    void testHealthEndpoint_HasCorrectStructure() {
        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(
            buildUrl(port, "/actuator/health"), 
            Map.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        
        // Verify structure
        assertTrue(responseBody.containsKey("status"));
        
        // Optional: Check if components are present (they may not be in basic setup)
        if (responseBody.containsKey("components")) {
            assertNotNull(responseBody.get("components"));
        }
    }

    @Test
    void testHealthEndpoint_ContentType() {
        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(
            buildUrl(port, "/actuator/health"), 
            Map.class
        );
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var contentType = response.getHeaders().getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.toString().contains("application/json"));
    }

    @Test
    void testHealthEndpoint_NoAuthentication() {
        // This test verifies that health endpoint doesn't require authentication
        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(
            buildUrl(port, "/actuator/health"), 
            Map.class
        );
        
        // Then - Should still be accessible without auth
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("UP", responseBody.get("status"));
    }
}
