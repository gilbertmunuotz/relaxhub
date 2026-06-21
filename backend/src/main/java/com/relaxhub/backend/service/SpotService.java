package com.relaxhub.backend.service;

import com.relaxhub.backend.dto.SpotResponse;
import com.relaxhub.backend.entity.Spot;
import com.relaxhub.backend.entity.SpotType;
import com.relaxhub.backend.exception.SpotNotFoundException;
import com.relaxhub.backend.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;

    public SpotService(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    public List<SpotResponse> listAll(SpotType type) {
        List<Spot> spots = type == null
                ? spotRepository.findByActiveTrueOrderByNameAsc()
                : spotRepository.findByTypeAndActiveTrueOrderByNameAsc(type);
        return spots.stream().map(spot -> toResponse(spot, null)).toList();
    }

    public List<SpotResponse> listNearby(double latitude, double longitude, double radiusKm, SpotType type) {
        List<Spot> spots = type == null
                ? spotRepository.findByActiveTrueOrderByNameAsc()
                : spotRepository.findByTypeAndActiveTrueOrderByNameAsc(type);
        return spots.stream()
                .map(spot -> toResponse(spot, distanceKm(
                        latitude, longitude, spot.getLatitude(), spot.getLongitude()
                )))
                .filter(spot -> spot.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparing(SpotResponse::getDistanceKm))
                .toList();
    }

    public SpotResponse getById(Long id) {
        Spot spot = spotRepository.findById(id)
                .filter(Spot::isActive)
                .orElseThrow(SpotNotFoundException::new);
        return toResponse(spot, null);
    }

    private SpotResponse toResponse(Spot spot, Double distanceKm) {
        return new SpotResponse(
                spot.getId(),
                spot.getName(),
                spot.getType(),
                spot.getDescription(),
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getAddress(),
                spot.getPhone(),
                distanceKm
        );
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
