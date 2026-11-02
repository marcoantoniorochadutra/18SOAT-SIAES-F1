# ADR-0012: Remoção lógica de serviços e peças

- Status: Aceita
- Data: 2026-11-01

## Contexto

Serviços (`maintenance`) e peças (`supplies`) aparecem em orçamentos antigos e em movimentações de estoque. Apagar um item do catálogo quebraria o histórico ou exigiria apagar orçamentos e movimentações.

## Decisão

- Serviços e peças não são apagados: têm `active` e os endpoints `PATCH /{id}/deactivate` e `PATCH /{id}/activate`.
- Itens inativos não entram em novos orçamentos (`QuotationAssembler`), mas continuam visíveis nos orçamentos e no histórico.
- O orçamento copia descrição e preço do catálogo no momento da inclusão, então mudanças posteriores de preço não alteram orçamentos já feitos.

## Alternativas consideradas

- **Exclusão física com `ON DELETE CASCADE`**: catálogo enxuto, mas perde o histórico de orçamentos e estoque.
- **Exclusão física bloqueada quando há uso**: preserva o histórico, mas impede retirar do catálogo um item que já foi usado.

## Consequências

- Positivas: histórico íntegro; o item sai do catálogo sem perder rastreabilidade; preço do orçamento estável.
- Negativas: o catálogo acumula itens inativos, e as listagens precisam filtrar por status. Clientes e veículos seguem com exclusão física, por serem dados pessoais.
