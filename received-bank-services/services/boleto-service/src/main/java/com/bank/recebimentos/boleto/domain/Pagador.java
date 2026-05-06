package com.bank.recebimentos.boleto.domain;

import java.util.Objects;

public final class Pagador {
    private final String cpfCnpj;
    private final String nome;
    private final String endereco;
    private final String cep;

    public Pagador(String cpfCnpj, String nome, String endereco, String cep) {
        Objects.requireNonNull(cpfCnpj, "CPF/CNPJ do pagador não pode ser nulo");
        Objects.requireNonNull(nome, "Nome do pagador não pode ser nulo");
        Objects.requireNonNull(endereco, "Endereço do pagador não pode ser nulo");
        Objects.requireNonNull(cep, "CEP do pagador não pode ser nulo");

        if (cpfCnpj.isBlank() || !cpfCnpj.matches("\\d{11,14}")) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome do pagador não pode ser vazio");
        }
        if (endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço não pode ser vazio");
        }
        if (cep.isBlank() || !cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos");
        }

        this.cpfCnpj = cpfCnpj;
        this.nome = nome;
        this.endereco = endereco;
        this.cep = cep;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCep() {
        return cep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagador pagador = (Pagador) o;
        return Objects.equals(cpfCnpj, pagador.cpfCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpfCnpj);
    }
}
