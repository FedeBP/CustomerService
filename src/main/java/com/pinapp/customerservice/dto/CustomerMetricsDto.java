package com.pinapp.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMetricsDto {
    private Double averageAge;
    private Double ageStandardDeviation;
    private Long totalCustomers;
    private Integer youngestCustomerAge;
    private Integer oldestCustomerAge;
}