package com.bank.recebimentos.boleto.domain;

import java.util.Objects;

public final class Beneficiario {
    private final String cpfCnpj;
    private final String nome;
    private final String banco;

    public Beneficiario(String cpfCnpj, String nome, String banco) {
        Objects.requireNonNull(cpfCnpj, "CPF/CNPJ do beneficiário não pode ser nulo");
        Objects.requireNonNull(nome, "Nome do beneficiário não pode ser nulo");
        Objects.requireNonNull(banco, "Banco do beneficiário não pode ser nulo");

        if (cpfCnpj.isBlank() || !cpfCnpj.matches("\\d{11,14}")) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome do beneficiário não pode ser vazio");
        }
        if (banco.length() != 3 || !banco.matches("\\d{3}")) {
            throw new IllegalArgumentException("Código do banco deve ter 3 dígitos");
        }

        this.cpfCnpj = cpfCnpj;
        this.nome = nome;
        this.banco = banco;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getBanco() {
        return banco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiario that = (Beneficiario) o;
        return Objects.equals(cpfCnpj, that.cpfCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpfCnpj);
    }
}
