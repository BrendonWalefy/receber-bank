package com.bank.recebimentos.boleto.adapter.http;

import java.time.Instant;

public record RegistrarBoletoSimuladoResponse(
    String boletoId,
    String numeroRegistro,
    String status,
    Instant registradoEm
) {
}
