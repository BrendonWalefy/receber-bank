package com.bank.recebimentos.payment.application;

import java.math.BigDecimal;
import java.time.Instant;

public record SimularPagamentoCommand(
    String boletoId,
    BigDecimal valorPago,
    Instant recebidoEm,
    String canal
) {
}
