package com.smarttrafficflow.backend.domain.streets.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StreetImportConfigLogger implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StreetImportConfigLogger.class);

    private final JdbcTemplate jdbcTemplate;
    private final boolean importEnabled;
    private final String geoJsonPath;

    public StreetImportConfigLogger(
            JdbcTemplate jdbcTemplate,
            @Value("${app.streets.import.enabled:false}") boolean importEnabled,
            @Value("${app.streets.import.geojson-path:}") String geoJsonPath
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.importEnabled = importEnabled;
        this.geoJsonPath = geoJsonPath;
    }

    @Override
    public void run(String... args) {
        String safePath = (geoJsonPath == null || geoJsonPath.isBlank()) ? "<empty>" : geoJsonPath;
        log.info("Street import settings resolved: enabled={}, geojsonPath={}", importEnabled, safePath);

        try {
            Integer streetCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM streets", Integer.class);
            log.info("Current streets count in database: {}", streetCount == null ? 0 : streetCount);
        } catch (Exception ex) {
            log.warn("Could not read streets count during startup diagnostics: {}", ex.getMessage());
        }
    }
}
