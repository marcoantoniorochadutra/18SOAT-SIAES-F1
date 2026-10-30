# SIAES

Sistema Integrado de Atendimento e Execução de Serviços para oficinas mecânicas, desenvolvido para o Tech Challenge da Fase 1 da pós-graduação em Software Architecture da FIAP.

É um monólito Spring Boot organizado em arquitetura hexagonal e DDD, com um contexto delimitado por pacote:

| Contexto | Responsabilidade |
|---|---|
| `auth` | Login, refresh token, logout e autorização por papel |
| `user` | Usuários do sistema (ADMIN, MANAGER, MECHANIC, CUSTOMER...) |
| `customer` | Clientes (CPF/CNPJ) |
| `vehicle` | Veículos dos clientes |
| `maintenance` | Catálogo de serviços |
| `supplies` / `inventory` | Peças e insumos, estoque e movimentações |
| `quotation` | Orçamentos e aprovação externa pelo cliente |
| `workorder` | Ordens de serviço, status e métricas de execução |
| `sk` | Shared kernel (documento, paginação, exceções, utilitários) |

## Tecnologias

- Java 26 e Spring Boot 4.0
- PostgreSQL 17 com migrações Flyway
- Spring Security com JWT
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito e JaCoCo (cobertura mínima de 80%)
- Docker e Docker Compose, Mailpit para os e-mails em desenvolvimento
- GitHub Actions, SonarCloud e Trivy

## Pré-requisitos

| Para | Precisa de |
|---|---|
| Rodar tudo em contêineres | Docker com Docker Compose |
| Rodar a aplicação localmente e os testes | JDK 26, Maven 3.9+ e Docker (para o PostgreSQL e o Mailpit) |

## Como rodar

### Opção 1: tudo no Docker

```bash
docker compose -f devops/docker-compose.yml up -d --build
```

Sobem três contêineres:

| Serviço | Endereço |
|---|---|
| API (`siaes-app`) | http://localhost:8715 |
| PostgreSQL (`siaes-postgres`) | `localhost:5432`, banco `siaes`, usuário `siaes` |
| Mailpit (`siaes-mailpit`) | SMTP em `localhost:1025`, caixa de entrada em http://localhost:8025 |

Na primeira subida, o PostgreSQL também cria o banco `siaes_it`, usado pelos testes de integração (`devops/postgres/01-create-integration-test-database.sql`).

Para parar: `docker compose -f devops/docker-compose.yml down`. Para apagar também os dados: acrescente `-v`.

> As migrações Flyway ainda são editadas no lugar, porque o sistema não está em produção. Se a aplicação falhar com `Validate failed: Migrations have failed validation` (checksum diferente), recrie o banco com `docker compose -f devops/docker-compose.yml down -v`.

### Opção 2: aplicação local, dependências no Docker

```bash
docker compose -f devops/docker-compose.yml up -d siaes-postgres siaes-mailpit
mvn spring-boot:run
```

O perfil padrão é `dev`, que já traz valores padrão para banco, e-mail e segredos. Nenhuma variável de ambiente é obrigatória.

### Variáveis de ambiente

No perfil `dev`, todas têm valor padrão. No perfil `prod` (`SPRING_PROFILES_ACTIVE=prod`), as marcadas como obrigatórias precisam ser informadas.

| Variável | Padrão no `dev` | Obrigatória no `prod` |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | |
| `SERVER_PORT` / `SERVER_CONTEXT_PATH` | `8715` / `/` | |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `5432` / `siaes` | sim |
| `DB_USERNAME` / `DB_PASSWORD` | `siaes` / `siaes` | sim |
| `JWT_SECRET` | valor de desenvolvimento | sim |
| `SIAES_AUTH_TOKEN_EXPIRATION` / `SIAES_AUTH_TOKEN_REFRESH_EXPIRATION` | `365` / `1` (dias) | sim |
| `MAIL_HOST` / `MAIL_PORT` / `MAIL_FROM` | `localhost` / `1025` / `no-reply@siaes.com` | sim |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | vazio | sim |
| `QUOTATION_APPROVAL_LINK_BASE_URL` | `http://localhost:8715/api/v1/external/quotations` | sim |
| `QUOTATION_APPROVAL_SECRET` | valor de desenvolvimento | sim |
| `QUOTATION_APPROVAL_TIME_TO_LIVE` | `72h` | |
| `APP_ENVIRONMENT` / `APP_VERSION` | | sim |

