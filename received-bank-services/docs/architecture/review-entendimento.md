# Review de Entendimento - Received Bank

Este documento serve como roteiro técnico para explicar o Received Bank em uma entrevista ou apresentação. A ideia central é mostrar os porquês do projeto: ele não é apenas um CRUD de boletos, mas uma simulação de modernização de recebimentos bancários com domínio financeiro, eventos, consistência transacional, leitura otimizada e desenho cloud-native.

## 1. Contexto e objetivo

O Received Bank foi construído para demonstrar como padrões como DDD, Arquitetura Hexagonal, CQRS, Event-Driven Architecture e Transactional Outbox funcionam juntos em um cenário próximo de um sistema financeiro real.

O domínio escolhido é recebimentos bancários via boleto. Esse domínio é interessante porque tem regras de negócio claras, estados relevantes, integrações assíncronas, necessidade de rastreabilidade e sensibilidade alta a inconsistências. Em um sistema bancário, não basta salvar um boleto no banco. Também é preciso garantir que outros contextos saibam que esse boleto existe, que pagamentos sejam processados de forma confiável e que consultas reflitam a jornada do boleto ao longo do tempo.

Por isso, a arquitetura separa responsabilidades em quatro serviços:

| Serviço | Responsabilidade |
| --- | --- |
| `boleto-service` | Cria e consulta boletos no modelo de escrita, aplica regras de domínio e registra eventos na outbox. |
| `payment-service` | Consome boletos gerados, recebe webhook/retorno de pagamento vindo de PSP ou banco externo e publica eventos de pagamento efetivado ou rejeitado. |
| `query-service` | Mantém um read model próprio para consultas, atualizado por eventos Kafka. |
| `notification-service` | Reage a eventos de boleto e pagamento, simula notificações e publica `notificacao.enviada`. |

Essa separação reforça a ideia de bounded contexts: cada serviço possui uma razão de existir e evolui em torno de uma responsabilidade do negócio.

## 2. Jornada principal

A jornada principal tem dois estímulos externos distintos: o PJ Parceiro emite o boleto, e depois o Cliente Pagador paga esse boleto em um PSP/Banco externo. Esse PSP/Banco envia um webhook de pagamento pela borda segura para que o `payment-service` valide e publique o resultado.

```text
PJ Parceiro
  -> WAF / API Gateway
  -> SQS boleto-generation
  -> boleto-service
  -> PostgreSQL + outbox_events
  -> Outbox Publisher
  -> Kafka / Redpanda
  -> notification-service
  -> query-service

Cliente Pagador
  -> PSP / Banco externo
  -> WAF / API Gateway
  -> payment-service
  -> Kafka / Redpanda
  -> notification-service
  -> query-service
```

No ambiente local, o cliente chama `POST /boletos` no `boleto-service`. O serviço monta o comando de criação, executa o caso de uso `CriarBoletoUseCase`, cria a entidade de domínio `Boleto`, persiste o boleto no PostgreSQL e registra o evento `boleto.gerado` na tabela de outbox.

Depois disso, o `OutboxKafkaPublisher` busca eventos pendentes na tabela `outbox_events` e publica no Kafka. A partir daí, outros serviços consomem o evento sem depender de chamada síncrona ao `boleto-service`.

O `payment-service` armazena os boletos aguardando pagamento e, quando recebe um retorno externo em `POST /simulacoes/pagamentos`, valida valor e vencimento. No projeto local, esse endpoint é um adapter simulador: ele representa o webhook que, em produção, viria de um PSP/Banco após o Cliente Pagador quitar o boleto. Pagamentos válidos geram `pagamento.efetivado`; pagamentos inválidos geram `pagamento.rejeitado`.

O `query-service` consome eventos de boleto, pagamento e notificação para atualizar um read model próprio. Assim, as consultas em `GET /consultas/boletos` não precisam acessar o banco transacional do `boleto-service`.

O `notification-service` consome eventos relevantes, simula envio de notificações e publica `notificacao.enviada`, permitindo que o `query-service` também reflita esse marco na jornada.

## 3. Porquês arquiteturais

### Por que DDD e bounded contexts?

O projeto usa DDD para deixar claro onde vivem as regras de negócio. No `boleto-service`, a entidade `Boleto` concentra regras como criação, status, vencimento, geração de código de barras e linha digitável. Isso evita espalhar regra de negócio em controllers, consumers ou persistência.

