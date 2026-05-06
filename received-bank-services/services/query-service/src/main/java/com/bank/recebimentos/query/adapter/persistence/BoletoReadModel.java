package com.bank.recebimentos.query.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "boleto_read_model")
public class BoletoReadModel {
    @Id
    @Column(name = "boleto_id", columnDefinition = "uuid")
    private UUID boletoId;

    @Column(name = "beneficiario_cpf_cnpj", nullable = false)
    private String beneficiarioCpfCnpj;

    @Column(name = "beneficiario_nome", nullable = false)
    private String beneficiarioNome;

    @Column(name = "pagador_cpf_cnpj", nullable = false)
    private String pagadorCpfCnpj;

    @Column(name = "pagador_nome", nullable = false)
    private String pagadorNome;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "vencimento", nullable = false)
    private LocalDate vencimento;

    @Column(name = "codigo_barras", nullable = false)
    private String codigoBarras;

    @Column(name = "linha_digitavel", nullable = false)
    private String linhaDigitavel;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "pago_em")
    private Instant pagoEm;

    @Column(name = "notificacao_enviada_em")
    private Instant notificacaoEnviadaEm;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UUID getBoletoId() {
        return boletoId;
    }

    public void setBoletoId(UUID boletoId) {
        this.boletoId = boletoId;
    }

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

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getLinhaDigitavel() {
        return linhaDigitavel;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        this.linhaDigitavel = linhaDigitavel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(Instant pagoEm) {
        this.pagoEm = pagoEm;
    }

    public Instant getNotificacaoEnviadaEm() {
        return notificacaoEnviadaEm;
    }

    public void setNotificacaoEnviadaEm(Instant notificacaoEnviadaEm) {
        this.notificacaoEnviadaEm = notificacaoEnviadaEm;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
