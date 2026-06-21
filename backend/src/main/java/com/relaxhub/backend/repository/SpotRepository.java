package com.relaxhub.backend.repository;

import com.relaxhub.backend.entity.Spot;
import com.relaxhub.backend.entity.SpotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findByActiveTrueOrderByNameAsc();

    List<Spot> findByTypeAndActiveTrueOrderByNameAsc(SpotType type);
}
