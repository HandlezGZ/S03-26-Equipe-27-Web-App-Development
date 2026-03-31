package com.smarttrafficflow.backend.domain.streets.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.streets.import.enabled", havingValue = "true")
public class StreetGeoJsonImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StreetGeoJsonImportRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final String geoJsonPath;

    public StreetGeoJsonImportRunner(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            @Value("${app.streets.import.geojson-path}") String geoJsonPath
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.geoJsonPath = geoJsonPath;
    }

    @Override
    public void run(String... args) throws IOException {
        log.info(
                "Street import config: enabled=true, geojsonPath={}",
                (geoJsonPath == null || geoJsonPath.isBlank()) ? "<empty>" : geoJsonPath
        );

        Integer streetCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM streets", Integer.class);
        if (streetCount != null && streetCount > 0) {
            log.info("Street import skipped because {} streets already exist in database", streetCount);
            return;
        }

        log.info("Street table is empty. Starting GeoJSON import.");

        if (geoJsonPath == null || geoJsonPath.isBlank()) {
            throw new IllegalArgumentException("APP_STREETS_IMPORT_GEOJSON_PATH nao configurado");
        }

        Path path = Paths.get(geoJsonPath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Arquivo GeoJSON nao encontrado: " + geoJsonPath);
        }

        JsonNode root = objectMapper.readTree(Files.readString(path, StandardCharsets.UTF_8));
        JsonNode features = root.get("features");
        if (features == null || !features.isArray()) {
            throw new IllegalArgumentException("GeoJSON invalido: campo 'features' nao encontrado");
        }
        log.info("GeoJSON loaded successfully with {} features", features.size());

        int imported = 0;
        int skipped = 0;

        for (JsonNode feature : features) {
            JsonNode geometry = feature.get("geometry");
            JsonNode properties = feature.get("properties");
            if (geometry == null || properties == null) {
                skipped++;
                continue;
            }

            String geometryType = geometry.path("type").asText("");
            if (!"LineString".equals(geometryType)) {
                skipped++;
                continue;
            }

            long osmWayId = extractOsmWayId(properties, feature);
            if (osmWayId <= 0) {
                skipped++;
                continue;
            }

            String streetName = properties.path("name").asText("");
            if (streetName.isBlank()) {
                streetName = "OSM WAY " + osmWayId;
            }

            UUID streetId = UUID.nameUUIDFromBytes(("osm-way-" + osmWayId).getBytes(StandardCharsets.UTF_8));
            String geometryJson = objectMapper.writeValueAsString(geometry);

            jdbcTemplate.update(
                    """
                    INSERT INTO streets (id, osm_way_id, name, geom)
                    VALUES (?, ?, ?, ST_SetSRID(ST_GeomFromGeoJSON(?), 4326))
                    ON CONFLICT (osm_way_id) DO UPDATE
                    SET name = EXCLUDED.name,
                        geom = EXCLUDED.geom
                    """,
                    streetId,
                    osmWayId,
                    streetName,
                    geometryJson
            );
            imported++;
        }

        Integer finalStreetCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM streets", Integer.class);
        log.info(
                "Street GeoJSON import complete: imported={}, skipped={}, finalStreetCount={}, path={}",
                imported,
                skipped,
                finalStreetCount == null ? 0 : finalStreetCount,
                geoJsonPath
        );
    }

    private long extractOsmWayId(JsonNode properties, JsonNode feature) {
        JsonNode explicitOsmWayId = properties.get("osm_way_id");
        if (explicitOsmWayId != null && explicitOsmWayId.canConvertToLong()) {
            return explicitOsmWayId.asLong();
        }

        JsonNode osmId = properties.get("osm_id");
        if (osmId != null && osmId.canConvertToLong()) {
            return osmId.asLong();
        }

        String id = feature.path("id").asText("");
        if (id.startsWith("way/")) {
            try {
                return Long.parseLong(id.substring("way/".length()));
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }

        return -1;
    }
}
