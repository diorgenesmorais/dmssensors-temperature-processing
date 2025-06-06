package com.dms.dmssensors.temperature.processing.api.controller;

import com.dms.dmssensors.temperature.processing.api.model.TemperatureLogOutput;
import com.dms.dmssensors.temperature.processing.common.IdGenerator;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

import static com.dms.dmssensors.temperature.processing.infrastructure.rabbitmq.RabbitMQConfig.EXCHANGE_NAME;

@RestController
@RequestMapping("/api/sensors/{sensorId}/temperatures/data")
@Slf4j
@RequiredArgsConstructor
public class TemperatureProcessingController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public void processTemperatureData(@PathVariable TSID sensorId, @RequestBody String input) {
        if (input == null || input.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        double temperatureValue;
        try {
            temperatureValue = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid temperature value");
        }

        TemperatureLogOutput payload = TemperatureLogOutput.builder()
                .id(IdGenerator.generateTimeBaseUUID())
                .sensorId(sensorId)
                .registeredAt(OffsetDateTime.now())
                .value(temperatureValue)
                .build();

        log.info(payload.toString());

        String routingKey = "";

        MessagePostProcessor messagePostProcessor =message -> {
            message.getMessageProperties().setHeader("sensorId", payload.getSensorId().toString());
            return message;
        };

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, payload, messagePostProcessor);

    }
}
