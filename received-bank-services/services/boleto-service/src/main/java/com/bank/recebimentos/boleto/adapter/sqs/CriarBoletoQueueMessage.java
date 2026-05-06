package com.bank.recebimentos.boleto.adapter.sqs;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarBoletoQueueMessage {
    private String beneficiarioCpfCnpj;
    private String beneficiarioNome;
    private String beneficiarioBanco;
    private String pagadorCpfCnpj;
    private String pagadorNome;
    private String pagadorEndereco;
    private String pagadorCep;
    private BigDecimal valor;
    private LocalDate vencimento;
    private String descricao;

    public String getBeneficiarioCpfCnpj() {
        return beneficiarioCpfCnpj;
    }

    public void setBeneficiarioCpfCnpj(String beneficiarioCpfCnpj) {
        this.beneficiarioCpfCnpj = beneficiarioCpfCnpj;
    }

    public String getBeneficiarioNome() {
        return beneficiarioNome;
    }

    public void setBeneficiarioNome(String beneficiarioNome) {
        this.beneficiarioNome = beneficiarioNome;
    }

    public String getBeneficiarioBanco() {
        return beneficiarioBanco;
    }

    public void setBeneficiarioBanco(String beneficiarioBanco) {
        this.beneficiarioBanco = beneficiarioBanco;
    }

    public String getPagadorCpfCnpj() {
        return pagadorCpfCnpj;
    }

    public void setPagadorCpfCnpj(String pagadorCpfCnpj) {
        this.pagadorCpfCnpj = pagadorCpfCnpj;
    }

    public String getPagadorNome() {
        return pagadorNome;
    }

    public void setPagadorNome(String pagadorNome) {
        this.pagadorNome = pagadorNome;
    }

    public String getPagadorEndereco() {
        return pagadorEndereco;
    }

    public void setPagadorEndereco(String pagadorEndereco) {
        this.pagadorEndereco = pagadorEndereco;
    }

    public String getPagadorCep() {
        return pagadorCep;
    }

    public void setPagadorCep(String pagadorCep) {
        this.pagadorCep = pagadorCep;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
