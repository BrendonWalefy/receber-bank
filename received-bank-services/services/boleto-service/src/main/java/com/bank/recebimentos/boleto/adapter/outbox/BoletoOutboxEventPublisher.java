package com.bank.recebimentos.boleto.adapter.outbox;

import com.bank.recebimentos.boleto.application.port.BoletoEventPublisher;
import com.bank.recebimentos.boleto.domain.events.BoletoGeradoEvent;
import com.bank.recebimentos.domain.event.BoletoGeradoIntegrationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BoletoOutboxEventPublisher implements BoletoEventPublisher {
    private static final String BOLETO_GERADO_EVENT_TYPE = "boleto.gerado";

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;
    private final String boletoGeradoTopic;

    public BoletoOutboxEventPublisher(OutboxEventRepository repository,
                                      ObjectMapper objectMapper,
                                      @Value("${app.kafka.topics.boleto-gerado:boleto.gerado}") String boletoGeradoTopic) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.boletoGeradoTopic = boletoGeradoTopic;
    }

    @Override
    public void publicarBoletoGerado(BoletoGeradoEvent evento) {
        repository.save(toOutboxEvent(evento));
    }

    OutboxEvent toOutboxEvent(BoletoGeradoEvent evento) {
        BoletoGeradoIntegrationEvent integrationEvent = toIntegrationEvent(evento);
        return OutboxEvent.pending(
            evento.getEventId(),
            evento.getBoletoId(),
            BOLETO_GERADO_EVENT_TYPE,
            boletoGeradoTopic,
            evento.getBoletoId(),
            toJson(integrationEvent)
        );
    }

    private String toJson(BoletoGeradoIntegrationEvent evento) {
        try {
            return objectMapper.writeValueAsString(evento);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar evento boleto.gerado", exception);
        }
    }

    private BoletoGeradoIntegrationEvent toIntegrationEvent(BoletoGeradoEvent evento) {
        return new BoletoGeradoIntegrationEvent(
            evento.getEventId(),
            evento.getBoletoId(),
            evento.getBeneficiarioCpfCnpj(),
            evento.getBeneficiarioNome(),
            evento.getBeneficiarioBanco(),
            evento.getPagadorCpfCnpj(),
            evento.getPagadorNome(),
            evento.getValor(),
            evento.getVencimento(),
            evento.getCodigoBarras(),
            evento.getLinhaDigitavel()
        );
    }
}
