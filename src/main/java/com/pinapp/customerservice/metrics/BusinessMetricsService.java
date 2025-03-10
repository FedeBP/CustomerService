package com.pinapp.customerservice.metrics;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;
    
    private Counter customerCreatedCounter;
    private Counter customerUpdatedCounter;
    private Counter customerDeletedCounter;
    private DistributionSummary customerAgeDistribution;
    private Timer customerProcessingTimer;
    private AtomicInteger activeCustomersGauge;

    @PostConstruct
    public void init() {
        customerCreatedCounter = Counter.builder("customers.created")
                .description("Number of customers created")
                .register(meterRegistry);
        
        customerUpdatedCounter = Counter.builder("customers.updated")
                .description("Number of customers updated")
                .register(meterRegistry);
        
        customerDeletedCounter = Counter.builder("customers.deleted")
                .description("Number of customers deleted")
                .register(meterRegistry);

        customerAgeDistribution = DistributionSummary.builder("customers.age.distribution")
                .description("Distribution of customer ages")
                .publishPercentiles(0.5, 0.75, 0.95)
                .register(meterRegistry);

        customerProcessingTimer = Timer.builder("customers.processing.time")
                .description("Time spent processing customer requests")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        activeCustomersGauge = new AtomicInteger(0);
        Gauge.builder("customers.active", activeCustomersGauge::get)
                .description("Number of active customers")
                .register(meterRegistry);
    }

    public void incrementCustomerCreated() {
        customerCreatedCounter.increment();
        activeCustomersGauge.incrementAndGet();
    }

    public void incrementCustomerUpdated() {
        customerUpdatedCounter.increment();
    }

    public void incrementCustomerDeleted() {
        customerDeletedCounter.increment();
        activeCustomersGauge.decrementAndGet();
    }

    public void recordCustomerAge(int age) {
        customerAgeDistribution.record(age);
    }

    public Timer.Sample startCustomerProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopCustomerProcessingTimer(Timer.Sample sample) {
        sample.stop(customerProcessingTimer);
    }

    public void setActiveCustomersCount(int count) {
        activeCustomersGauge.set(count);
    }
}