package com.relaxhub.backend.controller;

import com.relaxhub.backend.dto.ApiResponse;
import com.relaxhub.backend.dto.SpotResponse;
import com.relaxhub.backend.entity.SpotType;
import com.relaxhub.backend.service.SpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpotResponse>>> list(
            @RequestParam(required = false) SpotType type
    ) {
        List<SpotResponse> spots = spotService.listAll(type);
        return ResponseEntity.ok(ApiResponse.success("Spots loaded", spots));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<SpotResponse>>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "15") double radiusKm,
            @RequestParam(required = false) SpotType type
    ) {
        List<SpotResponse> spots = spotService.listNearby(lat, lng, radiusKm, type);
        return ResponseEntity.ok(ApiResponse.success("Nearby spots loaded", spots));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpotResponse>> getById(@PathVariable Long id) {
        SpotResponse spot = spotService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Spot loaded", spot));
    }
}
