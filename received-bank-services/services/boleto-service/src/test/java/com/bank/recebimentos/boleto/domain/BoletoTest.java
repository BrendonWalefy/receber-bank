package com.bank.recebimentos.boleto.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoletoTest {
    @Test
    void deveCriarBoletoGeradoComEventoDeDominio() {
        Boleto boleto = Boleto.criar(
            new Beneficiario("12345678901", "Empresa Beneficiaria", "341"),
            new Pagador("10987654321", "Cliente Pagador", "Rua Teste, 100", "01001000"),
            Moeda.brl(new BigDecimal("150.75")),
            LocalDate.now().plusDays(5),
            "Mensalidade"
        );

        assertEquals(BoletoStatus.GERADO, boleto.getStatus());
        assertEquals(new BigDecimal("150.75"), boleto.getValor().getValor());
        assertEquals(47, boleto.getCodigoBarras().getValor().length());
        assertEquals(47, boleto.getLinhaDigitavel().getValor().length());
        assertFalse(boleto.getEventos().isEmpty());
    }

    @Test
    void naoDeveCriarBoletoComVencimentoNoPassado() {
        assertThrows(IllegalArgumentException.class, () -> Boleto.criar(
            new Beneficiario("12345678901", "Empresa Beneficiaria", "341"),
            new Pagador("10987654321", "Cliente Pagador", "Rua Teste, 100", "01001000"),
            Moeda.brl(new BigDecimal("150.75")),
            LocalDate.now().minusDays(1),
            "Mensalidade"
        ));
    }
}
