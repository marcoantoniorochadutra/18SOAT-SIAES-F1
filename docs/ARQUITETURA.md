# Arquitetura do projeto

Arquitetura **Hexagonal (Ports & Adapters) / Clean Architecture, organizada em módulos por contexto de negócio (DDD)** — um monólito modular, não uma arquitetura em camadas técnicas no nível raiz.

O padrão se repete de forma consistente nos módulos de negócio (`auth`, `user`, `customer`, `vehicle`, `workorder`, `maintenance`, `quotation`, `supplies`), mais um shared kernel (`sk`).

## Estrutura por módulo (3 camadas)

Cada módulo segue sempre:

```
<modulo>/
  domain/            → regras de negócio puras
    model/            → entidades, agregados
    model/vo/         → Value Objects (IDs, atributos)
    repository/       → portas (interfaces)
    exception/        → exceções de domínio
    security/         → portas de segurança (ex.: TokenIssuer, TokenBlacklist)
  application/        → orquestração
    usecase/          → interface + Command/Query aninhados
    service/          → implementação do usecase (@Service)
    dto/, port/       → DTOs de resposta, portas auxiliares
  infrastructure/      → adapters (frameworks, I/O)
    web/              → @RestController + interface *ControllerOpenApi
    persistence/      → entity, repository (Spring Data), adapter, mapper
    security/, config/, notification/
```

**Regra de dependência** (clássica do Hexagonal/Clean Architecture): `domain` não depende de nada; `application` depende só de `domain`; `infrastructure` depende de `application` + `domain`.

Os testes espelham essa mesma estrutura (`src/test` replica os pacotes de `src/main`), com testes unitários por camada usando Mockito.

## Padrões transversais que reforçam o isolamento

- **Repository como porta**: `RepositoryBase<T, ID>` genérico em `sk.domain.repository`, estendido por cada `XRepository` (`CustomerRepository`, `UserRepository`, etc.) — Repository pattern por agregado.
- **Value Objects com tipagem própria**: IDs (`CustomerId`, `UserId`, `WorkOrderId`...) em `domain.model.vo`, com `@JsonCreator`/`@JsonValue` (serialização JSON) e um `UUIDWrapperJavaType` customizado (Hibernate `UserType`) — o VO atravessa JSON e JPA sem nunca "vazar" como `UUID` cru nas assinaturas.
- **Use Case explícito**: uma interface por caso de uso, com `Command`/`Query` como records aninhados, e uma única implementação em `application.service`.
- **DTOs de resposta obrigatórios**: controllers/use cases nunca retornam entidades de domínio, só `*Response`.
- **Paginação desacoplada**: `PageQuery`/`PageResponse` próprios em `sk.pagination`; o `Page` do Spring só aparece dentro do adapter JPA.
- **Documentação OpenAPI separada**: cada controller implementa uma interface `*ControllerOpenApi` com as anotações Swagger e chaves i18n (`messages/swagger/*`), mantendo o controller limpo.
- **Shared kernel (`sk`)**: `DomainException` base, `UUIDWrapper`, `RepositoryBase`, validação de CPF/CNPJ (`sk.document`), `GlobalExceptionHandler` (`@RestControllerAdvice` traduzindo `DomainException` → `ProblemDetail` via i18n), configs de Swagger/i18n.

## Pontos fora do padrão (observações)

- `DomainException` (camada **domain**) importa `org.springframework.http.HttpStatus` diretamente — um vazamento pragmático do framework pra dentro do domínio puro, provavelmente aceito pra evitar duplicar um enum de status.
- A autenticação/autorização não usa Spring Security de fato (a dependência `spring-security-crypto` existe só pelo `BCryptPasswordEncoder`). É uma solução caseira: `UserAuthenticationFilter` (um `OncePerRequestFilter` comum) resolve o `HandlerMethod` via `RequestMappingHandlerMapping` e lê a anotação customizada `@Authentication` — não é `SecurityFilterChain`/`@PreAuthorize`. Funciona, mas é uma escolha deliberada de não usar a stack de segurança padrão do Spring.