Separar boleto, pagamento, consulta e notificação em serviços diferentes também evita que um único modelo tente responder por tudo. Em sistemas de recebimento, o ciclo de vida do boleto não é igual ao ciclo de pagamento, que também não é igual à necessidade de consulta operacional ou notificação ao cliente.

Também é importante separar os atores da jornada. O PJ Parceiro emite o boleto, mas quem paga é o Cliente Pagador em outro canal. O retorno técnico desse pagamento vem de um PSP/Banco externo, que integra com a borda segura do banco recebedor. Por isso, a simulação de pagamento deve ser entendida como webhook/retorno externo, não como uma decisão interna do `payment-service`.

### Por que Arquitetura Hexagonal?

A estrutura `domain -> application -> adapter` torna o núcleo do sistema menos dependente de frameworks. O domínio e os casos de uso não precisam conhecer HTTP, Kafka ou JPA diretamente. Essas tecnologias entram por adapters.

Essa escolha melhora testabilidade. Os testes de domínio e aplicação podem validar regras centrais sem subir Spring, banco de dados ou mensageria. Em uma entrevista, esse é um bom ponto de defesa: a arquitetura não foi escolhida por estética, mas para reduzir acoplamento e permitir validação rápida das regras importantes.

### Por que Transactional Outbox?

Ao criar um boleto, o sistema precisa salvar o boleto no banco e publicar `boleto.gerado`. Fazer isso com dual write simples seria arriscado:

```text
1. salva boleto no banco
2. publica evento no Kafka
```

Se o processo cair entre os passos, o boleto fica salvo, mas o evento não é publicado. Nesse caso, `payment-service`, `query-service` e `notification-service` nunca saberiam que o boleto existe.

Com Transactional Outbox, o boleto e o evento são gravados na mesma transação PostgreSQL. O publisher assíncrono publica depois. Se a publicação falhar, o evento continua pendente e pode ser reprocessado.

Essa decisão resolve o problema de consistência banco/evento sem exigir transação distribuída entre PostgreSQL e Kafka.

### Por que CQRS?

O projeto separa comando e consulta porque os objetivos são diferentes.

O `boleto-service` precisa proteger regras de criação e estado do boleto. Já o `query-service` precisa entregar uma visão consultável da jornada, juntando informações que chegam por eventos: boleto criado, pagamento efetivado ou rejeitado e notificação enviada.

Com CQRS, o serviço de consulta não lê diretamente o schema de escrita do `boleto-service`. Ele mantém seu próprio read model, atualizado por Kafka. Isso melhora isolamento entre contextos e deixa explícita a natureza eventualmente consistente da leitura.

### Por que eventos e Kafka?

Eventos permitem que os serviços reajam ao que aconteceu no domínio sem acoplamento síncrono. Quando `boleto.gerado` é publicado, o `payment-service`, o `query-service` e o `notification-service` podem consumir o mesmo fato de negócio para finalidades diferentes.

Kafka/Redpanda entra como event bus. Localmente, Redpanda oferece compatibilidade Kafka com operação simples via Docker Compose. No desenho AWS, o equivalente é Amazon MSK.

### Por que SQS na arquitetura AWS?

Na arquitetura cloud-alvo, a criação de boleto entra por API Gateway integrado diretamente ao SQS. A API pode responder `202 Accepted` rapidamente, e o `boleto-service` processa a fila no próprio ritmo.

Isso é importante porque criação de boleto pode receber picos. A fila absorve variação de carga, permite retry e encaminha falhas para DLQ depois do limite configurado. Essa escolha reduz pressão sobre o serviço de escrita e melhora resiliência operacional.

No ambiente local, o fluxo principal usa REST direto no `boleto-service`, mas o código já possui consumer SQS desabilitado por configuração para o cenário AWS.

### Por que o pagamento entra por outro estímulo externo?

Porque pagamento é uma confirmação que vem de fora do sistema de emissão. O `boleto-service` sabe gerar e registrar o boleto; ele não deve assumir que o boleto foi pago. Em um cenário real, o pagador quita o boleto em outro canal, e o banco recebedor recebe um retorno de liquidação.

No desenho de solução, isso aparece como Cliente Pagador -> PSP/Banco externo -> WAF -> API Gateway -> `payment-service`. Localmente, `POST /simulacoes/pagamentos` faz esse papel de adapter fake. Em produção, o mesmo papel poderia ser exercido por webhook, SQS, Kafka de entrada, arquivo CNAB/retorno ou integração com PSP/CIP/Nuclea.

