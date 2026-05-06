package com.bank.recebimentos.payment.application;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class EfetivarPagamentoUseCase {
    private final BoletoAguardandoPagamentoRepository boletoRepository;

    public EfetivarPagamentoUseCase(BoletoAguardandoPagamentoRepository boletoRepository) {
        this.boletoRepository = boletoRepository;
    }

    public void registrarBoletoAguardandoPagamento(BoletoGeradoIntegrationEvent evento) {
        boletoRepository.salvar(evento);
    }

    public ResultadoPagamento efetivar(SimularPagamentoCommand comando) {
        if (comando == null || comando.boletoId() == null || comando.boletoId().isBlank()) {
            throw new IllegalArgumentException("Boleto é obrigatório");
        }

        Instant recebidoEm = comando.recebidoEm() == null ? Instant.now() : comando.recebidoEm();
        String canal = comando.canal() == null || comando.canal().isBlank() ? "SIMULADOR_CLIENTE" : comando.canal();

        BoletoGeradoIntegrationEvent boleto = boletoRepository.buscarPorId(comando.boletoId())
            .orElseThrow(() -> new IllegalArgumentException("Boleto não encontrado para pagamento"));

        if (comando.valorPago() == null) {
            return rejeitar(comando, recebidoEm, canal, "VALOR_PAGO_OBRIGATORIO");
        }

        if (comando.valorPago().compareTo(boleto.valor()) != 0) {
            return rejeitar(comando, recebidoEm, canal, "VALOR_PAGO_DIFERENTE_DO_VALOR_DO_BOLETO");
        }

        var dataPagamento = recebidoEm.atZone(ZoneOffset.UTC).toLocalDate();
        if (dataPagamento.isAfter(boleto.vencimento())) {
            return rejeitar(comando, recebidoEm, canal, "BOLETO_VENCIDO");
        }

        return ResultadoPagamento.efetivado(new PagamentoEfetivadoIntegrationEvent(
            UUID.randomUUID().toString(),
            boleto.boletoId(),
            comando.valorPago(),
            recebidoEm,
            canal
        ));
    }

    private ResultadoPagamento rejeitar(SimularPagamentoCommand comando, Instant recebidoEm, String canal, String motivo) {
        return ResultadoPagamento.rejeitado(new PagamentoRejeitadoIntegrationEvent(
            UUID.randomUUID().toString(),
            comando.boletoId(),
            comando.valorPago(),
            recebidoEm,
            canal,
            motivo
        ));
    }
}