## Primeiro acesso

Não há usuário pré-cadastrado e a criação de usuários (`POST /api/v1/users`) exige autenticação. Crie o primeiro administrador direto no banco:

```bash
docker exec -i siaes-postgres psql -U siaes -d siaes <<'SQL'
CREATE EXTENSION IF NOT EXISTS pgcrypto;
INSERT INTO siaes.users (id, name, email, password, role)
VALUES (gen_random_uuid(), 'Administrador', 'admin@admin.com', crypt('@dmiN123', gen_salt('bf', 10)), 'ADMIN');
SQL
```

Depois, faça login:

```bash
curl -X POST http://localhost:8715/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@admin.com", "password": "@dmiN123"}'
```

A resposta traz `token` e `refreshToken`. Use o `token` no cabeçalho `Authorization: Bearer <token>` das demais chamadas, ou no botão **Authorize** do Swagger.

## Documentação da API

- Swagger UI: http://localhost:8715/swagger-ui.html
- OpenAPI (JSON): http://localhost:8715/v3/api-docs

As mensagens de erro seguem o formato `ProblemDetail` (RFC 9457) e são traduzidas pelo cabeçalho `Accept-Language` (`pt-BR`, padrão, ou `en`).

## Testes e cobertura

Os testes de integração (`*IT`) usam o banco `siaes_it` do PostgreSQL do compose, que precisa estar no ar.

```bash
mvn test      # só testes unitários
mvn verify    # unitários + integração + verificação de cobertura mínima (80%)
```

Relatório de cobertura do JaCoCo: `target/site/jacoco/index.html`, gerado pelo `mvn verify`.

## Relatório de vulnerabilidades (Trivy)

O script `devops/security-scan.sh` roda o [Trivy](https://trivy.dev) em um contêiner, então só precisa do Docker:

```bash
docker compose -f devops/docker-compose.yml build siaes-app   # gera a imagem siaes-app:latest
bash devops/security-scan.sh                                   # analisa repositório e imagem
```

No Windows, rode pelo Git Bash. O script aceita dois argumentos opcionais: a imagem e a pasta de saída (padrão `siaes-app:latest docs/security`).

Ele analisa:

1. **Repositório**: segredos expostos e configuração do `Dockerfile` e do compose.
2. **Imagem**: CVEs nos pacotes do Alpine, no runtime Java e nas dependências da aplicação.

O resultado fica em `docs/security/trivy-report.md`, com o resumo por severidade e, para cada CVE, o pacote, a versão instalada e a versão que corrige. Os JSON brutos (`docs/security/*.json`) não são versionados. O layout do relatório está em `devops/trivy/report.md.tpl`.

## Integração contínua

O workflow `.github/workflows/ci.yml` roda em push e pull request para `main`:

| Job | O que faz |
|---|---|
| `build` | `mvn verify` com PostgreSQL de serviço, publica o relatório do JaCoCo como artefato e envia a análise para o SonarCloud |
| `security` | Gera a imagem, roda `devops/security-scan.sh`, publica os achados na aba **Security** do GitHub e o relatório em Markdown no resumo da execução e no artefato `trivy-reports` |

Configuração necessária no repositório:

- Secret `SONAR_TOKEN`, com um token do SonarCloud. Sem ele, a etapa do Sonar é pulada.
- No SonarCloud, desative a **Automatic Analysis** do projeto, porque ela conflita com a análise feita pelo CI.
- A trava de severidade do Trivy fica em `TRIVY_EXIT_CODE` no workflow: `"0"` só reporta, `"1"` falha o build com achados HIGH ou CRITICAL.

## Estrutura do projeto

```
src/main/java/com/fiap/siaes/<contexto>/
├── domain/          # modelo, value objects, regras e portas (sem Spring)
├── application/     # casos de uso
└── infrastructure/  # web (controllers), persistência JPA, integrações
src/main/resources/
├── db/migration/    # migrações Flyway
└── messages/        # mensagens i18n (pt_BR e en)
devops/              # docker-compose, init do PostgreSQL, script e template do Trivy
docs/                # enunciado do Tech Challenge e relatório de segurança
```
