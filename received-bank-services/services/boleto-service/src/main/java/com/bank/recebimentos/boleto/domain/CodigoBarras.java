package com.bank.recebimentos.boleto.domain;

import java.util.Objects;

public final class CodigoBarras {
    private static final int TAMANHO_CODIGO_BARRAS = 47;
    private final String valor;

    public CodigoBarras(String valor) {
        Objects.requireNonNull(valor, "Código de barras não pode ser nulo");

        if (valor.length() != TAMANHO_CODIGO_BARRAS) {
            throw new IllegalArgumentException(
                "Código de barras deve ter " + TAMANHO_CODIGO_BARRAS + " dígitos, recebido: " + valor.length()
            );
        }

        if (!valor.matches("\\d+")) {
            throw new IllegalArgumentException("Código de barras deve conter apenas dígitos");
        }

        this.valor = valor;
    }

    public static CodigoBarras gerar(String bancoNumero, String nossoNumero, String beneficiarioCpfCnpj) {
        String base = (nossoNumero + beneficiarioCpfCnpj).replaceAll("\\D", "");
        String campoLivre = String.format("%42s", base).replace(' ', '0');
        String codigoSemDv = bancoNumero.substring(0, 3) + "9" + campoLivre.substring(0, 42);

        int dv = calcularDigitoVerificador(codigoSemDv);
        String codigo = codigoSemDv.substring(0, 4) + dv + codigoSemDv.substring(4);

        return new CodigoBarras(codigo);
    }

    private static int calcularDigitoVerificador(String codigo) {
        int sequencia = 2;
        int soma = 0;

        for (int i = codigo.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(codigo.charAt(i));
            int resultado = digito * sequencia;

            if (resultado > 9) {
                resultado = resultado / 10 + resultado % 10;
            }

            soma += resultado;
            sequencia++;

            if (sequencia > 9) {
                sequencia = 2;
            }
        }

        int dv = 11 - (soma % 11);
        return dv == 10 || dv == 11 ? 0 : dv;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodigoBarras that = (CodigoBarras) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
