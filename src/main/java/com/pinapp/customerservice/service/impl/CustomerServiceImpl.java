package com.pinapp.customerservice.service.impl;

import com.pinapp.customerservice.dto.CustomerDetailDto;
import com.pinapp.customerservice.dto.CustomerDto;
import com.pinapp.customerservice.dto.CustomerMetricsDto;
import com.pinapp.customerservice.entity.Customer;
import com.pinapp.customerservice.exception.ResourceNotFoundException;
import com.pinapp.customerservice.messaging.CustomerMessageProducer;
import com.pinapp.customerservice.metrics.BusinessMetricsService;
import com.pinapp.customerservice.repository.CustomerRepository;
import com.pinapp.customerservice.service.CustomerService;
import com.pinapp.customerservice.util.LifeExpectancyCalculator;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMessageProducer messageProducer;
    private final LifeExpectancyCalculator lifeExpectancyCalculator;
    private final BusinessMetricsService metricsService;

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        log.info("Creating new customer: {}", customerDto);
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            Customer customer = mapToEntity(customerDto);
            Customer savedCustomer = customerRepository.save(customer);

            messageProducer.sendCustomerCreationMessage(mapToDto(savedCustomer));

            metricsService.incrementCustomerCreated();
            metricsService.recordCustomerAge(customer.getAge());

            log.info("Customer created successfully with ID: {}", savedCustomer.getId());
            return mapToDto(savedCustomer);
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

            return mapToDto(customer);
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDetailDto> getAllCustomersWithLifeExpectancy() {
        log.info("Fetching all customers with life expectancy calculation");
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            List<Customer> customers = customerRepository.findAll();
            metricsService.setActiveCustomersCount(customers.size());

            return customers.stream()
                    .map(this::mapToDetailDto)
                    .collect(Collectors.toList());
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerMetricsDto calculateMetrics() {
        log.info("Calculating customer metrics");
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            List<Customer> customers = customerRepository.findAll();

            if (customers.isEmpty()) {
                return CustomerMetricsDto.builder()
                        .averageAge(0.0)
                        .ageStandardDeviation(0.0)
                        .totalCustomers(0L)
                        .youngestCustomerAge(0)
                        .oldestCustomerAge(0)
                        .build();
            }

            Double averageAge = customerRepository.findAverageAge();
            Double stdDeviation = customerRepository.findAgeStandardDeviation();

            Integer minAge = customers.stream()
                    .min(Comparator.comparing(Customer::getAge))
                    .map(Customer::getAge)
                    .orElse(0);

            Integer maxAge = customers.stream()
                    .max(Comparator.comparing(Customer::getAge))
                    .map(Customer::getAge)
                    .orElse(0);

            return CustomerMetricsDto.builder()
                    .averageAge(averageAge != null ? averageAge : 0.0)
                    .ageStandardDeviation(stdDeviation != null ? stdDeviation : 0.0)
                    .totalCustomers((long) customers.size())
                    .youngestCustomerAge(minAge)
                    .oldestCustomerAge(maxAge)
                    .build();
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        log.info("Updating customer with ID: {}", id);
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            Customer existingCustomer = customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

            existingCustomer.setFirstName(customerDto.getFirstName());
            existingCustomer.setLastName(customerDto.getLastName());
            existingCustomer.setAge(customerDto.getAge());
            existingCustomer.setDateOfBirth(customerDto.getDateOfBirth());

            Customer updatedCustomer = customerRepository.save(existingCustomer);

            metricsService.incrementCustomerUpdated();
            metricsService.recordCustomerAge(updatedCustomer.getAge());

            log.info("Customer updated successfully: {}", updatedCustomer);

            return mapToDto(updatedCustomer);
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        Timer.Sample sample = metricsService.startCustomerProcessingTimer();

        try {
            if (!customerRepository.existsById(id)) {
                throw new ResourceNotFoundException("Customer not found with ID: " + id);
            }

            customerRepository.deleteById(id);

            metricsService.incrementCustomerDeleted();

            log.info("Customer deleted successfully");
        } finally {
            metricsService.stopCustomerProcessingTimer(sample);
        }
    }

    // Helper methods
    private Customer mapToEntity(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .age(dto.getAge())
                .dateOfBirth(dto.getDateOfBirth())
                .build();
    }

    private CustomerDto mapToDto(Customer entity) {
        return CustomerDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .age(entity.getAge())
                .dateOfBirth(entity.getDateOfBirth())
                .build();
    }

    private CustomerDetailDto mapToDetailDto(Customer entity) {
        return CustomerDetailDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .age(entity.getAge())
                .dateOfBirth(entity.getDateOfBirth())
                .createdAt(entity.getCreatedAt())
                .estimatedLifeExpectancy(lifeExpectancyCalculator.calculateLifeExpectancy(entity))
                .build();
    }
}