package com.bank.recebimentos.domain.event;

import java.time.Instant;

public record NotificacaoEnviadaIntegrationEvent(
    String eventId,
    String boletoId,
    String destinatario,
    String tipo,
    Instant enviadaEm
) {
}
