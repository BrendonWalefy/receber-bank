package com.bank.recebimentos.query.adapter.kafka;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.domain.event.NotificacaoEnviadaIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;
import com.bank.recebimentos.query.application.AtualizarBoletoReadModelUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BoletoReadModelConsumers {
    private final AtualizarBoletoReadModelUseCase useCase;

    public BoletoReadModelConsumers(AtualizarBoletoReadModelUseCase useCase) {
        this.useCase = useCase;
    }

    @KafkaListener(
        topics = "${app.kafka.topics.boleto-gerado:boleto.gerado}",
        groupId = "${spring.kafka.consumer.group-id:query-service}"
    )
    public void consumirBoletoGerado(@Payload BoletoGeradoIntegrationEvent evento) {
        useCase.registrarBoletoGerado(evento);
    }

    @KafkaListener(
        topics = "${app.kafka.topics.pagamento-efetivado:pagamento.efetivado}",
        groupId = "${spring.kafka.consumer.group-id:query-service}"
    )
    public void consumirPagamentoEfetivado(@Payload PagamentoEfetivadoIntegrationEvent evento) {
        useCase.registrarPagamentoEfetivado(evento);
    }

    @KafkaListener(
        topics = "${app.kafka.topics.pagamento-rejeitado:pagamento.rejeitado}",
        groupId = "${spring.kafka.consumer.group-id:query-service}"
    )
    public void consumirPagamentoRejeitado(@Payload PagamentoRejeitadoIntegrationEvent evento) {
        useCase.registrarPagamentoRejeitado(evento);
    }

    @KafkaListener(
        topics = "${app.kafka.topics.notificacao-enviada:notificacao.enviada}",
        groupId = "${spring.kafka.consumer.group-id:query-service}"
    )
    public void consumirNotificacaoEnviada(@Payload NotificacaoEnviadaIntegrationEvent evento) {
        useCase.registrarNotificacaoEnviada(evento);
    }
}
