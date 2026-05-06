package com.bank.recebimentos.boleto.domain;

import com.bank.recebimentos.boleto.domain.events.BoletoEnviadoCIPEvent;
import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;
import com.bank.recebimentos.domain.DomainEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Boleto {
    private final BoletoId boletoId;
    private final Beneficiario beneficiario;
    private final Pagador pagador;
    private final Moeda valor;
    private final LocalDate vencimento;
    private final CodigoBarras codigoBarras;
    private final LinhaDigitavel linhaDigitavel;
    private final String descricao;
    private BoletoStatus status;
    private String numeroSequencial;
    private final List<DomainEvent> eventos;

    private Boleto(BoletoId boletoId, Beneficiario beneficiario, Pagador pagador,
                   Moeda valor, LocalDate vencimento, CodigoBarras codigoBarras,
                   LinhaDigitavel linhaDigitavel, String descricao) {
        this.boletoId = boletoId;
        this.beneficiario = beneficiario;
        this.pagador = pagador;
        this.valor = valor;
        this.vencimento = vencimento;
        this.codigoBarras = codigoBarras;
        this.linhaDigitavel = linhaDigitavel;
        this.descricao = descricao;
        this.status = BoletoStatus.GERADO;
        this.eventos = new ArrayList<>();
    }

    public static Boleto criar(Beneficiario beneficiario, Pagador pagador, Moeda valor,
                               LocalDate vencimento, String descricao) {
        Objects.requireNonNull(beneficiario, "Beneficiário não pode ser nulo");
        Objects.requireNonNull(pagador, "Pagador não pode ser nulo");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(vencimento, "Vencimento não pode ser nulo");

        if (vencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento não pode ser no passado");
        }

        BoletoId boletoId = BoletoId.generate();
        CodigoBarras codigoBarras = CodigoBarras.gerar(
            beneficiario.getBanco(),
            boletoId.getValue().toString().replace("-", "").substring(0, 15),
            beneficiario.getCpfCnpj()
        );
        LinhaDigitavel linhaDigitavel = LinhaDigitavel.gerar(codigoBarras);

        Boleto boleto = new Boleto(boletoId, beneficiario, pagador, valor, vencimento,
                                   codigoBarras, linhaDigitavel, descricao);

        boleto.registrarEvento(new BoletoGeradoEvent(boletoId, beneficiario, pagador,
                                                      valor, vencimento, codigoBarras,
                                                      linhaDigitavel));

        return boleto;
    }

    public static Boleto reconstituir(BoletoId boletoId, Beneficiario beneficiario, Pagador pagador,
                                      Moeda valor, LocalDate vencimento, CodigoBarras codigoBarras,
                                      LinhaDigitavel linhaDigitavel, String descricao,
                                      BoletoStatus status, String numeroSequencial) {
        Objects.requireNonNull(boletoId, "Id do boleto não pode ser nulo");
        Objects.requireNonNull(status, "Status do boleto não pode ser nulo");

        Boleto boleto = new Boleto(boletoId, beneficiario, pagador, valor, vencimento,
            codigoBarras, linhaDigitavel, descricao);
        boleto.status = status;
        boleto.numeroSequencial = numeroSequencial;
        return boleto;
    }

    public void marcarEnviadoCIP(String numeroSequencial) {
        Objects.requireNonNull(numeroSequencial, "Número sequencial não pode ser nulo");

        if (this.status != BoletoStatus.GERADO) {
            throw new IllegalStateException("Boleto deve estar em estado GERADO para ser marcado como enviado");
        }

        this.numeroSequencial = numeroSequencial;
        this.status = BoletoStatus.ENVIADO_CIP;

        registrarEvento(new BoletoEnviadoCIPEvent(boletoId, numeroSequencial));
    }

    public void marcarComoPago() {
        if (this.status != BoletoStatus.ENVIADO_CIP) {
            throw new IllegalStateException("Boleto deve estar em estado ENVIADO_CIP para ser marcado como pago");
        }

        this.status = BoletoStatus.PAGO;
    }

    public void cancelar() {
        if (this.status == BoletoStatus.PAGO) {
            throw new IllegalStateException("Não é possível cancelar um boleto já pago");
        }

        this.status = BoletoStatus.CANCELADO;
    }

    private void registrarEvento(DomainEvent evento) {
        this.eventos.add(evento);
    }

    public BoletoId getBoletoId() {
        return boletoId;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public Pagador getPagador() {
        return pagador;
    }

    public Moeda getValor() {
        return valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public CodigoBarras getCodigoBarras() {
        return codigoBarras;
    }

    public LinhaDigitavel getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public BoletoStatus getStatus() {
        return status;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }

    public List<DomainEvent> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void limparEventos() {
        eventos.clear();
    }
}
