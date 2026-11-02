# ADR-0010: Erros como ProblemDetail com mensagens i18n

- Status: Aceita
- Data: 2026-11-01

## Contexto

O domínio lança dezenas de exceções de negócio. Sem um padrão, cada controller trataria erros de um jeito e o domínio passaria a conhecer HTTP. A API atende usuários em português e precisa estar pronta para outros idiomas.

## Decisão

- Toda exceção de negócio estende `DomainException(ErrorType, params...)`. `ErrorType` é semântico (`INVALID`, `NOT_FOUND`, `CONFLICT`, `UNAUTHENTICATED`, `FORBIDDEN`), sem tipos HTTP no domínio.
- O `GlobalExceptionHandler` converte para `ProblemDetail` (RFC 9457), mapeia o `ErrorType` para o status HTTP e resolve título e detalhe pelas chaves `<Exceção>.message` e `<Exceção>.detail`, com os parâmetros da exceção.
- Bean Validation usa `message="{chave}"`; o 400 traz a lista `fields`.
- Bundles `pt_BR` (padrão) e `en` para exceções, validação, Swagger e notificações.

## Alternativas consideradas

- **Corpo de erro próprio** (`{code, message}`): funciona, mas reinventa um padrão que o Spring já suporta.
- **Mensagens fixas no código**: mais rápido, mas sem tradução e com texto de UI no domínio.

## Consequências

- Positivas: formato único e padronizado; domínio sem HTTP; tradução sem mexer em código.
- Negativas: cada exceção nova exige chaves nos dois idiomas; uma chave esquecida aparece como texto cru na resposta.
