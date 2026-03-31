package com.smarttrafficflow.backend.api.dto;

import java.util.UUID;

public record StreetResponse(
        UUID id,
        Long osmWayId,
        String name
) {
}
