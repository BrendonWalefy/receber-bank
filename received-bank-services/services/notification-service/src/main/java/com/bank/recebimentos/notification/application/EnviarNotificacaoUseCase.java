package com.bank.recebimentos.notification.application;

import com.bank.recebimentos.domain.event.NotificacaoEnviadaIntegrationEvent;
import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class EnviarNotificacaoUseCase {
    public NotificacaoEnviadaIntegrationEvent enviarBoletoGerado(BoletoGeradoIntegrationEvent evento) {
        return new NotificacaoEnviadaIntegrationEvent(
            UUID.randomUUID().toString(),
            evento.boletoId(),
            "pagador:" + evento.pagadorCpfCnpj(),
            "BOLETO_GERADO",
            Instant.now()
        );
    }

    public NotificacaoEnviadaIntegrationEvent enviar(PagamentoEfetivadoIntegrationEvent evento) {
        return new NotificacaoEnviadaIntegrationEvent(
            UUID.randomUUID().toString(),
            evento.boletoId(),
            "pagador:" + evento.boletoId(),
            "PAGAMENTO_EFETIVADO",
            Instant.now()
        );
    }

    public NotificacaoEnviadaIntegrationEvent enviarPagamentoRejeitado(PagamentoRejeitadoIntegrationEvent evento) {
        return new NotificacaoEnviadaIntegrationEvent(
            UUID.randomUUID().toString(),
            evento.boletoId(),
            "pagador:" + evento.boletoId(),
            "PAGAMENTO_REJEITADO:" + evento.motivo(),
            Instant.now()
        );
    }
}
