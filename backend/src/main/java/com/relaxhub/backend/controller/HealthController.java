package com.relaxhub.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public Map<String, String> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "relaxhub-api");
        response.put("timestamp", Instant.now().toString());
        return response;
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, String>> databaseHealth() {
        Map<String, String> response = new LinkedHashMap<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            if (resultSet.next()) {
                response.put("database", "UP");
                return ResponseEntity.ok(response);
            }

            response.put("database", "DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } catch (Exception exception) {
            response.put("database", "DOWN");
            response.put("message", exception.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
