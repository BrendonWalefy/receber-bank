# Colecao de API

Importe `received-bank.postman_collection.json` no Postman ou `received-bank.insomnia.json` no Insomnia para demonstrar a jornada ponta a ponta do projeto.

## Como usar

1. Suba a stack local:

```bash
cd received-bank-services
docker compose up --build
```

2. Importe uma das colecoes:

```text
received-bank-services/docs/api/received-bank.postman_collection.json
received-bank-services/docs/api/received-bank.insomnia.json
```

3. Execute a pasta `00 - Health checks`.
4. Execute a pasta `01 - Jornada feliz` em ordem.
5. Apos `Criar boleto PJ`, aguarde alguns segundos antes de simular o webhook de pagamento do PSP/Banco externo. O outbox precisa publicar `boleto.gerado` e o `payment-service` precisa consumir o evento para conhecer o boleto aguardando liquidacao.
6. Para demonstrar falha de regra de negocio, crie outro boleto e execute `02 - Cenario de rejeicao`.

No Postman, a colecao captura automaticamente variaveis como `boleto_id`, `codigo_barras` e `linha_digitavel`. No Insomnia, copie esses valores da resposta de criacao do boleto para o ambiente antes de seguir para os proximos requests.

## Portas padrao

- `boleto-service`: `http://localhost:8081`
- `query-service`: `http://localhost:8082`
- `payment-service`: `http://localhost:8083`
