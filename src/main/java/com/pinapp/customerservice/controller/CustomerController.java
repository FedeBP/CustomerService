package com.pinapp.customerservice.controller;

import com.pinapp.customerservice.dto.CustomerDetailDto;
import com.pinapp.customerservice.dto.CustomerDto;
import com.pinapp.customerservice.dto.CustomerMetricsDto;
import com.pinapp.customerservice.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "API for customer operations")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer",
            description = "Creates a new customer with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody CustomerDto customerDto) {
        log.info("Received request to create customer: {}", customerDto);
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID",
            description = "Returns customer details for the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDto> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Received request to get customer with ID: {}", id);
        CustomerDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "Get all customers",
            description = "Returns all customers with their details including life expectancy")
    public ResponseEntity<List<CustomerDetailDto>> getAllCustomers() {
        log.info("Received request to get all customers");
        List<CustomerDetailDto> customers = customerService.getAllCustomersWithLifeExpectancy();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get customer metrics",
            description = "Returns metrics about customers including average age and standard deviation")
    public ResponseEntity<CustomerMetricsDto> getCustomerMetrics() {
        log.info("Received request to get customer metrics");
        CustomerMetricsDto metrics = customerService.calculateMetrics();
        return ResponseEntity.ok(metrics);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer",
            description = "Updates an existing customer with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CustomerDto> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto) {
        log.info("Received request to update customer with ID: {}", id);
        CustomerDto updatedCustomer = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer",
            description = "Deletes the customer with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("Received request to delete customer with ID: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}