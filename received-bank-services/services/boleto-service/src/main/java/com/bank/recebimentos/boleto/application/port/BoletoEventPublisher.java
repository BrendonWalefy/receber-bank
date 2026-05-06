package com.bank.recebimentos.boleto.application.port;

import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;

public interface BoletoEventPublisher {
    void publicarBoletoGerado(BoletoGeradoEvent evento);
}
