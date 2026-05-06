package com.bank.recebimentos.boleto.adapter.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "boletos")
public class BoletoJpaEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "codigo_barras", unique = true, nullable = false)
    private String codigoBarras;

    @Column(name = "linha_digitavel", unique = true, nullable = false)
    private String linhaDigitavel;

    @Column(name = "beneficiario_cpf_cnpj", nullable = false)
    private String beneficiarioCpfCnpj;

    @Column(name = "beneficiario_nome", nullable = false)
    private String beneficiarioNome;

    @Column(name = "beneficiario_banco", nullable = false)
    private String beneficiarioBanco;

    @Column(name = "pagador_cpf_cnpj", nullable = false)
    private String pagadorCpfCnpj;

    @Column(name = "pagador_nome", nullable = false)
    private String pagadorNome;

    @Column(name = "pagador_endereco", nullable = false)
    private String pagadorEndereco;

    @Column(name = "pagador_cep", nullable = false)
    private String pagadorCep;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "vencimento", nullable = false)
    private LocalDate vencimento;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "numero_sequencial")
    private String numeroSequencial;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }

    public void setNumeroSequencial(String numeroSequencial) {
        this.numeroSequencial = numeroSequencial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
