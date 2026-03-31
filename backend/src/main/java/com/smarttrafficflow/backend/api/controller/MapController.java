package com.smarttrafficflow.backend.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttrafficflow.backend.domain.trafficrecords.repository.TrafficRecordRepository;
import com.smarttrafficflow.backend.domain.trafficrecords.service.TrafficRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/traffic-map")
public class MapController {

    private static final Logger log = LoggerFactory.getLogger(MapController.class);
    private static final TypeReference<Map<String, Object>> GEOMETRY_TYPE = new TypeReference<>() {
    };
    private static final String LOW_COLOR = "#2BA36A";
    private static final String MEDIUM_COLOR = "#FFB24A";
    private static final String HIGH_COLOR = "#E04545";

    private final TrafficRecordService trafficRecordService;
    private final ObjectMapper objectMapper;

    public MapController(TrafficRecordService trafficRecordService, ObjectMapper objectMapper) {
        this.trafficRecordService = trafficRecordService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public Map<String, Object> getMapData(@RequestParam(required = false) List<UUID> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            log.info("GET /api/traffic-map - no recordIds selected, returning empty FeatureCollection");
            return Map.of("type", "FeatureCollection", "features", List.of());
        }

        List<TrafficRecordRepository.TrafficMapFeatureView> featuresView = trafficRecordService.findMapFeaturesByRecordIds(recordIds);
        List<Map<String, Object>> features = featuresView.stream().map(this::toFeature).toList();
        log.info("GET /api/traffic-map - returning {} features from {} selected records", features.size(), recordIds.size());
        return Map.of("type", "FeatureCollection", "features", features);
    }

    private Map<String, Object> toFeature(TrafficRecordRepository.TrafficMapFeatureView featureView) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        feature.put("properties", Map.of(
                "recordId", featureView.getRecordId(),
                "streetId", featureView.getStreetId(),
                "streetOsmWayId", featureView.getStreetOsmWayId(),
                "streetName", featureView.getStreetName(),
                "vehicleVolume", featureView.getVehicleVolume(),
                "trafficLevel", toTrafficLevel(featureView.getVehicleVolume()),
                "color", toColor(featureView.getVehicleVolume())
        ));

        try {
            feature.put("geometry", objectMapper.readValue(featureView.getGeometry(), GEOMETRY_TYPE));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Geometria invalida para streetId=" + featureView.getStreetId(), ex);
        }

        return feature;
    }

    private String toTrafficLevel(int volume) {
        if (volume <= 100) {
            return "LOW";
        }
        if (volume <= 200) {
            return "MEDIUM";
        }
        return "HIGH";
    }

    private String toColor(int volume) {
        if (volume <= 100) {
            return LOW_COLOR;
        }
        if (volume <= 200) {
            return MEDIUM_COLOR;
        }
        return HIGH_COLOR;
    }
}
