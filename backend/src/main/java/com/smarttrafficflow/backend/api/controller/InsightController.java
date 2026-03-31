package com.smarttrafficflow.backend.api.controller;

import com.smarttrafficflow.backend.api.dto.TrafficInsightResponse;
import com.smarttrafficflow.backend.domain.insights.service.InsightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/traffic-insights")
public class InsightController {

    private static final Logger log = LoggerFactory.getLogger(InsightController.class);

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping
    public TrafficInsightResponse getInsights(@RequestParam(required = false) List<UUID> recordIds) {
        int filterSize = recordIds == null ? 0 : recordIds.size();
        log.info("GET /api/traffic-insights - generating insights with {} selected recordIds", filterSize);
        TrafficInsightResponse response = insightService.generateInsights(recordIds);
        log.info("GET /api/traffic-insights - returning {} insights", response.insights().size());
        return response;
    }
}
