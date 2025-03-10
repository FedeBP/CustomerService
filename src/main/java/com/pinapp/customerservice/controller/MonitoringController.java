package com.pinapp.customerservice.controller;

import com.pinapp.customerservice.repository.CustomerRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {

    private final CustomerRepository customerRepository;
    private final MeterRegistry meterRegistry;
    private final MetricsEndpoint metricsEndpoint;
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getMonitoringSummary() {
        log.info("Generating monitoring summary");
        Map<String, Object> summary = new HashMap<>();

        summary.put("jvmMemoryUsed", metricsEndpoint.metric("jvm.memory.used", null).getMeasurements());
        summary.put("systemCpuUsage", metricsEndpoint.metric("system.cpu.usage", null).getMeasurements());
        summary.put("processUptime", metricsEndpoint.metric("process.uptime", null).getMeasurements());

        summary.put("totalCustomers", customerRepository.count());
        summary.put("customerCreationRate", metricsEndpoint.metric("customers.created", null).getMeasurements());
        summary.put("averageProcessingTime", metricsEndpoint.metric("customers.processing.time", null).getMeasurements());
        
        return summary;
    }
    
    @GetMapping("/prometheus")
    public String getPrometheusMetrics() {
        return prometheusMeterRegistry.scrape();
    }
}