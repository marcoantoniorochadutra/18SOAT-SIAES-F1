# ADR-0011: Paginação própria no shared kernel

- Status: Aceita
- Data: 2026-11-01

## Contexto

As listagens (clientes, OS, orçamentos, catálogo, movimentações) precisam de paginação. Usar `Pageable` e `Page` do Spring Data nas portas e casos de uso levaria o framework para o domínio e a aplicação. Além disso, `Page` faz um `COUNT` a cada consulta.

## Decisão

- `PageQuery(page, size)` no `sk/pagination`, com padrão `0`/`20`, máximo `100` e validação por Bean Validation.
- `PageResponse<T>(content, hasNext, page, size)`, sem total de registros.
- Os adapters JPA consultam um `Slice` e convertem com `PageResponses.from`, o único ponto que conhece o Spring Data.

## Alternativas consideradas

- **`Pageable`/`Page` do Spring Data em toda a pilha**: menos código, mas framework no domínio e `COUNT` em toda listagem.
- **Paginação por cursor**: melhor para listas muito grandes, mas mais complexa para o cliente e desnecessária no volume de uma oficina.

## Consequências

- Positivas: domínio e aplicação independentes do Spring Data; listagens sem `COUNT`; contrato de paginação igual em todos os endpoints.
- Negativas: o cliente não sabe o total de páginas, só se existe a próxima.
