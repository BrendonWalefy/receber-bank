package com.bank.recebimentos.payment.application;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;

import java.util.Optional;

public interface BoletoAguardandoPagamentoRepository {
    void salvar(BoletoGeradoIntegrationEvent boleto);

    Optional<BoletoGeradoIntegrationEvent> buscarPorId(String boletoId);
}
