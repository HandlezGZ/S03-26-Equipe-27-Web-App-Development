package com.smarttrafficflow.backend.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttrafficflow.backend.api.dto.TrafficRecordResponse;
import com.smarttrafficflow.backend.api.exception.GlobalExceptionHandler;
import com.smarttrafficflow.backend.domain.trafficrecords.service.TrafficRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrafficRecordController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("TrafficRecordController MVC integration tests")
class TrafficRecordControllerITTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrafficRecordService trafficRecordService;

    @Test
    @DisplayName("creates a traffic record when the payload is valid")
    void createsRecordWhenPayloadIsValid() throws Exception {
        UUID recordId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID streetId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        TrafficRecordResponse response = new TrafficRecordResponse(
                recordId,
                OffsetDateTime.parse("2024-06-17T08:00:00Z"),
                "ARTERIAL",
                120,
                "RUSH_HOUR",
                "SUNNY",
                streetId,
                101L,
                "Avenida Central"
        );
        when(trafficRecordService.create(any())).thenReturn(response);

        Map<String, Object> body = Map.of(
                "timestamp", "2024-06-17T08:00:00Z",
                "roadType", "ARTERIAL",
                "vehicleVolume", 120,
                "eventType", "RUSH_HOUR",
                "weather", "SUNNY",
                "streetOsmWayId", 101
        );

        mockMvc.perform(post("/api/traffic-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(recordId.toString()))
                .andExpect(jsonPath("$.roadType").value("ARTERIAL"))
                .andExpect(jsonPath("$.vehicleVolume").value(120))
                .andExpect(jsonPath("$.streetOsmWayId").value(101))
                .andExpect(jsonPath("$.streetName").value("Avenida Central"));
    }

    @Test
    @DisplayName("rejects requests without the required road type")
    void rejectsRecordWhenRoadTypeIsMissing() throws Exception {
        Map<String, Object> body = Map.of(
                "timestamp", "2024-06-17T08:00:00Z",
                "vehicleVolume", 120,
                "streetOsmWayId", 101
        );

        mockMvc.perform(post("/api/traffic-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value(org.hamcrest.Matchers.containsString("roadType")));
    }

    @Test
    @DisplayName("rejects requests with a non-positive street osm way id")
    void rejectsRecordWhenStreetOsmWayIdIsInvalid() throws Exception {
        Map<String, Object> body = Map.of(
                "timestamp", "2024-06-17T08:00:00Z",
                "roadType", "LOCAL",
                "vehicleVolume", 120,
                "streetOsmWayId", 0
        );

        mockMvc.perform(post("/api/traffic-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value(org.hamcrest.Matchers.containsString("streetOsmWayId")));
    }

    @Test
    @DisplayName("surfaces invalid street ids reported by the service")
    void rejectsRecordWhenStreetServiceCannotResolveTheStreet() throws Exception {
        when(trafficRecordService.create(any())).thenThrow(new IllegalArgumentException("streetOsmWayId invalido: 999"));

        Map<String, Object> body = Map.of(
                "timestamp", "2024-06-17T08:00:00Z",
                "roadType", "LOCAL",
                "vehicleVolume", 120,
                "streetOsmWayId", 999
        );

        mockMvc.perform(post("/api/traffic-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("streetOsmWayId invalido: 999"));
    }

    @Test
    @DisplayName("returns an empty list when there are no stored records")
    void returnsEmptyListWhenThereAreNoRecords() throws Exception {
        when(trafficRecordService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/traffic-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("returns all records exposed by the service")
    void returnsAllRecordsWhenDataExists() throws Exception {
        when(trafficRecordService.findAll()).thenReturn(List.of(
                new TrafficRecordResponse(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        OffsetDateTime.parse("2024-06-17T08:00:00Z"),
                        "ARTERIAL",
                        120,
                        "RUSH_HOUR",
                        "SUNNY",
                        UUID.fromString("00000000-0000-0000-0000-000000000101"),
                        101L,
                        "Avenida Central"
                ),
                new TrafficRecordResponse(
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        OffsetDateTime.parse("2024-06-17T17:30:00Z"),
                        "HIGHWAY",
                        250,
                        null,
                        "RAIN",
                        UUID.fromString("00000000-0000-0000-0000-000000000202"),
                        202L,
                        "Rodovia Norte"
                )
        ));

        mockMvc.perform(get("/api/traffic-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].streetName").value("Avenida Central"))
                .andExpect(jsonPath("$[1].roadType").value("HIGHWAY"));
    }
}
