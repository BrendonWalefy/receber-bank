package com.bank.recebimentos.payment.adapter.kafka;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.payment.application.EfetivarPagamentoUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BoletoGeradoConsumer {
    private final EfetivarPagamentoUseCase efetivarPagamentoUseCase;

    public BoletoGeradoConsumer(EfetivarPagamentoUseCase efetivarPagamentoUseCase) {
        this.efetivarPagamentoUseCase = efetivarPagamentoUseCase;
    }

    @KafkaListener(
        topics = "${app.kafka.topics.boleto-gerado:boleto.gerado}",
        groupId = "${spring.kafka.consumer.group-id:payment-service}"
    )
    public void consumir(@Payload BoletoGeradoIntegrationEvent evento) {
        efetivarPagamentoUseCase.registrarBoletoAguardandoPagamento(evento);
    }
}
