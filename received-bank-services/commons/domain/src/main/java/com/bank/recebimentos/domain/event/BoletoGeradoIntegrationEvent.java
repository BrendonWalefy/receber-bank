package com.bank.recebimentos.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoGeradoIntegrationEvent(
    String eventId,
    String boletoId,
    String beneficiarioCpfCnpj,
    String beneficiarioNome,
    String beneficiarioBanco,
    String pagadorCpfCnpj,
    String pagadorNome,
    BigDecimal valor,
    LocalDate vencimento,
    String codigoBarras,
    String linhaDigitavel
) {
}
