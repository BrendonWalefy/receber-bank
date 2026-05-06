package com.bank.recebimentos.boleto.domain.events;

import com.bank.recebimentos.boleto.domain.*;
import com.bank.recebimentos.domain.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BoletoGeradoEvent extends DomainEvent {
    private final String boletoId;
    private final String beneficiarioCpfCnpj;
    private final String beneficiarioNome;
    private final String beneficiarioBanco;
    private final String pagadorCpfCnpj;
    private final String pagadorNome;
    private final BigDecimal valor;
    private final LocalDate vencimento;
    private final String codigoBarras;
    private final String linhaDigitavel;

    public BoletoGeradoEvent(BoletoId boletoId, Beneficiario beneficiario, Pagador pagador,
                             Moeda valor, LocalDate vencimento, CodigoBarras codigoBarras,
                             LinhaDigitavel linhaDigitavel) {
        this.boletoId = boletoId.getValue().toString();
        this.beneficiarioCpfCnpj = beneficiario.getCpfCnpj();
        this.beneficiarioNome = beneficiario.getNome();
        this.beneficiarioBanco = beneficiario.getBanco();
        this.pagadorCpfCnpj = pagador.getCpfCnpj();
        this.pagadorNome = pagador.getNome();
        this.valor = valor.getValor();
        this.vencimento = vencimento;
        this.codigoBarras = codigoBarras.getValor();
        this.linhaDigitavel = linhaDigitavel.getValor();
    }

    @Override
    public String getEventType() {
        return "boleto.gerado";
    }

    public String getBoletoId() {
        return boletoId;
    }

    public String getBeneficiarioCpfCnpj() {
        return beneficiarioCpfCnpj;
    }

    public String getBeneficiarioNome() {
        return beneficiarioNome;
    }

    public String getBeneficiarioBanco() {
        return beneficiarioBanco;
    }

    public String getPagadorCpfCnpj() {
        return pagadorCpfCnpj;
    }

    public String getPagadorNome() {
        return pagadorNome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }
}
