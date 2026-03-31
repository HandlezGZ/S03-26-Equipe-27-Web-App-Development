package com.smarttrafficflow.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record TrafficStatsFilterRequest(
        @NotBlank String groupBy,
        List<UUID> recordIds
) {
}
