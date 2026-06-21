package com.relaxhub.backend.config;

import com.relaxhub.backend.entity.Spot;
import com.relaxhub.backend.entity.SpotType;
import com.relaxhub.backend.repository.SpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotDataInitializer {

    @Bean
    CommandLineRunner seedSpots(SpotRepository spotRepository) {
        return args -> {
            if (spotRepository.count() > 0) {
                return;
            }

            spotRepository.save(spot("Kivukoni Fish Market", SpotType.RESTAURANT,
                    "Fresh seafood and local dishes near the harbour.",
                    -6.8178, 39.2875, "Kivukoni, Dar es Salaam", "+255712345001"));
            spotRepository.save(spot("Slipway Waterfront", SpotType.RESTAURANT,
                    "Popular dining strip with ocean views.",
                    -6.7489, 39.2712, "Oyster Bay, Dar es Salaam", "+255712345002"));
            spotRepository.save(spot("Mlimani City Food Court", SpotType.RESTAURANT,
                    "Mall food court with varied cuisines.",
                    -6.7731, 39.2298, "Mlimani, Dar es Salaam", "+255712345003"));
            spotRepository.save(spot("Coco Beach Grill", SpotType.RESTAURANT,
                    "Casual beachside grill and drinks.",
                    -6.7534, 39.2678, "Masaki, Dar es Salaam", "+255712345004"));
            spotRepository.save(spot("Kariakoo Street Kitchen", SpotType.RESTAURANT,
                    "Affordable local meals in the busy market area.",
                    -6.8234, 39.2698, "Kariakoo, Dar es Salaam", "+255712345005"));

            spotRepository.save(spot("Serenity Spa Masaki", SpotType.RELAXATION,
                    "Massage, sauna, and wellness treatments.",
                    -6.7512, 39.2689, "Masaki, Dar es Salaam", "+255712345006"));
            spotRepository.save(spot("Ocean Breeze Yoga Studio", SpotType.RELAXATION,
                    "Yoga classes and meditation sessions.",
                    -6.7598, 39.2745, "Oyster Bay, Dar es Salaam", "+255712345007"));
            spotRepository.save(spot("Urban Retreat Lounge", SpotType.RELAXATION,
                    "Quiet lounge for tea, reading, and rest.",
                    -6.7924, 39.2083, "City Centre, Dar es Salaam", "+255712345008"));
            spotRepository.save(spot("Msasani Wellness Centre", SpotType.RELAXATION,
                    "Spa packages and relaxation therapies.",
                    -6.7456, 39.2798, "Msasani, Dar es Salaam", "+255712345009"));
            spotRepository.save(spot("Kunduchi Beach Park", SpotType.RELAXATION,
                    "Beach park for walks, picnics, and unwinding.",
                    -6.6789, 39.2123, "Kunduchi, Dar es Salaam", "+255712345010"));
        };
    }

    private Spot spot(
            String name,
            SpotType type,
            String description,
            double latitude,
            double longitude,
            String address,
            String phone
    ) {
        Spot spot = new Spot();
        spot.setName(name);
        spot.setType(type);
        spot.setDescription(description);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);
        spot.setAddress(address);
        spot.setPhone(phone);
        spot.setActive(true);
        return spot;
    }
}
