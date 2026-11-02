# ADR-0006: Autorização declarativa por anotação, recusando por padrão

- Status: Aceita
- Data: 2026-11-01

## Contexto

Cada endpoint tem um público diferente: gerente para catálogo e estoque, mecânico para OS e orçamento, cliente para os próprios dados, e alguns endpoints públicos (login, link de aprovação). Regras de URL no `SecurityFilterChain` ficam longe do endpoint e é fácil esquecer uma rota nova.

## Decisão

- Cada endpoint (ou controller) declara `@Authentication(minimumRole, deniedRoles)` ou `@PublicEndpoint`.
- O `UserAuthenticationFilter` resolve o handler da requisição, lê a anotação e valida token, papel mínimo (hierarquia por peso: `ADMIN` > `MANAGER` > `MECHANIC` > `CUSTOMER`) e papéis negados.
- **Recusa por padrão**: endpoint sem nenhuma das anotações responde 401 (`UndeclaredEndpointAccessException`).
- O isolamento de dados do cliente (só os próprios registros) é feito no app service, com o `AuthenticatedUser`.

## Alternativas consideradas

- **Regras por URL no Spring Security** (`requestMatchers(...).hasRole(...)`): padrão do framework, mas centraliza longe do código e falha aberto quando uma rota fica de fora.
- **`@PreAuthorize` com SpEL**: declarativo, mas sem hierarquia por peso nem "papéis negados" de forma simples, e sem a recusa por padrão.

## Consequências

- Positivas: a permissão fica ao lado do endpoint e aparece na revisão; uma rota nova sem anotação é bloqueada; casos como "só o cliente decide o orçamento" (`deniedRoles`) ficam explícitos.
- Negativas: o Spring Security fica com `permitAll` e a segurança depende do filtro próprio, que precisa de testes dedicados; a regra de "só os próprios dados" fica espalhada pelos app services.
