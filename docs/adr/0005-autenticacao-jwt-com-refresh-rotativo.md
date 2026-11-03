# ADR-0005: Autenticação JWT com refresh token rotativo

- Status: Aceita
- Data: 2026-11-01

## Contexto

O enunciado exige JWT nas APIs administrativas. A API é stateless e precisa de logout e de renovação de sessão sem pedir a senha de novo.

## Decisão

- **Access token**: JWT assinado com HMAC (`JWT_SECRET`), com a claim `userData` (`userId`, `role`) e payload comprimido. Validade em dias (`SIAES_AUTH_TOKEN_EXPIRATION`).
- **Refresh token**: 32 bytes aleatórios, entregues ao cliente; o banco guarda só o SHA-256 (`users.refresh_token`). Cada `POST /auth/refresh` gera um novo par e invalida o anterior (rotação).
- **Logout**: o access token entra em uma lista de bloqueio (`TokenBlocklist`, Caffeine em memória) e o refresh token é descartado.
- A cada requisição, o usuário é carregado pelo id do token, então um usuário removido perde o acesso na hora.

## Alternativas consideradas

- **Sessão no servidor (cookie + sessão)**: revogação trivial, mas estado no servidor e problemas com clientes que não são navegador.
- **OAuth2/OIDC com provedor externo (Keycloak, Cognito)**: mais completo, mas infraestrutura e configuração desproporcionais ao MVP.
- **Refresh token em texto puro**: mais simples, mas um vazamento do banco daria sessões válidas.

## Consequências

- Positivas: API stateless; vazamento do banco não expõe refresh tokens; rotação limita o reuso de um refresh roubado.
- Negativas e riscos conhecidos:
  - A lista de bloqueio é em memória: não é compartilhada entre instâncias e se perde ao reiniciar.
  - As entradas da lista expiram após `SIAES_AUTH_TOKEN_REFRESH_EXPIRATION` (horas), mas o access token vale dias. Depois desse prazo, um token que fez logout volta a ser aceito até expirar.
  - O padrão de desenvolvimento do access token é de 365 dias.
- Mitigação proposta: expirar as entradas da lista junto com o próprio token (usar o `exp` do JWT), adotar access tokens curtos (minutos) apoiados no refresh e, com mais de uma instância, mover a lista para um cache compartilhado.
