package com.bank.recebimentos.notification.application;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnviarNotificacaoUseCaseTest {
    @Test
    void deveGerarEventoDeNotificacaoParaBoletoGerado() {
        EnviarNotificacaoUseCase useCase = new EnviarNotificacaoUseCase();
        String boletoId = UUID.randomUUID().toString();

        var notificacao = useCase.enviarBoletoGerado(new BoletoGeradoIntegrationEvent(
            UUID.randomUUID().toString(),
            boletoId,
            "12345678901",
            "Empresa Beneficiaria",
            "341",
            "10987654321",
            "Cliente Pagador",
            new BigDecimal("120.50"),
            LocalDate.now().plusDays(1),
            "12345678901234567890123456789012345678901234567",
            "12345678901234567890123456789012345678901234567"
        ));

        assertEquals(boletoId, notificacao.boletoId());
        assertEquals("BOLETO_GERADO", notificacao.tipo());
        assertEquals("pagador:10987654321", notificacao.destinatario());
        assertNotNull(notificacao.eventId());
        assertNotNull(notificacao.enviadaEm());
    }

    @Test
    void deveGerarEventoDeNotificacaoEnviada() {
        EnviarNotificacaoUseCase useCase = new EnviarNotificacaoUseCase();
        String boletoId = UUID.randomUUID().toString();

        var notificacao = useCase.enviar(new PagamentoEfetivadoIntegrationEvent(
            UUID.randomUUID().toString(),
            boletoId,
            new BigDecimal("120.50"),
            Instant.now(),
            "SIMULADO"
        ));

        assertEquals(boletoId, notificacao.boletoId());
        assertEquals("PAGAMENTO_EFETIVADO", notificacao.tipo());
        assertEquals("pagador:" + boletoId, notificacao.destinatario());
        assertNotNull(notificacao.eventId());
        assertNotNull(notificacao.enviadaEm());
    }
}
