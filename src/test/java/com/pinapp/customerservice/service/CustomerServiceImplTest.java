package com.pinapp.customerservice.service;


import com.pinapp.customerservice.dto.CustomerDetailDto;
import com.pinapp.customerservice.dto.CustomerDto;
import com.pinapp.customerservice.dto.CustomerMetricsDto;
import com.pinapp.customerservice.entity.Customer;
import com.pinapp.customerservice.exception.ResourceNotFoundException;
import com.pinapp.customerservice.messaging.CustomerMessageProducer;
import com.pinapp.customerservice.repository.CustomerRepository;
import com.pinapp.customerservice.service.impl.CustomerServiceImpl;
import com.pinapp.customerservice.util.LifeExpectancyCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMessageProducer messageProducer;

    @Mock
    private LifeExpectancyCalculator lifeExpectancyCalculator;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerDto customerDto;
    private Customer customer;
    private List<Customer> customerList;

    @BeforeEach
    void setUp() {
        // Initialize test data
        customerDto = CustomerDto.builder()
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .createdAt(LocalDate.now())
                .build();

        Customer customer2 = Customer.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .age(25)
                .dateOfBirth(LocalDate.of(1998, 1, 1))
                .createdAt(LocalDate.now())
                .build();

        customerList = Arrays.asList(customer, customer2);
    }

    @Test
    void createCustomer_ShouldReturnSavedCustomer() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerDto result = customerService.createCustomer(customerDto);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getFirstName(), result.getFirstName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals(customer.getAge(), result.getAge());
        assertEquals(customer.getDateOfBirth(), result.getDateOfBirth());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(messageProducer, times(1)).sendCustomerCreationMessage(any(CustomerDto.class));
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        CustomerDto result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getFirstName(), result.getFirstName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(99L));
        verify(customerRepository, times(1)).findById(99L);
    }

    @Test
    void getAllCustomersWithLifeExpectancy_ShouldReturnAllCustomers() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusYears(50);
        when(customerRepository.findAll()).thenReturn(customerList);
        when(lifeExpectancyCalculator.calculateLifeExpectancy(any(Customer.class))).thenReturn(futureDate);

        // Act
        List<CustomerDetailDto> result = customerService.getAllCustomersWithLifeExpectancy();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(customer.getId(), result.get(0).getId());
        assertEquals(futureDate, result.get(0).getEstimatedLifeExpectancy());
        verify(customerRepository, times(1)).findAll();
        verify(lifeExpectancyCalculator, times(2)).calculateLifeExpectancy(any(Customer.class));
    }

    @Test
    void calculateMetrics_WithCustomers_ShouldReturnMetrics() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(customerList);
        when(customerRepository.findAverageAge()).thenReturn(27.5);
        when(customerRepository.findAgeStandardDeviation()).thenReturn(3.5);

        // Act
        CustomerMetricsDto result = customerService.calculateMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(27.5, result.getAverageAge());
        assertEquals(3.5, result.getAgeStandardDeviation());
        assertEquals(2L, result.getTotalCustomers());
        assertEquals(25, result.getYoungestCustomerAge());
        assertEquals(30, result.getOldestCustomerAge());

        verify(customerRepository, times(1)).findAverageAge();
        verify(customerRepository, times(1)).findAgeStandardDeviation();
    }

    @Test
    void calculateMetrics_WithNoCustomers_ShouldReturnEmptyMetrics() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(List.of());

        // Act
        CustomerMetricsDto result = customerService.calculateMetrics();

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getAverageAge());
        assertEquals(0.0, result.getAgeStandardDeviation());
        assertEquals(0L, result.getTotalCustomers());
        assertEquals(0, result.getYoungestCustomerAge());
        assertEquals(0, result.getOldestCustomerAge());
    }

    @Test
    void updateCustomer_WhenCustomerExists_ShouldReturnUpdatedCustomer() {
        // Arrange
        CustomerDto updateDto = CustomerDto.builder()
                .firstName("John")
                .lastName("Updated")
                .age(31)
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .build();

        Customer updatedCustomer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Updated")
                .age(31)
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .createdAt(LocalDate.now())
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerDto result = customerService.updateCustomer(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedCustomer.getId(), result.getId());
        assertEquals(updatedCustomer.getLastName(), result.getLastName());
        assertEquals(updatedCustomer.getAge(), result.getAge());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_WhenCustomerDoesNotExist_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(99L, customerDto));
        verify(customerRepository, times(1)).findById(99L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_WhenCustomerExists_ShouldDeleteCustomer() {
        // Arrange
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCustomer_WhenCustomerDoesNotExist_ShouldThrowException() {
        // Arrange
        when(customerRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(99L));
        verify(customerRepository, times(1)).existsById(99L);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}