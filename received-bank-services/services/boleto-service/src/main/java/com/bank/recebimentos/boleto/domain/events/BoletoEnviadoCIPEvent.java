package com.bank.recebimentos.boleto.domain.events;

import com.bank.recebimentos.boleto.domain.BoletoId;
import com.bank.recebimentos.domain.DomainEvent;

public class BoletoEnviadoCIPEvent extends DomainEvent {
    private final String boletoId;
    private final String numeroSequencial;

    public BoletoEnviadoCIPEvent(BoletoId boletoId, String numeroSequencial) {
        this.boletoId = boletoId.getValue().toString();
        this.numeroSequencial = numeroSequencial;
    }

    @Override
    public String getEventType() {
        return "boleto.enviado-cip";
    }

    public String getBoletoId() {
        return boletoId;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }
}
