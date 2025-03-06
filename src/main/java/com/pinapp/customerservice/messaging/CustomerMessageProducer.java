package com.pinapp.customerservice.messaging;

import com.pinapp.customerservice.config.RabbitMQConfig;
import com.pinapp.customerservice.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCustomerCreationMessage(CustomerDto customerDto) {
        log.info("Sending customer creation message for customer ID: {}", customerDto.getId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                RabbitMQConfig.CUSTOMER_CREATED_ROUTING_KEY,
                customerDto
        );

        log.info("Customer creation message sent successfully");
    }
}