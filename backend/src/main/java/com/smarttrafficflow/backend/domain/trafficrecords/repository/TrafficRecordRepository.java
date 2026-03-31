package com.smarttrafficflow.backend.domain.trafficrecords.repository;

import com.smarttrafficflow.backend.domain.trafficrecords.entity.TrafficRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TrafficRecordRepository extends JpaRepository<TrafficRecord, UUID> {

    @Query("select tr from TrafficRecord tr join fetch tr.street")
    List<TrafficRecord> findAllWithStreet();

    @Query("select tr from TrafficRecord tr join fetch tr.street where tr.id in :ids")
    List<TrafficRecord> findAllByIdInWithStreet(@Param("ids") Collection<UUID> ids);

    @Query(
            value = """
                    SELECT
                        tr.id AS recordId,
                        s.id AS streetId,
                        s.osm_way_id AS streetOsmWayId,
                        s.name AS streetName,
                        tr.vehicle_volume AS vehicleVolume,
                        ST_AsGeoJSON(s.geom) AS geometry
                    FROM traffic_records tr
                    JOIN streets s ON s.id = tr.street_id
                    WHERE tr.id IN (:ids)
                    """,
            nativeQuery = true
    )
    List<TrafficMapFeatureView> findMapFeaturesByRecordIds(@Param("ids") Collection<UUID> ids);

    interface TrafficMapFeatureView {
        UUID getRecordId();

        UUID getStreetId();

        Long getStreetOsmWayId();

        String getStreetName();

        Integer getVehicleVolume();

        String getGeometry();
    }
}
