package com.smarttrafficflow.backend.domain.streets.repository;

import com.smarttrafficflow.backend.domain.streets.entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StreetRepository extends JpaRepository<Street, UUID> {
    Optional<Street> findByOsmWayId(Long osmWayId);
}
