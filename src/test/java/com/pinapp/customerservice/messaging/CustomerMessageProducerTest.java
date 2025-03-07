package com.pinapp.customerservice.messaging;

import com.pinapp.customerservice.config.RabbitMQConfig;
import com.pinapp.customerservice.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerMessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CustomerMessageProducer messageProducer;

    @Test
    void sendCustomerCreationMessage_ShouldSendMessageToRabbitMQ() {
        // Arrange
        CustomerDto customerDto = CustomerDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .dateOfBirth(LocalDate.of(1993, 1, 1))
                .build();

        // Act
        messageProducer.sendCustomerCreationMessage(customerDto);

        // Assert
        verify(rabbitTemplate, times(1)).convertAndSend(
                RabbitMQConfig.CUSTOMER_EXCHANGE,
                RabbitMQConfig.CUSTOMER_CREATED_ROUTING_KEY,
                customerDto
        );
    }
}