package com.bank.recebimentos.boleto.domain;

public enum BoletoStatus {
    GERADO("Boleto gerado"),
    ENVIADO_CIP("Enviado para CIP"),
    PAGO("Boleto pago"),
    CANCELADO("Boleto cancelado");

    private final String descricao;

    BoletoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
