package com.bank.recebimentos.query.application;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.bank.recebimentos.domain.event.NotificacaoEnviadaIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoEfetivadoIntegrationEvent;
import com.bank.recebimentos.domain.event.PagamentoRejeitadoIntegrationEvent;
import com.bank.recebimentos.query.adapter.persistence.BoletoReadModel;
import com.bank.recebimentos.query.adapter.persistence.BoletoReadModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AtualizarBoletoReadModelUseCase {
    private final BoletoReadModelRepository repository;

    public AtualizarBoletoReadModelUseCase(BoletoReadModelRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void registrarBoletoGerado(BoletoGeradoIntegrationEvent evento) {
        BoletoReadModel readModel = repository.findById(UUID.fromString(evento.boletoId()))
            .orElseGet(BoletoReadModel::new);

        readModel.setBoletoId(UUID.fromString(evento.boletoId()));
        readModel.setBeneficiarioCpfCnpj(evento.beneficiarioCpfCnpj());
        readModel.setBeneficiarioNome(evento.beneficiarioNome());
        readModel.setPagadorCpfCnpj(evento.pagadorCpfCnpj());
        readModel.setPagadorNome(evento.pagadorNome());
        readModel.setValor(evento.valor());
        readModel.setVencimento(evento.vencimento());
        readModel.setCodigoBarras(evento.codigoBarras());
        readModel.setLinhaDigitavel(evento.linhaDigitavel());
        readModel.setStatus("GERADO");
        readModel.setUpdatedAt(Instant.now());

        repository.save(readModel);
    }

    @Transactional
    public void registrarPagamentoEfetivado(PagamentoEfetivadoIntegrationEvent evento) {
        repository.findById(UUID.fromString(evento.boletoId())).ifPresent(readModel -> {
            readModel.setStatus("PAGO");
            readModel.setPagoEm(evento.pagoEm());
            readModel.setUpdatedAt(Instant.now());
            repository.save(readModel);
        });
    }

    @Transactional
    public void registrarPagamentoRejeitado(PagamentoRejeitadoIntegrationEvent evento) {
        repository.findById(UUID.fromString(evento.boletoId())).ifPresent(readModel -> {
            readModel.setStatus("PAGAMENTO_REJEITADO:" + evento.motivo());
            readModel.setUpdatedAt(Instant.now());
            repository.save(readModel);
        });
    }

    @Transactional
    public void registrarNotificacaoEnviada(NotificacaoEnviadaIntegrationEvent evento) {
        repository.findById(UUID.fromString(evento.boletoId())).ifPresent(readModel -> {
            readModel.setNotificacaoEnviadaEm(evento.enviadaEm());
            readModel.setUpdatedAt(Instant.now());
            repository.save(readModel);
        });
    }
}
