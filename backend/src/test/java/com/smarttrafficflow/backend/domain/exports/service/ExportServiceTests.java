package com.smarttrafficflow.backend.domain.exports.service;

import com.smarttrafficflow.backend.api.dto.TrafficRecordResponse;
import com.smarttrafficflow.backend.domain.trafficrecords.service.TrafficRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExportService tests")
class ExportServiceTests {

    @Mock
    private TrafficRecordService trafficRecordService;

    @InjectMocks
    private ExportService exportService;

    @Test
    @DisplayName("returns only the current csv header when there are no records")
    void returnsOnlyHeaderWhenThereAreNoRecords() {
        when(trafficRecordService.findAll()).thenReturn(List.of());

        String csv = exportService.exportCsv();

        assertThat(csv).isEqualTo("id,timestamp,roadType,vehicleVolume,eventType,weather,streetId,streetOsmWayId,streetName\n");
    }

    @Test
    @DisplayName("exports each current record field in csv order")
    void exportsCurrentStreetBasedColumns() {
        when(trafficRecordService.findAll()).thenReturn(List.of(
                recordResponse("ARTERIAL", 120, "RUSH_HOUR", "SUNNY", 101L, "Avenida Central"),
                recordResponse("HIGHWAY", 250, null, "RAIN", 202L, "Rodovia Norte")
        ));

        String csv = exportService.exportCsv();

        assertThat(csv).contains("streetId,streetOsmWayId,streetName");
        assertThat(csv).contains("ARTERIAL,120,RUSH_HOUR,SUNNY");
        assertThat(csv).contains("HIGHWAY,250,,RAIN");
        assertThat(csv).contains(",101,Avenida Central");
        assertThat(csv).contains(",202,Rodovia Norte");
    }

    @Test
    @DisplayName("renders nullable string fields as empty values")
    void rendersNullableFieldsAsEmptyValues() {
        when(trafficRecordService.findAll()).thenReturn(List.of(
                recordResponse("LOCAL", 80, null, null, 303L, null)
        ));

        String dataLine = exportService.exportCsv().lines().skip(1).findFirst().orElseThrow();

        assertThat(dataLine).contains(",LOCAL,80,,,");
        assertThat(dataLine).endsWith(",303,");
    }

    @Test
    @DisplayName("returns one data line per exported record")
    void returnsOneDataLinePerExportedRecord() {
        when(trafficRecordService.findAll()).thenReturn(List.of(
                recordResponse("ARTERIAL", 120, "RUSH_HOUR", "SUNNY", 101L, "Avenida Central"),
                recordResponse("HIGHWAY", 250, null, "RAIN", 202L, "Rodovia Norte"),
                recordResponse("LOCAL", 80, "SCHOOL", "CLOUDY", 303L, "Rua Um")
        ));

        List<String> lines = exportService.exportCsv().lines().toList();

        assertThat(lines).hasSize(4);
        assertThat(lines.get(0)).isEqualTo("id,timestamp,roadType,vehicleVolume,eventType,weather,streetId,streetOsmWayId,streetName");
        assertThat(lines.get(3)).contains(",LOCAL,80,SCHOOL,CLOUDY,");
    }

    private TrafficRecordResponse recordResponse(
            String roadType,
            int volume,
            String eventType,
            String weather,
            long streetOsmWayId,
            String streetName
    ) {
        return new TrafficRecordResponse(
                UUID.randomUUID(),
                OffsetDateTime.of(2024, 6, 17, 8, 0, 0, 0, ZoneOffset.UTC),
                roadType,
                volume,
                eventType,
                weather,
                UUID.randomUUID(),
                streetOsmWayId,
                streetName
        );
    }
}
