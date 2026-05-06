package com.bank.recebimentos.boleto.application;

import com.bank.recebimentos.boleto.domain.*;
import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;
import com.bank.recebimentos.boleto.application.dto.CriarBoletoCommand;
import com.bank.recebimentos.boleto.application.port.BoletoEventPublisher;
import com.bank.recebimentos.domain.DomainEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class CriarBoletoUseCase {
    private final BoletoRepository boletoRepository;
    private final BoletoEventPublisher boletoEventPublisher;

    public CriarBoletoUseCase(BoletoRepository boletoRepository, BoletoEventPublisher boletoEventPublisher) {
        this.boletoRepository = boletoRepository;
        this.boletoEventPublisher = boletoEventPublisher;
    }

    public String executar(CriarBoletoCommand comando) {
        validarComando(comando);

        Beneficiario beneficiario = new Beneficiario(
            comando.getBeneficiarioCpfCnpj(),
            comando.getBeneficiarioNome(),
            comando.getBeneficiarioBanco()
        );

        Pagador pagador = new Pagador(
            comando.getPagadorCpfCnpj(),
            comando.getPagadorNome(),
            comando.getPagadorEndereco(),
            comando.getPagadorCep()
        );

        Moeda valor = Moeda.brl(comando.getValor());

        Boleto boleto = Boleto.criar(
            beneficiario,
            pagador,
            valor,
            comando.getVencimento(),
            comando.getDescricao()
        );

        boletoRepository.salvar(boleto);

        for (DomainEvent evento : boleto.getEventos()) {
            if (evento instanceof BoletoGeradoEvent) {
                boletoEventPublisher.publicarBoletoGerado((BoletoGeradoEvent) evento);
            }
        }

        boleto.limparEventos();

        return boleto.getBoletoId().getValue().toString();
    }

    private void validarComando(CriarBoletoCommand comando) {
        if (comando == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }

        if (comando.getValor() == null || comando.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        if (comando.getVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
    }
}
