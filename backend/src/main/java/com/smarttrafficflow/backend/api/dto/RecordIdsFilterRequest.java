package com.smarttrafficflow.backend.api.dto;

import java.util.List;
import java.util.UUID;

public record RecordIdsFilterRequest(
        List<UUID> recordIds
) {
}
