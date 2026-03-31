package com.smarttrafficflow.backend.domain.streets.service;

import com.smarttrafficflow.backend.api.dto.StreetResponse;
import com.smarttrafficflow.backend.domain.streets.entity.Street;
import com.smarttrafficflow.backend.domain.streets.repository.StreetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class StreetService {

    private final StreetRepository streetRepository;
    private final Random random = new Random();

    public StreetService(StreetRepository streetRepository) {
        this.streetRepository = streetRepository;
    }

    public List<StreetResponse> findAll() {
        return streetRepository.findAll()
                .stream()
                .map(street -> new StreetResponse(street.getId(), street.getOsmWayId(), street.getName()))
                .toList();
    }

    public Street getByOsmWayId(Long osmWayId) {
        return streetRepository.findByOsmWayId(osmWayId)
                .orElseThrow(() -> new IllegalArgumentException("streetOsmWayId invalido: " + osmWayId));
    }

    public Long getRandomStreetOsmWayId() {
        List<Long> osmWayIds = streetRepository.findAll()
                .stream()
                .map(Street::getOsmWayId)
                .toList();

        if (osmWayIds.isEmpty()) {
            throw new IllegalStateException("Nenhuma rua real importada. Importe GeoJSON antes de simular.");
        }

        return osmWayIds.get(random.nextInt(osmWayIds.size()));
    }
}
