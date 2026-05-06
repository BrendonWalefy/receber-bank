package com.bank.recebimentos.boleto.domain;

import java.util.Objects;

public final class LinhaDigitavel {
    private static final int TAMANHO_LINHA_DIGITAVEL = 47;
    private final String valor;

    public LinhaDigitavel(String valor) {
        Objects.requireNonNull(valor, "Linha digitável não pode ser nula");

        if (valor.length() != TAMANHO_LINHA_DIGITAVEL) {
            throw new IllegalArgumentException(
                "Linha digitável deve ter " + TAMANHO_LINHA_DIGITAVEL + " dígitos, recebido: " + valor.length()
            );
        }

        if (!valor.matches("\\d+")) {
            throw new IllegalArgumentException("Linha digitável deve conter apenas dígitos");
        }

        this.valor = valor;
    }

    public static LinhaDigitavel gerar(CodigoBarras codigoBarras) {
        return new LinhaDigitavel(codigoBarras.getValor());
    }

    public String getValor() {
        return valor;
    }

    public String getFormatada() {
        if (valor.length() != TAMANHO_LINHA_DIGITAVEL) {
            return valor;
        }
        return valor.substring(0, 5) + "." + valor.substring(5, 10) + " " +
               valor.substring(10, 15) + "." + valor.substring(15, 20) + " " +
               valor.substring(20, 25) + "." + valor.substring(25, 30) + " " +
               valor.substring(30, 31) + " " + valor.substring(31);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinhaDigitavel that = (LinhaDigitavel) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return getFormatada();
    }
}
