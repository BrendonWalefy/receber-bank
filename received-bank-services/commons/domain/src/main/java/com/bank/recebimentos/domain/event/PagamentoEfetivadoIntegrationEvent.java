package com.bank.recebimentos.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoEfetivadoIntegrationEvent(
    String eventId,
    String boletoId,
    BigDecimal valorPago,
    Instant pagoEm,
    String canal
) {
}
