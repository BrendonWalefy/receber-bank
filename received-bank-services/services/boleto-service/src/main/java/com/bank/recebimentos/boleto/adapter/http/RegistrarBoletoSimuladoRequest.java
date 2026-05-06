package com.bank.recebimentos.boleto.adapter.http;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarBoletoSimuladoRequest(
    String boletoId,
    String codigoBarras,
    String linhaDigitavel,
    BigDecimal valor,
    LocalDate vencimento
) {
}
