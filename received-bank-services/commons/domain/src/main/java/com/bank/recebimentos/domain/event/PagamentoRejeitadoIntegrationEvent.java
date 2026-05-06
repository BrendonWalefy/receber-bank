package com.bank.recebimentos.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoRejeitadoIntegrationEvent(
    String eventId,
    String boletoId,
    BigDecimal valorPago,
    Instant recebidoEm,
    String canal,
    String motivo
) {
}
