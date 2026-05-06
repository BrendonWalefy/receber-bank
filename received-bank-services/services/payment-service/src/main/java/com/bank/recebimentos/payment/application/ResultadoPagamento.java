package com.bank.recebimentos.payment.application;

import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;

public record ResultadoPagamento(
    PagamentoEfetivadoIntegrationEvent efetivado,
    PagamentoRejeitadoIntegrationEvent rejeitado
) {
    public static ResultadoPagamento efetivado(PagamentoEfetivadoIntegrationEvent evento) {
        return new ResultadoPagamento(evento, null);
    }

    public static ResultadoPagamento rejeitado(PagamentoRejeitadoIntegrationEvent evento) {
        return new ResultadoPagamento(null, evento);
    }

    public boolean aprovado() {
        return efetivado != null;
    }
}
