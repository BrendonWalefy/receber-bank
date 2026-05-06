package com.bank.recebimentos.boleto.adapter.http;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarBoletoRequest {
    @NotBlank
    @Pattern(regexp = "\\d{11,14}")
    private String beneficiarioCpfCnpj;

    @NotBlank
    private String beneficiarioNome;

    @NotBlank
    @Pattern(regexp = "\\d{3}")
    private String beneficiarioBanco;

    @NotBlank
    @Pattern(regexp = "\\d{11,14}")
    private String pagadorCpfCnpj;

    @NotBlank
    private String pagadorNome;

    @NotBlank
    private String pagadorEndereco;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    private String pagadorCep;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valor;

    @NotNull
    @FutureOrPresent
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
