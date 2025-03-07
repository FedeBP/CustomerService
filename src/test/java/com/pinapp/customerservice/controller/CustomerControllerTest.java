package com.pinapp.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinapp.customerservice.dto.CustomerDetailDto;
import com.pinapp.customerservice.dto.CustomerDto;
import com.pinapp.customerservice.dto.CustomerMetricsDto;
import com.pinapp.customerservice.exception.ResourceNotFoundException;
import com.pinapp.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerDto customerDto;
    private CustomerDetailDto customerDetailDto;
    private CustomerMetricsDto customerMetricsDto;

    @BeforeEach
    void setUp() {
        // Initialize test data
        customerDto = CustomerDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .build();

        customerDetailDto = CustomerDetailDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .createdAt(LocalDate.now())
                .estimatedLifeExpectancy(LocalDate.now().plusYears(50))
                .build();

        customerMetricsDto = CustomerMetricsDto.builder()
                .averageAge(30.0)
                .ageStandardDeviation(5.0)
                .totalCustomers(1L)
                .youngestCustomerAge(30)
                .oldestCustomerAge(30)
                .build();
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        CustomerDto inputDto = CustomerDto.builder()
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .build();

        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(customerDto);

        mockMvc.perform(post("/api/customers")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.age", is(30)));

        verify(customerService, times(1)).createCustomer(any(CustomerDto.class));
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customerDto);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new ResourceNotFoundException("Customer not found with ID: 99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(99L);
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() throws Exception {
        List<CustomerDetailDto> customers = List.of(customerDetailDto);
        when(customerService.getAllCustomersWithLifeExpectancy()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].estimatedLifeExpectancy", notNullValue()));

        verify(customerService, times(1)).getAllCustomersWithLifeExpectancy();
    }

    @Test
    void getCustomerMetrics_ShouldReturnMetrics() throws Exception {
        when(customerService.calculateMetrics()).thenReturn(customerMetricsDto);

        mockMvc.perform(get("/api/customers/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageAge", is(30.0)))
                .andExpect(jsonPath("$.ageStandardDeviation", is(5.0)))
                .andExpect(jsonPath("$.totalCustomers", is(1)))
                .andExpect(jsonPath("$.youngestCustomerAge", is(30)))
                .andExpect(jsonPath("$.oldestCustomerAge", is(30)));

        verify(customerService, times(1)).calculateMetrics();
    }

    @Test
    void updateCustomer_WhenCustomerExists_ShouldReturnUpdatedCustomer() throws Exception {
        CustomerDto updateDto = CustomerDto.builder()
                .firstName("John")
                .lastName("Updated")
                .age(31)
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .build();

        CustomerDto updatedCustomerDto = CustomerDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Updated")
                .age(31)
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .build();

        when(customerService.updateCustomer(eq(1L), any(CustomerDto.class))).thenReturn(updatedCustomerDto);

        mockMvc.perform(put("/api/customers/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.lastName", is("Updated")))
                .andExpect(jsonPath("$.age", is(31)));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDto.class));
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    void createCustomer_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CustomerDto invalidDto = CustomerDto.builder()
                // Missing required fields
                .build();

        mockMvc.perform(post("/api/customers")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any(CustomerDto.class));
    }
}