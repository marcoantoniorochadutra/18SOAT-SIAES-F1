# ADR-0007: Status da OS alterado só por ações de negócio

- Status: Aceita
- Data: 2026-11-01

## Contexto

O enunciado pede a alteração automática do status conforme as ações no sistema e a aplicação de regras de transição. Um endpoint genérico de "trocar status" permitiria pular etapas (ex.: executar sem orçamento aprovado) e esconderia a intenção da operação.

## Decisão

- Não existe endpoint para alterar o status. Cada transição é efeito de uma ação: abrir (`RECEIVED`), iniciar diagnóstico (`IN_DIAGNOSIS`), concluir orçamento (`AWAITING_APPROVAL`, por evento), aprovar orçamento (`IN_EXECUTION`, por evento), finalizar (`FINISHED`), entregar (`DELIVERED`), cancelar (`CANCELLED`).
- `WorkOrderStatus.canChangeTo` define as transições válidas e `WorkOrder.transitionTo` rejeita as demais.
- Toda transição grava `WorkOrderStatusHistory`, que alimenta o acompanhamento pelo cliente e as métricas de tempo de execução.

## Alternativas consideradas

- **`PATCH /workOrders/{id}/status`** com validação de transição: menos endpoints, mas API pouco expressiva e regras de cada etapa (diagnóstico obrigatório, orçamento aprovado) espalhadas por condicionais.

## Consequências

- Positivas: impossível chegar a um status sem passar pelas etapas; API orientada a intenção; histórico completo para auditoria e métricas.
- Negativas: corrigir um status lançado por engano exige uma ação de negócio (ex.: cancelar e abrir outra OS), não uma edição.
