package com.bank.recebimentos.boleto.adapter.http;

public class BoletoNaoEncontradoException extends RuntimeException {
    public BoletoNaoEncontradoException(String boletoId) {
        super("Boleto não encontrado: " + boletoId);
    }
}
