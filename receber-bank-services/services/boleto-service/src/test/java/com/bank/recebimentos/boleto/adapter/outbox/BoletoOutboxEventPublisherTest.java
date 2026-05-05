package com.bank.recebimentos.boleto.adapter.outbox;

import com.bank.recebimentos.boleto.domain.Beneficiario;
import com.bank.recebimentos.boleto.domain.BoletoId;
import com.bank.recebimentos.boleto.domain.CodigoBarras;
import com.bank.recebimentos.boleto.domain.LinhaDigitavel;
import com.bank.recebimentos.boleto.domain.Moeda;
import com.bank.recebimentos.boleto.domain.Pagador;
import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoletoOutboxEventPublisherTest {
    @Test
    void deveConverterBoletoGeradoParaEventoPendenteNaOutbox() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        BoletoOutboxEventPublisher publisher = new BoletoOutboxEventPublisher(
            null,
            objectMapper,
            "boleto.gerado"
        );

        BoletoId boletoId = new BoletoId(UUID.randomUUID());
        BoletoGeradoEvent evento = new BoletoGeradoEvent(
            boletoId,
            new Beneficiario("12345678901", "Empresa Beneficiaria", "341"),
            new Pagador("10987654321", "Cliente Pagador", "Rua Teste, 100", "01001000"),
            Moeda.brl(new BigDecimal("99.90")),
            LocalDate.of(2026, 6, 10),
            new CodigoBarras("12345678901234567890123456789012345678901234567"),
            new LinhaDigitavel("12345678901234567890123456789012345678901234567")
        );

        OutboxEvent outboxEvent = publisher.toOutboxEvent(evento);

        assertEquals(evento.getEventId(), outboxEvent.getId());
        assertEquals(evento.getBoletoId(), outboxEvent.getAggregateId());
        assertEquals("boleto.gerado", outboxEvent.getEventType());
        assertEquals("boleto.gerado", outboxEvent.getTopic());
        assertEquals(evento.getBoletoId(), outboxEvent.getMessageKey());
        assertEquals(OutboxEventStatus.PENDING, outboxEvent.getStatus());
        assertEquals(0, outboxEvent.getAttempts());
        assertTrue(outboxEvent.getPayload().contains("\"boletoId\":\"" + evento.getBoletoId() + "\""));
        assertTrue(outboxEvent.getPayload().contains("\"valor\":99.90"));
    }
}
