package com.bank.recebimentos.payment.adapter.http;

import com.bank.recebimentos.payment.application.EfetivarPagamentoUseCase;
import com.bank.recebimentos.payment.application.ResultadoPagamento;
import com.bank.recebimentos.payment.application.SimularPagamentoCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/simulacoes/pagamentos")
public class PagamentoSimuladoController {
    private final EfetivarPagamentoUseCase useCase;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String pagamentoEfetivadoTopic;
    private final String pagamentoRejeitadoTopic;

    public PagamentoSimuladoController(EfetivarPagamentoUseCase useCase,
                                       KafkaTemplate<String, Object> kafkaTemplate,
                                       @Value("${app.kafka.topics.pagamento-efetivado:pagamento.efetivado}")
                                       String pagamentoEfetivadoTopic,
                                       @Value("${app.kafka.topics.pagamento-rejeitado:pagamento.rejeitado}")
                                       String pagamentoRejeitadoTopic) {
        this.useCase = useCase;
        this.kafkaTemplate = kafkaTemplate;
        this.pagamentoEfetivadoTopic = pagamentoEfetivadoTopic;
        this.pagamentoRejeitadoTopic = pagamentoRejeitadoTopic;
    }

    @PostMapping
    public ResponseEntity<PagamentoSimuladoResponse> simular(@RequestBody PagamentoSimuladoRequest request) {
        ResultadoPagamento resultado = useCase.efetivar(new SimularPagamentoCommand(
            request.boletoId(),
            request.valorPago(),
            request.recebidoEm() == null ? Instant.now() : request.recebidoEm(),
            request.canal() == null || request.canal().isBlank() ? "SIMULADOR_CLIENTE" : request.canal()
        ));

        if (resultado.aprovado()) {
            var evento = resultado.efetivado();
            kafkaTemplate.send(pagamentoEfetivadoTopic, evento.boletoId(), evento);
            return ResponseEntity.accepted().body(new PagamentoSimuladoResponse(
                evento.boletoId(),
                "PAGAMENTO_EFETIVADO",
                evento.valorPago(),
                evento.pagoEm(),
                evento.canal(),
                null
            ));
        }

        var evento = resultado.rejeitado();
        kafkaTemplate.send(pagamentoRejeitadoTopic, evento.boletoId(), evento);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new PagamentoSimuladoResponse(
            evento.boletoId(),
            "PAGAMENTO_REJEITADO",
            evento.valorPago(),
            evento.recebidoEm(),
            evento.canal(),
            evento.motivo()
        ));
    }
}
