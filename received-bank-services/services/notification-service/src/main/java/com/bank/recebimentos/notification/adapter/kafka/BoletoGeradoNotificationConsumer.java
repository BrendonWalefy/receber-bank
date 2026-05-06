package com.bank.recebimentos.notification.adapter.kafka;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.notification.application.EnviarNotificacaoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BoletoGeradoNotificationConsumer {
    private final EnviarNotificacaoUseCase enviarNotificacaoUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String notificacaoEnviadaTopic;

    public BoletoGeradoNotificationConsumer(EnviarNotificacaoUseCase enviarNotificacaoUseCase,
                                            KafkaTemplate<String, Object> kafkaTemplate,
                                            @Value("${app.kafka.topics.notificacao-enviada:notificacao.enviada}")
                                            String notificacaoEnviadaTopic) {
        this.enviarNotificacaoUseCase = enviarNotificacaoUseCase;
        this.kafkaTemplate = kafkaTemplate;
        this.notificacaoEnviadaTopic = notificacaoEnviadaTopic;
    }

    @KafkaListener(
        topics = "${app.kafka.topics.boleto-gerado:boleto.gerado}",
        groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void consumir(@Payload BoletoGeradoIntegrationEvent evento) {
        var notificacao = enviarNotificacaoUseCase.enviarBoletoGerado(evento);
        kafkaTemplate.send(notificacaoEnviadaTopic, notificacao.boletoId(), notificacao);
    }
}
