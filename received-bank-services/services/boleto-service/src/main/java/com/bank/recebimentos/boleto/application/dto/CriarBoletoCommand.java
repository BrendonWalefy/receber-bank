package com.bank.recebimentos.boleto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarBoletoCommand {
    private final String beneficiarioCpfCnpj;
    private final String beneficiarioNome;
    private final String beneficiarioBanco;
    private final String pagadorCpfCnpj;
    private final String pagadorNome;
    private final String pagadorEndereco;
    private final String pagadorCep;
    private final BigDecimal valor;
    private final LocalDate vencimento;
    private final String descricao;

    public CriarBoletoCommand(String beneficiarioCpfCnpj, String beneficiarioNome, String beneficiarioBanco,
                              String pagadorCpfCnpj, String pagadorNome, String pagadorEndereco,
                              String pagadorCep, BigDecimal valor, LocalDate vencimento, String descricao) {
        this.beneficiarioCpfCnpj = beneficiarioCpfCnpj;
        this.beneficiarioNome = beneficiarioNome;
        this.beneficiarioBanco = beneficiarioBanco;
        this.pagadorCpfCnpj = pagadorCpfCnpj;
        this.pagadorNome = pagadorNome;
        this.pagadorEndereco = pagadorEndereco;
        this.pagadorCep = pagadorCep;
        this.valor = valor;
        this.vencimento = vencimento;
        this.descricao = descricao;
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

    public String getPagadorEndereco() {
        return pagadorEndereco;
    }

    public String getPagadorCep() {
        return pagadorCep;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public String getDescricao() {
        return descricao;
    }
}
