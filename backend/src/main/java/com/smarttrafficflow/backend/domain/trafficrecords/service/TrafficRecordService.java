package com.smarttrafficflow.backend.domain.trafficrecords.service;

import com.smarttrafficflow.backend.api.dto.CreateTrafficRecordRequest;
import com.smarttrafficflow.backend.api.dto.TrafficRecordResponse;
import com.smarttrafficflow.backend.domain.trafficrecords.entity.TrafficRecord;
import com.smarttrafficflow.backend.domain.trafficrecords.repository.TrafficRecordRepository;
import com.smarttrafficflow.backend.domain.streets.entity.Street;
import com.smarttrafficflow.backend.domain.streets.service.StreetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TrafficRecordService {

    private static final Logger log = LoggerFactory.getLogger(TrafficRecordService.class);

    private final TrafficRecordRepository repository;
    private final StreetService streetService;

    public TrafficRecordService(TrafficRecordRepository repository, StreetService streetService) {
        this.repository = repository;
        this.streetService = streetService;
    }

    public TrafficRecordResponse create(CreateTrafficRecordRequest request) {
        Street street = streetService.getByOsmWayId(request.streetOsmWayId());

        TrafficRecord record = new TrafficRecord();
        record.setTimestamp(request.timestamp());
        record.setRoadType(request.roadType());
        record.setVehicleVolume(request.vehicleVolume());
        record.setEventType(request.eventType());
        record.setWeather(request.weather());
        record.setStreet(street);

        TrafficRecord saved = repository.save(record);
        log.info("TrafficRecord saved id={}, roadType={}, timestamp={}",
                saved.getId(), saved.getRoadType(), saved.getTimestamp());
        return toResponse(saved);
    }

    public List<TrafficRecordResponse> findAll() {
        List<TrafficRecordResponse> records = repository.findAllWithStreet().stream().map(this::toResponse).toList();
        log.debug("TrafficRecord findAll returned {} records", records.size());
        return records;
    }

    public List<TrafficRecord> findAllEntities() {
        List<TrafficRecord> records = repository.findAllWithStreet();
        log.debug("TrafficRecord findAllEntities returned {} records", records.size());
        return records;
    }

    public List<TrafficRecord> findEntitiesByIds(List<UUID> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return List.of();
        }

        List<TrafficRecord> records = repository.findAllByIdInWithStreet(recordIds);
        log.debug("TrafficRecord findEntitiesByIds returned {} records from {} ids", records.size(), recordIds.size());
        return records;
    }

    public List<TrafficRecordRepository.TrafficMapFeatureView> findMapFeaturesByRecordIds(List<UUID> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return List.of();
        }
        return repository.findMapFeaturesByRecordIds(recordIds);
    }

    private TrafficRecordResponse toResponse(TrafficRecord record) {
        return new TrafficRecordResponse(
                record.getId(),
                record.getTimestamp(),
                record.getRoadType(),
                record.getVehicleVolume(),
                record.getEventType(),
                record.getWeather(),
                record.getStreet().getId(),
                record.getStreet().getOsmWayId(),
                record.getStreet().getName()
        );
    }
}
