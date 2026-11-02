# ADR-0003: Integração entre contextos por eventos de domínio em processo

- Status: Aceita
- Data: 2026-11-01

## Contexto

Ações em um contexto têm efeito em outro: concluir o orçamento põe a OS em aguardando aprovação, aprová-lo põe a OS em execução, criar uma peça cria o item de estoque, excluir um cliente remove os dados dependentes. Chamar o outro contexto diretamente acopla o upstream ao downstream. Ao mesmo tempo, a maior parte desses efeitos precisa ser atômica.

## Decisão

- Os agregados registram eventos (`AggregateRoot.registerEvent`). O app service salva e publica `pullDomainEvents()` pela porta `DomainEventPublisher`, implementada sobre o `ApplicationEventPublisher` do Spring.
- **Síncrono por padrão** (`@EventListener`): o consumidor roda na mesma transação, e uma falha desfaz a operação inteira.
- **Assíncrono só para efeitos externos** (`@Async` + `@TransactionalEventListener(AFTER_COMMIT)`): hoje, apenas o envio do link de aprovação. Executor dedicado com fila limitada e `CallerRunsPolicy`.
- Sem broker de mensagens.

## Alternativas consideradas

- **Chamada direta entre app services**: simples, mas o upstream passaria a conhecer cada consumidor.
- **Broker (Kafka, RabbitMQ) com outbox**: desacoplamento e entrega garantida entre processos, mas consistência eventual e infraestrutura extra sem necessidade em um único processo.
- **Tudo assíncrono**: libera a requisição mais cedo, mas quebra a atomicidade entre orçamento, estoque e OS.

## Consequências

- Positivas: consistência forte entre agregados de contextos diferentes; o upstream não conhece os consumidores; a troca por um broker no futuro mantém os mesmos eventos.
- Negativas: a transação cresce com cada consumidor síncrono; eventos assíncronos não sobrevivem a uma queda do processo após o commit (um link de aprovação pode deixar de ser enviado e precisa ser reemitido por `POST /quotations/{id}/approvalLink`).
