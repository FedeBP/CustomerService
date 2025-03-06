package com.pinapp.customerservice.messaging;

import com.pinapp.customerservice.config.RabbitMQConfig;
import com.pinapp.customerservice.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMessageConsumer {

    /**
     * This method processes customer creation messages.
     * In a real-world scenario, you might:
     * - Send welcome emails
     * - Update analytics systems
     * - Trigger integrations with other services
     * - Generate reports
     * - Update cache systems
     */
    @RabbitListener(queues = RabbitMQConfig.CUSTOMER_CREATED_QUEUE)
    public void processCustomerCreation(CustomerDto customerDto) {
        log.info("Received customer creation message for processing: {}", customerDto);

        // Simulate processing time
        try {
            log.info("Processing customer data asynchronously...");
            Thread.sleep(1000); // Simulating some async work

            // Example of what we might do here:
            // 1. Send welcome email
            log.info("Sending welcome email to customer: {} {}",
                    customerDto.getFirstName(), customerDto.getLastName());

            // 2. Update analytics
            log.info("Updating analytics with new customer information");

            // 3. Trigger any integrations
            log.info("Notifying other systems about new customer");

            log.info("Customer created message processed successfully");
        } catch (InterruptedException e) {
            log.error("Error processing customer creation message", e);
            Thread.currentThread().interrupt();
        }
    }
}