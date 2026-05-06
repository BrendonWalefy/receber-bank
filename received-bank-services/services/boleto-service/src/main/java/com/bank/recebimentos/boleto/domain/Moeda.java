package com.bank.recebimentos.boleto.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Moeda {
    private final BigDecimal valor;
    private final String codigoIso;

    public Moeda(BigDecimal valor, String codigoIso) {
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(codigoIso, "Código ISO não pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        if (!codigoIso.equals("BRL")) {
            throw new IllegalArgumentException("Apenas BRL é suportado");
        }

        this.valor = valor.setScale(2, java.math.RoundingMode.HALF_UP);
        this.codigoIso = codigoIso;
    }

    public static Moeda brl(BigDecimal valor) {
        return new Moeda(valor, "BRL");
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getCodigoIso() {
        return codigoIso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Moeda moeda = (Moeda) o;
        return Objects.equals(valor, moeda.valor) && Objects.equals(codigoIso, moeda.codigoIso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, codigoIso);
    }

    @Override
    public String toString() {
        return codigoIso + " " + valor;
    }
}
