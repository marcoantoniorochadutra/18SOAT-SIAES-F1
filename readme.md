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

### Por que PostgreSQL

- **Modelo relacional com transações ACID.** Ordem de serviço, orçamento e estoque mudam juntos: o orçamento reserva peças no estoque e a decisão do cliente muda o status da OS, operações que precisam ser atômicas. As regras de integridade ficam no banco (19 chaves estrangeiras, índices únicos para e-mail, documento e código, e `CHECK` de valores), não só na aplicação.
- **Concorrência.** Estoque, orçamento e OS usam controle otimista (`@Version`), que o PostgreSQL atende bem com MVCC: leituras não bloqueiam escritas, e duas reservas simultâneas da mesma peça não deixam o saldo negativo.
- **Robustez e escalabilidade.** É um banco maduro, com réplicas de leitura, particionamento e connection pooling quando o volume crescer.
- **Custo e portabilidade.** É open source, sem licença. O mesmo banco roda no Docker em desenvolvimento e em qualquer provedor gerenciado (Supabase, AWS RDS/Aurora, Google Cloud SQL, Azure), sem mudar código.
- **Caminho de produto.** Para começar, o [Supabase](https://supabase.com) oferece PostgreSQL gerenciado com backup e plano gratuito. Basta apontar `DB_HOST`, `DB_USERNAME` e `DB_PASSWORD` para ele. Quando o volume justificar, a migração para uma instância dedicada é um `pg_dump`/`pg_restore`, e o Flyway mantém o schema igual nos dois lados.
- **Ecossistema Java.** Suporte de primeira linha no Hibernate, no Flyway e no driver JDBC, além de extensões úteis para o futuro, como `pgcrypto` e busca textual.

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
| `DB_POOL_MAX_SIZE` / `DB_POOL_MIN_IDLE` | `5` / `1` | |
| `DB_POOL_LEAK_DETECTION_THRESHOLD` | `20000` (ms) | |
| `JWT_SECRET` | valor de desenvolvimento | sim |
| `SIAES_AUTH_TOKEN_EXPIRATION` / `SIAES_AUTH_TOKEN_REFRESH_EXPIRATION` | `365` / `1` (dias) | sim |
| `MAIL_HOST` / `MAIL_PORT` / `MAIL_FROM` | `localhost` / `1025` / `no-reply@siaes.com` | sim |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | vazio | sim |
| `QUOTATION_APPROVAL_LINK_BASE_URL` | `http://localhost:8715/api/v1/external/quotations` | sim |
| `QUOTATION_APPROVAL_SECRET` | valor de desenvolvimento | sim |
| `QUOTATION_APPROVAL_TIME_TO_LIVE` | `72h` | |
| `APP_ENVIRONMENT` / `APP_VERSION` | | sim |

## Primeiro acesso

No perfil `dev` (o padrão, inclusive no Docker Compose), o Flyway carrega dados de demonstração de `src/main/resources/db/seed/R__seed_demo_data.sql`: usuários, clientes, veículos, serviços e peças com estoque. O seed não roda no perfil `prod` nem nos testes de integração.

Todos os usuários de demonstração usam a senha `Siaes@123`:

| E-mail | Papel | Observação |
|---|---|---|
| `admin@siaes.com` | ADMIN | Acesso total |
| `gerente@siaes.com` | MANAGER | Gestão e métricas |
| `mecanico@siaes.com` | MECHANIC | Diagnóstico e execução |
| `joao.silva@siaes.com` | CUSTOMER | Cliente João da Silva (CPF 529.982.247-25, veículo ABC1D23) |

Também ficam cadastrados a Transportadora Rápida Ltda (CNPJ 11.222.333/0001-81, veículo XYZ4567), três serviços (`TROCA-OLEO`, `ALINHAMENTO`, `REVISAO-FREIOS`) e três peças com estoque (`OLEO-5W30-1L`, `FILTRO-OLEO`, `PASTILHA-FREIO`).

Faça login:

```bash
curl -X POST http://localhost:8715/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@siaes.com", "password": "Siaes@123"}'
```

A resposta traz `token` e `refreshToken`. Use o `token` no cabeçalho `Authorization: Bearer <token>` das demais chamadas, ou no botão **Authorize** do Swagger.

## Documentação da API

- Swagger UI: http://localhost:8715/swagger-ui.html
- OpenAPI (JSON): http://localhost:8715/v3/api-docs

Para testar no Swagger, faça login em `POST /api/v1/auth/login` com um usuário de demonstração (perfil `dev`, senha `Siaes@123` para todos) e cole o `token` em **Authorize**:

| E-mail | Senha | Papel |
|---|---|---|
| `admin@siaes.com` | `Siaes@123` | ADMIN |
| `gerente@siaes.com` | `Siaes@123` | MANAGER |
| `mecanico@siaes.com` | `Siaes@123` | MECHANIC |
| `joao.silva@siaes.com` | `Siaes@123` | CUSTOMER |

As mensagens de erro seguem o formato `ProblemDetail` (RFC 9457) e são traduzidas pelo cabeçalho `Accept-Language` (`pt-BR`, padrão, ou `en`).

### Postman

A collection [`docs/postman/siaes.postman_collection.json`](docs/postman/siaes.postman_collection.json) tem todos os endpoints, na ordem do fluxo de uma ordem de serviço: cadastros, abertura da OS, diagnóstico, orçamento (rejeitado pelo cliente logado e depois aprovado pelo link público), execução, entrega, métricas, cancelamento e exclusões.

1. Importe o arquivo no Postman. No perfil `dev`, o administrador de demonstração já existe (veja [Primeiro acesso](#primeiro-acesso)).
2. Confira as variáveis da collection `baseUrl`, `adminEmail` e `adminPassword`.
3. Rode a collection inteira pelo **Collection Runner**. Cada requisição guarda os ids que as seguintes usam, e os dados de teste (CPF, placa, e-mails, SKU) são gerados a cada execução.

## Documentação de arquitetura e DDD

| Documento | Conteúdo |
|---|---|
| [docs/requisitos.md](docs/requisitos.md) | Requisitos funcionais e não funcionais, regras de negócio, limitações conhecidas e rastreabilidade com o enunciado |
| [docs/arquitetura/hld.md](docs/arquitetura/hld.md) | High-Level Design: atributos de qualidade, estilo arquitetural, contextos, integração, dados, segurança, implantação e evolução |
| [docs/arquitetura/lld.md](docs/arquitetura/lld.md) | Low-Level Design: estrutura de pacotes, convenções, ciclo da requisição, tokens, matriz de acesso, eventos, estoque, modelo de dados, erros e configuração |
| [docs/adr/](docs/adr/README.md) | Architecture Decision Records: as 14 decisões de arquitetura, com alternativas e consequências |
| [docs/arquitetura/c4.md](docs/arquitetura/c4.md) | Diagramas C4: contexto, contêineres, componentes e código, mais o pipeline de entrega |
| [docs/ddd/event-storming.md](docs/ddd/event-storming.md) | Event Storming da criação da OS, do acompanhamento e da gestão de peças e insumos |
| [docs/ddd/domain-storytelling.md](docs/ddd/domain-storytelling.md) | Domain Storytelling dos fluxos: atendimento, diagnóstico e orçamento, aprovação, execução e entrega, peças e insumos, métricas |
| [docs/ddd/modelo-de-dominio.md](docs/ddd/modelo-de-dominio.md) | Subdomínios, mapa de contextos, agregados, entidades, objetos de valor, eventos, ciclos de vida, atores e a relação com o código |
| [docs/ddd/linguagem-ubiqua.md](docs/ddd/linguagem-ubiqua.md) | Linguagem ubíqua e glossário |
| [docs/ddd/event-storming.pdf](docs/ddd/event-storming.pdf) | Exportação em PDF do Event Storming |
| [docs/ddd/diagramas.pdf](docs/ddd/diagramas.pdf) | Exportação em PDF do modelo de domínio e dos diagramas C4 |
| Miro | _link do quadro a definir_ |

## Vídeo da entrega

_Link do YouTube a definir._

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

Para barrar vulnerabilidades no próprio build, use o profile `security`. Ele roda o Trivy, via Docker, sobre o jar empacotado na fase `verify` e falha com achados HIGH ou CRITICAL, antes do `install`:

```bash
mvn clean install -Psecurity
mvn clean install -Psecurity -Dtrivy.severity=MEDIUM,HIGH,CRITICAL   # mais rígido
```

O resultado do script fica em `docs/security/trivy-report.md`, com o resumo por severidade e, para cada CVE, o pacote, a versão instalada e a versão que corrige. Os JSON brutos (`docs/security/*.json`) não são versionados. O layout do relatório está em `devops/trivy/report.md.tpl`.

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
├── db/seed/         # dados de demonstração (só no perfil dev)
└── messages/        # mensagens i18n (pt_BR e en)
devops/              # docker-compose, init do PostgreSQL, script e template do Trivy
docs/                # enunciado, arquitetura (C4), DDD, collection do Postman e relatório de segurança
```
