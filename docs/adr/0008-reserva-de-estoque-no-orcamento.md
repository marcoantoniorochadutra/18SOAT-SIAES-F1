# ADR-0008: Reserva de estoque na criação do orçamento

- Status: Aceita
- Data: 2026-11-01

## Contexto

Entre o orçamento e a aprovação do cliente pode passar tempo. Se a peça só for verificada na aprovação, outro orçamento pode tê-la consumido, e o cliente aprova algo que a oficina não consegue executar.

## Decisão

- Criar ou editar um orçamento **reserva** as peças (`reserved_quantity`), exigindo saldo disponível.
- Aprovar **consome** a reserva (baixa saldo e reservado). Rejeitar, descartar (cancelamento da OS) ou remover itens **libera** a reserva.
- Toda operação gera um `StockMovement` (`RESERVATION`, `RESERVATION_RELEASE`, `CONSUMPTION`) com o saldo resultante e a referência do orçamento.
- O estoque é um agregado próprio (`StockItem`, contexto `inventory`), chamado pelo `quotation` via casos de uso.

## Alternativas consideradas

- **Baixa direta na aprovação, sem reserva**: mais simples, mas permite aprovar orçamento sem peça disponível.
- **Reserva com expiração automática**: libera estoque de orçamentos esquecidos, mas exige agendamento e regras de renovação fora do escopo do MVP.

## Consequências

- Positivas: o orçamento enviado ao cliente é executável; histórico de cada unidade reservada, liberada e consumida.
- Negativas: orçamentos sem resposta prendem estoque até a decisão ou o cancelamento; a validação de liberação e baixa usa o total reservado do item, não a reserva de cada orçamento (limitação registrada em [requisitos.md](../requisitos.md#limitações-conhecidas)).
