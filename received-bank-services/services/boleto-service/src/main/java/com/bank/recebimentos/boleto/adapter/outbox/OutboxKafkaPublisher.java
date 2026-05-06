package com.bank.recebimentos.boleto.adapter.outbox;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxKafkaPublisher {
    private static final String BOLETO_GERADO_EVENT_TYPE = "boleto.gerado";

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final int maxAttempts;

    public OutboxKafkaPublisher(OutboxEventRepository repository,
                                KafkaTemplate<String, Object> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${app.outbox.max-attempts:5}") int maxAttempts) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
    }

    @Scheduled(fixedDelayString = "${app.outbox.publish-delay-ms:5000}")
    @Transactional
    public void publicarPendentes() {
        repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus.PENDING)
            .forEach(this::publicar);
    }

    private void publicar(OutboxEvent evento) {
        try {
            kafkaTemplate.send(evento.getTopic(), evento.getMessageKey(), toKafkaPayload(evento))
                .get(10, TimeUnit.SECONDS);
            evento.marcarPublicado();
        } catch (Exception exception) {
            evento.registrarFalha(exception, maxAttempts);
        }
    }

    private Object toKafkaPayload(OutboxEvent evento) {
        if (BOLETO_GERADO_EVENT_TYPE.equals(evento.getEventType())) {
            return readPayload(evento, BoletoGeradoIntegrationEvent.class);
        }
        throw new IllegalArgumentException("Tipo de evento nao suportado para publicacao Kafka: " + evento.getEventType());
    }

    private <T> T readPayload(OutboxEvent evento, Class<T> payloadType) {
        try {
            return objectMapper.readValue(evento.getPayload(), payloadType);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel desserializar payload da outbox: " + evento.getId(), exception);
        }
    }
}
