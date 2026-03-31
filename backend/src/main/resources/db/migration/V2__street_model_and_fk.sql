CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE streets (
    id UUID PRIMARY KEY,
    osm_way_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    geom geometry(LineString, 4326) NOT NULL
);

CREATE INDEX idx_streets_geom_gist ON streets USING GIST(geom);

ALTER TABLE traffic_records
    ADD COLUMN street_id UUID;

ALTER TABLE traffic_records
    ADD CONSTRAINT fk_traffic_records_street
    FOREIGN KEY (street_id)
    REFERENCES streets(id);

CREATE INDEX idx_traffic_records_street_id ON traffic_records(street_id);

ALTER TABLE traffic_records
    ALTER COLUMN street_id SET NOT NULL;

ALTER TABLE traffic_records
    DROP COLUMN region;
