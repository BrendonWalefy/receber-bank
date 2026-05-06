package com.bank.recebimentos.payment.adapter.memory;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.payment.application.BoletoAguardandoPagamentoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryBoletoAguardandoPagamentoRepository implements BoletoAguardandoPagamentoRepository {
    private final ConcurrentMap<String, BoletoGeradoIntegrationEvent> boletos = new ConcurrentHashMap<>();

    @Override
    public void salvar(BoletoGeradoIntegrationEvent boleto) {
        boletos.put(boleto.boletoId(), boleto);
    }

    @Override
    public Optional<BoletoGeradoIntegrationEvent> buscarPorId(String boletoId) {
        return Optional.ofNullable(boletos.get(boletoId));
    }
}
