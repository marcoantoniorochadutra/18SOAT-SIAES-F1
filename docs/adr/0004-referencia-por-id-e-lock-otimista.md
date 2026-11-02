# ADR-0004: Agregados referenciados por id e lock otimista

- Status: Aceita
- Data: 2026-11-01

## Contexto

Guardar a instância de outro agregado (ex.: `WorkOrder` com um `Customer` dentro) amplia a fronteira de consistência e o que é carregado e salvo. Ao mesmo tempo, OS, orçamento e estoque recebem atualizações concorrentes: dois orçamentos podem reservar a última unidade de uma peça, ou um cliente e um mecânico podem mexer no mesmo orçamento.

## Decisão

- Agregados referenciam outros agregados só pelo id tipado (`CustomerId`, `VehicleId`, `WorkOrderId`, `SuppliesId`...). A integridade fica nas chaves estrangeiras.
- `WorkOrder`, `Quotation` e `StockItem` têm `@Version` (lock otimista). O conflito vira `OptimisticLockingFailureException`, respondido com 409 pelo `GlobalExceptionHandler`.
- O banco reforça as regras críticas (`CHECK (reserved_quantity <= quantity)`, índice único parcial de orçamento aberto).

## Alternativas consideradas

- **Associações JPA entre agregados**: navegação cômoda, mas carregamento implícito, fronteiras borradas e acoplamento entre contextos.
- **Lock pessimista (`SELECT ... FOR UPDATE`)**: elimina o conflito, mas serializa o acesso e aumenta o risco de deadlock, sem ganho para a taxa de conflito de uma oficina.

## Consequências

- Positivas: agregados pequenos e independentes; nenhuma atualização perdida; o banco é a última barreira.
- Negativas: o cliente recebe 409 em caso de conflito e precisa repetir a operação; montar respostas com dados de vários agregados exige consultas separadas ou projeções.
