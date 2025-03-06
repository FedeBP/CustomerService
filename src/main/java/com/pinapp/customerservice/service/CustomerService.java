package com.pinapp.customerservice.service;

import com.pinapp.customerservice.dto.CustomerDetailDto;
import com.pinapp.customerservice.dto.CustomerDto;
import com.pinapp.customerservice.dto.CustomerMetricsDto;

import java.util.List;

public interface CustomerService {

    CustomerDto createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerById(Long id);

    List<CustomerDetailDto> getAllCustomersWithLifeExpectancy();

    CustomerMetricsDto calculateMetrics();

    CustomerDto updateCustomer(Long id, CustomerDto customerDto);

    void deleteCustomer(Long id);
}