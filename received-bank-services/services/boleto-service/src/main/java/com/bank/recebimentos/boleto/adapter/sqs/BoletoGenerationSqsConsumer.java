package com.bank.recebimentos.boleto.adapter.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bank.recebimentos.boleto.application.CriarBoletoUseCase;
import com.bank.recebimentos.boleto.application.dto.CriarBoletoCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
public class BoletoGenerationSqsConsumer {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final CriarBoletoUseCase criarBoletoUseCase;
    private final String queueUrl;
    private final boolean enabled;

    public BoletoGenerationSqsConsumer(SqsClient sqsClient,
                                       ObjectMapper objectMapper,
                                       CriarBoletoUseCase criarBoletoUseCase,
                                       @Value("${app.sqs.boleto-generation-queue-url:}") String queueUrl,
                                       @Value("${app.sqs.enabled:false}") boolean enabled) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.criarBoletoUseCase = criarBoletoUseCase;
        this.queueUrl = queueUrl;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${app.sqs.poll-delay-ms:5000}")
    public void poll() {
        if (!enabled || queueUrl == null || queueUrl.isBlank()) {
            return;
        }

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(10)
            .visibilityTimeout(60)
            .build();

        for (Message message : sqsClient.receiveMessage(request).messages()) {
            process(message);
        }
    }

    private void process(Message message) {
        try {
            CriarBoletoQueueMessage payload = objectMapper.readValue(message.body(), CriarBoletoQueueMessage.class);
            criarBoletoUseCase.executar(toCommand(payload));
            delete(message);
        } catch (Exception exception) {
            // A mensagem permanece na fila para retry e segue para DLQ apos maxReceiveCount.
        }
    }

    private CriarBoletoCommand toCommand(CriarBoletoQueueMessage payload) {
        return new CriarBoletoCommand(
            payload.getBeneficiarioCpfCnpj(),
            payload.getBeneficiarioNome(),
            payload.getBeneficiarioBanco(),
            payload.getPagadorCpfCnpj(),
            payload.getPagadorNome(),
            payload.getPagadorEndereco(),
            payload.getPagadorCep(),
            payload.getValor(),
            payload.getVencimento(),
            payload.getDescricao()
        );
    }

    private void delete(Message message) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
            .queueUrl(queueUrl)
            .receiptHandle(message.receiptHandle())
            .build());
    }
}
