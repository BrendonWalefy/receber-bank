# Architecture Decision Record — Receber Bank

Decisões arquiteturais relevantes do projeto, registradas para facilitar revisões e entrevistas técnicas.

---

## ADR-001 · Transactional Outbox vs Saga para publicação de eventos

**Status:** Aceito

**Contexto**

Ao criar um boleto, o sistema precisa persistir o estado no banco de dados **e** publicar um evento `boleto.gerado` no Kafka (MSK). A pergunta central é: como garantir que os dois aconteçam de forma consistente, sem perder eventos ou gerar duplicatas em caso de falha?

**Alternativas consideradas**

| Alternativa | Descrição | Problema |
|---|---|---|
| **Dual write simples** | Salva no DB, depois publica no Kafka | Se o processo cair entre os dois passos, o evento se perde. Não há garantia de "at-least-once delivery" sem lógica extra. |
| **Saga Coreografada** | Cada serviço reage a eventos e publica os seus, sem orquestrador central | Adequada para fluxos multi-serviço longos. Para a **geração do boleto** é overhead desnecessário — temos um único ponto de escrita. |
| **Saga Orquestrada** | Um orquestrador central coordena os passos via comandos | Introduz acoplamento ao orquestrador e complexidade operacional não justificada para o escopo atual. |
| **Transactional Outbox** ✓ | Evento é gravado na tabela `outbox_events` **na mesma transação ACID** que salva o boleto. Um worker independente lê e publica. | Solução escolhida. |

**Decisão**

Adotar o **Transactional Outbox Pattern** no `boleto-service`.

O boleto e o registro em `outbox_events` são gravados em uma única transação PostgreSQL. O `outbox-worker` (scheduler periódico) lê os eventos pendentes e os publica no MSK, marcando-os como publicados.

```
BEGIN
  INSERT INTO boletos (...)
  INSERT INTO outbox_events (aggregate_id, type, payload, status='PENDING')
COMMIT

-- worker independente:
SELECT * FROM outbox_events WHERE status = 'PENDING'
→ kafka.send(event)
→ UPDATE outbox_events SET status = 'PUBLISHED'
```

**Consequências**

- Garantia de "at-least-once delivery": se o worker falhar antes de marcar como publicado, ele reprocessa. Consumidores devem ser idempotentes (o `Idempotency-Key` no Redis cobre isso no `boleto-service`).
- Nenhuma dependência de transações distribuídas (2PC) ou framework de Saga.
- O `outbox_events` cresce e precisa de limpeza periódica — aceitável para o escopo atual.

---

## ADR-002 · CQRS com bancos físicos compartilhados em ambientes diferentes

**Status:** Aceito

**Contexto**

O CQRS separa o modelo de escrita (comandos) do modelo de leitura (consultas). A dúvida é se os modelos devem usar o mesmo banco físico ou bancos separados.

**Decisão**

- **Local (Docker Compose):** banco único PostgreSQL, schemas separados por serviço. Facilita o desenvolvimento.
- **AWS (produção):** mesma instância RDS, mas o `boleto-service` escreve em seu schema e o `query-service` mantém seu próprio read model atualizado via eventos Kafka — sem leitura direta do schema de escrita.

Isso preserva o isolamento lógico do CQRS sem o custo operacional de dois clusters RDS em ambiente de demonstração.

**Consequências**

- Em produção real com alto volume, separar fisicamente os bancos é o próximo passo natural.
- O read model pode eventualmente ficar dessincronizado em cenários de falha do `query-service` — o Kafka DLT garante que eventos não se percam e o reprocessamento é possível.

---

## ADR-003 · Entrada assíncrona via SQS (API Gateway → SQS direto)

**Status:** Aceito

**Contexto**

A criação de boleto é uma operação que pode receber picos de volume. Colocar o `boleto-service` diretamente exposto ao API Gateway seria um gargalo e exigiria autoescaling síncrono.

**Decisão**

O API Gateway tem uma **integração direta com o SQS** (sem Lambda intermediário). A API retorna `202 Accepted` imediatamente, e o `boleto-service` consome do SQS via long polling.

```
Cliente → API Gateway → SQS boleto-generation → boleto-service (EKS)
                              ↓ (após retries)
                         SQS DLQ (falhas isoladas)
```

**Consequências**

- O SQS absorve picos: o `boleto-service` processa no seu próprio ritmo.
- O cliente não recebe o boleto na resposta síncrona — o status é consultável via `query-service` (CQRS).
- A `Idempotency-Key` no header garante que reprocessamentos de mensagens SQS duplicadas não criem boletos duplicados.

---

## ADR-004 · Arquitetura Hexagonal (Ports & Adapters)

**Status:** Aceito

**Contexto**

Cada microsserviço precisa ser testável em isolamento e sem dependência direta de frameworks ou infraestrutura.

**Decisão**

Cada serviço segue a estrutura:

```
domain/       ← entidades, value objects, eventos de domínio, interfaces de repositório
application/  ← use cases (CriarBoletoUseCase, EfetivarPagamentoUseCase etc.)
adapter/
  http/       ← controllers REST (Spring MVC)
  kafka/      ← producers e consumers (Spring Kafka)
  persistence/← implementações JPA dos repositórios
  memory/     ← implementações in-memory para testes
```

A camada `domain` não conhece Spring, JPA nem Kafka. Os use cases dependem apenas de interfaces definidas no próprio domínio.

**Consequências**

- Testes unitários nos use cases rodam sem contexto Spring, sem banco, sem Kafka.
- Trocar PostgreSQL por outro banco exige apenas uma nova implementação de adapter.
- Maior cerimônia inicial de código, justificada pelo escopo de demonstração de boas práticas.
