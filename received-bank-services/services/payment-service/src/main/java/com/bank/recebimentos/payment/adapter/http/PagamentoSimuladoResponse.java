package com.bank.recebimentos.payment.adapter.http;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoSimuladoResponse(
    String boletoId,
    String status,
    BigDecimal valorPago,
    Instant recebidoEm,
    String canal,
    String motivo
) {
}
