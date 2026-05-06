package com.bank.recebimentos.notification.adapter.kafka;

import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;
import com.bank.recebimentos.notification.application.EnviarNotificacaoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PagamentoRejeitadoConsumer {
    private final EnviarNotificacaoUseCase enviarNotificacaoUseCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String notificacaoEnviadaTopic;

    public PagamentoRejeitadoConsumer(EnviarNotificacaoUseCase enviarNotificacaoUseCase,
                                      KafkaTemplate<String, Object> kafkaTemplate,
                                      @Value("${app.kafka.topics.notificacao-enviada:notificacao.enviada}")
                                      String notificacaoEnviadaTopic) {
        this.enviarNotificacaoUseCase = enviarNotificacaoUseCase;
        this.kafkaTemplate = kafkaTemplate;
        this.notificacaoEnviadaTopic = notificacaoEnviadaTopic;
    }

    @KafkaListener(
        topics = "${app.kafka.topics.pagamento-rejeitado:pagamento.rejeitado}",
        groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void consumir(@Payload PagamentoRejeitadoIntegrationEvent evento) {
        var notificacao = enviarNotificacaoUseCase.enviarPagamentoRejeitado(evento);
        kafkaTemplate.send(notificacaoEnviadaTopic, notificacao.boletoId(), notificacao);
    }
}
