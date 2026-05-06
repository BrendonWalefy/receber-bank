package com.bank.recebimentos.payment.application;

import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EfetivarPagamentoUseCaseTest {
    @Test
    void deveGerarEventoDePagamentoEfetivado() {
        var repository = new FakeBoletoAguardandoPagamentoRepository();
        EfetivarPagamentoUseCase useCase = new EfetivarPagamentoUseCase(repository);
        String boletoId = UUID.randomUUID().toString();

        useCase.registrarBoletoAguardandoPagamento(new BoletoGeradoIntegrationEvent(
            UUID.randomUUID().toString(),
            boletoId,
            "12345678901",
            "Empresa Beneficiaria",
            "341",
            "10987654321",
            "Cliente Pagador",
            new BigDecimal("120.50"),
            LocalDate.now().plusDays(1),
            "12345678901234567890123456789012345678901234567",
            "12345678901234567890123456789012345678901234567"
        ));

        var resultado = useCase.efetivar(new SimularPagamentoCommand(
            boletoId,
            new BigDecimal("120.50"),
            Instant.now(),
            "SIMULADOR_CLIENTE"
        ));

        assertTrue(resultado.aprovado());
        var pagamento = resultado.efetivado();
        assertEquals(boletoId, pagamento.boletoId());
        assertEquals(new BigDecimal("120.50"), pagamento.valorPago());
        assertEquals("SIMULADOR_CLIENTE", pagamento.canal());
        assertNotNull(pagamento.eventId());
        assertNotNull(pagamento.pagoEm());
    }

    @Test
    void deveRejeitarPagamentoComValorDiferente() {
        var repository = new FakeBoletoAguardandoPagamentoRepository();
        EfetivarPagamentoUseCase useCase = new EfetivarPagamentoUseCase(repository);
        String boletoId = UUID.randomUUID().toString();

        useCase.registrarBoletoAguardandoPagamento(new BoletoGeradoIntegrationEvent(
            UUID.randomUUID().toString(),
            boletoId,
            "12345678901",
            "Empresa Beneficiaria",
            "341",
            "10987654321",
            "Cliente Pagador",
            new BigDecimal("120.50"),
            LocalDate.now().plusDays(1),
            "12345678901234567890123456789012345678901234567",
            "12345678901234567890123456789012345678901234567"
        ));

        var resultado = useCase.efetivar(new SimularPagamentoCommand(
            boletoId,
            new BigDecimal("100.00"),
            Instant.now(),
            "SIMULADOR_CLIENTE"
        ));

        assertEquals("VALOR_PAGO_DIFERENTE_DO_VALOR_DO_BOLETO", resultado.rejeitado().motivo());
    }

    private static class FakeBoletoAguardandoPagamentoRepository implements BoletoAguardandoPagamentoRepository {
        private BoletoGeradoIntegrationEvent boleto;

        @Override
        public void salvar(BoletoGeradoIntegrationEvent boleto) {
            this.boleto = boleto;
        }

        @Override
        public java.util.Optional<BoletoGeradoIntegrationEvent> buscarPorId(String boletoId) {
            if (boleto != null && boleto.boletoId().equals(boletoId)) {
                return java.util.Optional.of(boleto);
            }
            return java.util.Optional.empty();
        }
    }
}
