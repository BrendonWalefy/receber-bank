package com.bank.recebimentos.boleto.adapter.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxKafkaPublisher {
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int maxAttempts;

    public OutboxKafkaPublisher(OutboxEventRepository repository,
                                KafkaTemplate<String, String> kafkaTemplate,
                                @Value("${app.outbox.max-attempts:5}") int maxAttempts) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
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
            kafkaTemplate.send(evento.getTopic(), evento.getMessageKey(), evento.getPayload())
                .get(10, TimeUnit.SECONDS);
            evento.marcarPublicado();
        } catch (Exception exception) {
            evento.registrarFalha(exception, maxAttempts);
        }
    }
}
