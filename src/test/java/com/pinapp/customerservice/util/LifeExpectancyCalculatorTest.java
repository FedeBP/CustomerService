package com.pinapp.customerservice.util;

import com.pinapp.customerservice.entity.Customer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LifeExpectancyCalculatorTest {

    private final LifeExpectancyCalculator calculator = new LifeExpectancyCalculator();

    @Test
    void calculateLifeExpectancy_ForYoungCustomer_ShouldReturnFutureDate() {
        // Arrange
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.now().minusYears(30))
                .build();

        // Act
        LocalDate result = calculator.calculateLifeExpectancy(customer);

        // Assert
        // Average life expectancy is 80, so for a 30-year-old, expect around 50 years more
        long yearsLeft = ChronoUnit.YEARS.between(LocalDate.now(), result);
        assertTrue(yearsLeft >= 49 && yearsLeft <= 51, "Expected around 50 years remaining");
    }

    @Test
    void calculateLifeExpectancy_ForVeryOldCustomer_ShouldReturnAtLeastOneYearMore() {
        // Arrange
        Customer customer = Customer.builder()
                .firstName("Elder")
                .lastName("Person")
                .age(90)
                .dateOfBirth(LocalDate.now().minusYears(90))
                .build();

        // Act
        LocalDate result = calculator.calculateLifeExpectancy(customer);

        // Assert
        // For someone older than the average life expectancy, should return at least 1 year
        assertTrue(result.isAfter(LocalDate.now()), "Should return a future date");
        long yearsLeft = ChronoUnit.YEARS.between(LocalDate.now(), result);
        assertEquals(1, yearsLeft, "Expected at least one year remaining");
    }
}