package com.pinapp.customerservice.util;

import com.pinapp.customerservice.entity.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LifeExpectancyCalculator {

    // Average life expectancy (simplified model)
    private static final int AVERAGE_LIFE_EXPECTANCY = 80;

    // Regional and lifestyle factors could be added for more complex calculations

    /**
     * Calculates the estimated date of end of life based on a simple algorithm.
     * In a real-world scenario, this would include more factors like:
     * - Gender
     * - Country/region
     * - Health status
     * - Lifestyle factors
     * - Family medical history
     */
    public LocalDate calculateLifeExpectancy(Customer customer) {
        int currentAge = customer.getAge();
        int estimatedRemainingYears = AVERAGE_LIFE_EXPECTANCY - currentAge;

        // Ensure we don't return a date in the past for very old customers
        if (estimatedRemainingYears <= 0) {
            estimatedRemainingYears = 1;
        }

        return LocalDate.now().plusYears(estimatedRemainingYears);
    }
}