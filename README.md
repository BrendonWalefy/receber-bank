# Received Bank

Sistema distribuído de recebimentos bancários construído com Java 21 e Spring Boot, aplicando padrões modernos de arquitetura de software em um contexto real de domínio financeiro.

O projeto nasceu do interesse em entender como patterns como DDD, Hexagonal Architecture, CQRS e Transactional Outbox funcionam em conjunto — não de forma isolada, mas colaborando para resolver problemas reais de consistência, escalabilidade e rastreabilidade em sistemas de pagamento.

---

## Stacks e Tecnologias

| Camada | Tecnologias |
|---|---|
| **Linguagem & Runtime** | Java 21 (virtual threads), Spring Boot 3.2, Spring Cloud |
| **Mensageria** | Apache Kafka via Redpanda (local) / Amazon MSK (cloud) |
| **Bancos de Dados** | PostgreSQL 16 (escrita + Outbox), Redis 7 (idempotência) |
| **Infraestrutura Cloud** | AWS EKS, API Gateway, SQS, SNS, S3, CloudFront, WAF, RDS, ElastiCache |
| **Infraestrutura como Código** | Terraform (provisionamento AWS), Kubernetes (manifests EKS) |
| **Observabilidade** | Spring Actuator, health checks por serviço |
| **Build & CI** | Maven (multi-module), GitHub Actions |

---

## Padrões e Metodologias

| Padrão | Como se aplica |
|---|---|
| **DDD** | Cada microsserviço corresponde a um bounded context: `boleto`, `payment`, `query`, `notification`. Entidades e regras de negócio vivem na camada `domain`, isoladas do framework. |
| **Arquitetura Hexagonal** | Camadas `domain → application → adapter`. Use cases dependem apenas de interfaces de porta; adapters (HTTP, Kafka, JPA) são plugáveis e substituíveis. |
| **CQRS** | `boleto-service` é o modelo de escrita; `query-service` mantém um read model próprio, atualizado por eventos Kafka. Nenhum serviço lê o banco do outro. |
| **Transactional Outbox** | Boleto e evento `boleto.gerado` são gravados na mesma transação ACID no PostgreSQL. O `outbox-worker` publica no MSK de forma independente, garantindo consistência banco/evento sem dual write. |
| **Event-Driven Architecture** | Amazon MSK (Kafka) desacopla todos os domínios. Serviços publicam e consomem eventos sem chamadas síncronas entre si. |
| **Resiliência** | SQS absorve picos na entrada (API Gateway → SQS → boleto-service). DLQ isola mensagens com falha. Kafka DLT por consumer com retry e backoff configurável. |
| **Idempotência** | `Idempotency-Key` validada no ElastiCache Redis antes de processar cada mensagem, prevenindo duplicatas em reprocessamentos. |
| **Segurança em Camadas** | CloudFront → AWS WAF → API Gateway → IAM/IRSA por service account no EKS → Secrets Manager para credenciais. |

Decisões de design detalhadas estão documentadas em [`received-bank-services/docs/ARCHITECTURE_DECISION_RECORD.md`](received-bank-services/docs/ARCHITECTURE_DECISION_RECORD.md).

---

## Visão Geral dos Serviços

O projeto é organizado como um conjunto de microsserviços Maven em `received-bank-services`:

| Módulo | Porta local | Responsabilidade |
|---|---|---|
| `boleto-service` | `8081` | Cria e consulta boletos no modelo de escrita, aplica regras de domínio e publica eventos via Transactional Outbox. |
| `query-service` | `8082` | Mantém e expõe o read model de boletos para consultas. |
| `payment-service` | `8083` | Consome boletos gerados e simula/efetiva pagamentos, publicando `pagamento.efetivado`. |
| `notification-service` | `8084` | Consome eventos de boleto e pagamento, envia notificações e publica `notificacao.enviada`. |

Infraestrutura local:

- PostgreSQL 16
- Redis 7
- Redpanda (compatível com Kafka)
- Docker Compose para subir dependências e serviços

---

## Documentação

A documentação navegável do projeto fica em [`docs/index.html`](docs/index.html) e é publicada pelo GitHub Pages via workflow [`pages.yml`](.github/workflows/pages.yml).

As coleções de API estão em [`docs/api`](docs/api): Postman e Insomnia prontos para uso.


---

## Como Rodar

Entre no módulo principal:

```bash
cd received-bank-services
```

Compile e rode os testes:

```bash
./mvnw clean test
```

Suba a stack local:

```bash
docker compose up --build
```

---

## Endpoints e Jornada

**Endpoints principais:**

- `POST http://localhost:8081/boletos`
- `GET  http://localhost:8081/boletos/{id}`
- `POST http://localhost:8081/simulacoes/registradora/boletos`
- `POST http://localhost:8083/simulacoes/pagamentos`
- `GET  http://localhost:8082/consultas/boletos`
- `GET  http://localhost:8082/consultas/boletos/{boletoId}`

**Fluxo completo:**

1. O Cliente PJ solicita a criação do boleto no `boleto-service`.
2. O boleto é persistido e publicado como evento `boleto.gerado` via Outbox.
3. A registradora externa pode ser simulada via `/simulacoes/registradora/boletos`.
4. O `payment-service` aguarda um estímulo externo de pagamento em `/simulacoes/pagamentos`.
5. O pagamento é validado por valor e vencimento.
6. Pagamentos válidos publicam `pagamento.efetivado`; inválidos publicam `pagamento.rejeitado`.
7. O `query-service` atualiza o read model e o `notification-service` emite notificações do resultado.

**Health checks:**

- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`
- `http://localhost:8083/actuator/health`
- `http://localhost:8084/actuator/health`

---

## Configuração do Repositório Remoto

```bash
git remote add origin https://github.com/SEU_USUARIO/received-bank.git
git push -u origin main
```

---

## Workflow Git

Desenvolvimento e commits acontecem na branch `develop`. A branch `main` integra apenas código vindo de `develop`, após os testes locais passarem e o fluxo principal da API ser validado.