### Por que PostgreSQL e Redis?

PostgreSQL é usado porque a criação de boleto exige consistência transacional. O mesmo banco sustenta a gravação do boleto e da outbox, o que é essencial para a garantia do Transactional Outbox.

Redis aparece na arquitetura como base para idempotência. A motivação é evitar duplicidade em cenários de retry, especialmente quando a entrada passa por SQS ou quando mensagens são reprocessadas. No estado atual do código, Redis está presente como dependência e infraestrutura local/cloud, mas a política completa de idempotência ainda é mais arquitetura-alvo do que fluxo implementado de ponta a ponta.

### Por que AWS/EKS?

O desenho AWS mostra como a solução poderia operar fora do ambiente local:

```text
PJ Parceiro
  -> WAF / API Gateway
  -> SQS
  -> boleto-service no EKS
  -> RDS PostgreSQL + outbox_events
  -> Outbox Publisher
  -> Amazon MSK
  -> notification-service / query-service

Cliente Pagador
  -> PSP / Banco externo
  -> WAF / API Gateway webhook
  -> payment-service
  -> Amazon MSK
  -> notification-service / query-service
```

EKS hospeda os serviços, RDS fornece banco relacional, MSK fornece Kafka gerenciado, ElastiCache representa idempotência/cache, Secrets Manager guarda credenciais, CloudWatch apoia operação e Terraform descreve a infraestrutura.

## 4. Como o código comprova a arquitetura

O código sustenta a narrativa nos pontos centrais:

| Decisão | Evidência no código |
| --- | --- |
| Domínio rico | `Boleto` cria o boleto, valida vencimento, gera identificadores e registra `BoletoGeradoEvent`. |
| Caso de uso isolado | `CriarBoletoUseCase` orquestra criação, persistência e publicação pela porta `BoletoEventPublisher`. |
| Outbox | `BoletoOutboxEventPublisher` transforma evento de domínio em evento de integração e salva `OutboxEvent`. |
| Publicação assíncrona | `OutboxKafkaPublisher` busca eventos pendentes e publica no Kafka por agendamento. |
| CQRS | `BoletoReadModelConsumers` consome eventos e chama `AtualizarBoletoReadModelUseCase`. |
| Pagamento por retorno externo | `EfetivarPagamentoUseCase` valida valor e vencimento quando o adapter de pagamento recebe o webhook/retorno externo do PSP/Banco. |
| Notificação por evento | Consumers do `notification-service` reagem a eventos e publicam `notificacao.enviada`. |
| Entrada AWS assíncrona | Terraform configura API Gateway integrado ao SQS, e `BoletoGenerationSqsConsumer` consome mensagens quando habilitado. |

A camada de testes também reforça a intenção. Há testes para domínio de boleto, criação de boleto, publicação via outbox, pagamento e notificação. Isso mostra que as regras centrais foram pensadas para serem validadas em isolamento.

## 5. Interfaces e contratos principais

Endpoints locais mais importantes:

| Método | Endpoint | Serviço | Uso |
| --- | --- | --- | --- |
| `POST` | `/boletos` | `boleto-service` | Cria boleto no modelo de escrita. |
| `GET` | `/boletos/{id}` | `boleto-service` | Consulta o boleto no modelo transacional. |
| `POST` | `/simulacoes/registradora/boletos` | `boleto-service` | Simula registro externo do boleto. |
| `POST` | `/simulacoes/pagamentos` | `payment-service` | Simula webhook de pagamento vindo de PSP/Banco externo e publica resultado. |
| `GET` | `/consultas/boletos` | `query-service` | Lista o read model de boletos. |
| `GET` | `/consultas/boletos/{boletoId}` | `query-service` | Consulta a visão consolidada de um boleto. |

Eventos principais:

| Evento | Origem | Consumidores |
| --- | --- | --- |
| `boleto.gerado` | `boleto-service` via outbox | `payment-service`, `query-service`, `notification-service` |
| `pagamento.efetivado` | `payment-service` | `query-service`, `notification-service` |
| `pagamento.rejeitado` | `payment-service` | `query-service`, `notification-service` |
| `notificacao.enviada` | `notification-service` | `query-service` |

## 6. Tradeoffs e maturidade

O projeto é uma boa base de demonstração, mas é importante separar implementação atual, desenho alvo e próximos passos.

### Implementado no fluxo local

