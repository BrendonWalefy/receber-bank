package com.bank.recebimentos.boleto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarBoletoResponse {
    private final String boletoId;
    private final String codigoBarras;
    private final String linhaDigitavel;
    private final BigDecimal valor;
    private final LocalDate vencimento;
    private final String status;

    public CriarBoletoResponse(String boletoId, String codigoBarras, String linhaDigitavel,
                               BigDecimal valor, LocalDate vencimento, String status) {
        this.boletoId = boletoId;
        this.codigoBarras = codigoBarras;
        this.linhaDigitavel = linhaDigitavel;
        this.valor = valor;
        this.vencimento = vencimento;
        this.status = status;
    }

    public String getBoletoId() {
        return boletoId;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public String getStatus() {
        return status;
    }
}
