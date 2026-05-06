package com.bank.recebimentos.payment.adapter.http;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoSimuladoRequest(
    String boletoId,
    BigDecimal valorPago,
    Instant recebidoEm,
    String canal
) {
}