- Quatro serviços Spring Boot em um monorepo Maven.
- Criação de boleto via REST.
- Persistência PostgreSQL com Flyway.
- Transactional Outbox no `boleto-service`.
- Publicação e consumo de eventos via Kafka/Redpanda.
- Read model próprio no `query-service`.
- Simulação de retorno externo de pagamento e notificação.
- Docker Compose para PostgreSQL, Redis, Redpanda e serviços.
- Documentação de arquitetura, ADRs, coleções Postman/Insomnia e infraestrutura AWS.

### Arquitetura-alvo documentada

- API Gateway publicando diretamente em SQS.
- SQS com DLQ para entrada assíncrona de criação de boleto.
- Webhook/entrada dedicada para retorno de pagamento vindo de PSP/Banco externo.
- EKS como runtime dos serviços.
- Amazon MSK como Kafka gerenciado.
- RDS PostgreSQL, ElastiCache Redis, S3, SNS/SES, Secrets Manager, CloudWatch, WAF, IAM/IRSA e ECR.
- Terraform e manifests Kubernetes para provisionamento/deploy.

### Pontos para evolução

- Completar idempotência de ponta a ponta com Redis e `Idempotency-Key`.
- Tornar tratamento de erro Kafka mais robusto com retry, backoff e DLT por consumer.
- Ampliar testes integrados com Kafka/PostgreSQL/Testcontainers para cobrir jornada completa.
- Persistir estado do `payment-service` em storage durável, caso deixe de ser apenas simulação.
- Adicionar observabilidade distribuída mais completa, com tracing e métricas de negócio.
- Definir política de limpeza/arquivamento da tabela `outbox_events`.

## 7. Roteiro curto para falar em entrevista

Uma forma direta de explicar o projeto:

> O Received Bank é uma simulação de modernização de recebimentos bancários. Eu escolhi boleto porque é um domínio com consistência forte, eventos importantes e jornada assíncrona. O `boleto-service` é o modelo de escrita e aplica as regras de domínio. Quando um boleto é criado, eu não publico direto no Kafka depois de salvar no banco, porque isso criaria risco de dual write. Eu uso Transactional Outbox: boleto e evento são gravados na mesma transação, e um publisher assíncrono publica depois.

Depois, complemente:

> A partir do evento `boleto.gerado`, outros serviços reagem sem chamada síncrona. O `payment-service` usa esse evento para saber quais boletos estão aguardando pagamento. Mas o pagamento em si não nasce dentro do sistema: o Cliente Pagador paga em um PSP/Banco externo, e esse agente envia um webhook de retorno, que no projeto local é simulado por `POST /simulacoes/pagamentos`.

E feche com a visão cloud:

> No desenho AWS, a emissão passa por WAF/API Gateway e SQS para absorver picos e permitir retry/DLQ. O pagamento passa por PSP/Banco externo e retorna via webhook pela borda segura. Os serviços rodam em EKS, eventos passam pelo MSK, dados ficam em RDS, PDFs podem ir para S3, notificações saem por SNS/SES e Redis apoia idempotência.

## 8. Resposta rápida para perguntas comuns

**Por que não fazer tudo síncrono?**  
Porque pagamento, notificação e consulta não precisam bloquear a criação do boleto. Eventos reduzem acoplamento e tornam cada contexto evolutivo.

**Por que a simulação de pagamento existe?**  
Porque o projeto não integra com PSP/Banco real, CIP/Nuclea ou arquivo de retorno. O endpoint de simulação representa esse ator externo enviando uma liquidação para o `payment-service`.

**Por que não publicar direto no Kafka dentro do use case?**  
Porque salvar no banco e publicar no Kafka não compartilham uma transação. A outbox evita perder evento quando há falha entre os dois passos.

**Por que query-service separado?**  
Porque a consulta representa uma visão consolidada da jornada. Ela não deve depender do schema transacional do serviço de boleto.

**Por que usar Kafka e SQS juntos no desenho AWS?**  
SQS é usado como fila de entrada para absorver picos de comandos. Kafka/MSK é usado como log de eventos de domínio para integração entre serviços.

**Qual é a principal garantia da arquitetura?**  
O boleto e o evento inicial nascem de forma consistente no banco. A publicação é at-least-once, então consumidores devem ser idempotentes.

**O que eu melhoraria em produção?**  
Fechar idempotência com Redis no fluxo real, fortalecer retry/DLT Kafka, ampliar observabilidade, persistir dados operacionais do pagamento e criar política de retenção da outbox.
