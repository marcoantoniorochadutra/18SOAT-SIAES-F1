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
- Spring Boot Actuator, Micrometer, Prometheus e Grafana para métricas
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

Sobem cinco contêineres:

| Serviço | Endereço |
|---|---|
| API (`siaes-app`) | http://localhost:8715 |
| PostgreSQL (`siaes-postgres`) | `localhost:5432`, banco `siaes`, usuário `siaes` |
| Mailpit (`siaes-mailpit`) | SMTP em `localhost:1025`, caixa de entrada em http://localhost:8025 |
| Prometheus (`siaes-prometheus`) | http://localhost:9090 |
| Grafana (`siaes-grafana`) | http://localhost:3000, usuário `admin`, senha `admin` |

Os detalhes do monitoramento estão em [Observabilidade](#observabilidade).

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
| `MANAGEMENT_PORT` | `8716` (Actuator: health e métricas) | |
| `GRAFANA_ADMIN_PASSWORD` | `admin` (só no docker-compose) | |
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

### Dados para apresentação (opcional)

O script `devops/seed/demo-apresentacao.sql` carrega um cenário completo para demonstrar o sistema. Ele **não** roda quando a aplicação sobe: fica fora do Flyway e é executado à mão, depois que a aplicação subir no perfil `dev` (ele usa os usuários do seed acima e para com erro se eles não existirem).

```bash
docker compose -f devops/docker-compose.yml exec -T siaes-postgres \
  psql -U siaes -d siaes -v ON_ERROR_STOP=1 < devops/seed/demo-apresentacao.sql
```

Rodar de novo não duplica nada. As datas são relativas ao momento da execução, então o cenário sempre parece recente. O que ele traz:

| Item | Conteúdo |
|---|---|
| Clientes | 5 novos (4 com CPF e uma auto escola com CNPJ), 6 veículos com placas no padrão antigo e Mercosul |
| Catálogo | 4 serviços, um deles inativo; 7 peças com estoque, uma inativa e uma (`DISCO-FREIO`) zerada |
| OS em cada status | Recebida; em diagnóstico sem e com orçamento em rascunho; aguardando aprovação com link ativo; aguardando após rejeição; em execução; finalizada; entregue; cancelada |
| Histórico | 4 OS entregues e 1 finalizada nos últimos 20 dias, que alimentam o tempo médio de execução; uma delas com orçamento rejeitado e refeito |
| Decisões | Aprovações pelo link e pela conta do João (`joao.silva@siaes.com`), rejeições com motivo e um rascunho descartado pelo cancelamento |
| Estoque | Saldo inicial, entrada, ajuste com motivo, reservas, liberações e baixas, coerentes com os orçamentos |

Para aprovar ao vivo o orçamento que aguarda a cliente Maria Oliveira, gere o link com `POST /api/v1/quotations/{id}/approvalLink` (como mecânico) e confirme com os 4 últimos dígitos do CPF dela: `8909`.

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

A collection [`docs/postman/siaes.postman_collection.json`](docs/postman/siaes.postman_collection.json) tem duas pastas:

| Pasta | Conteúdo |
|---|---|
| **APIs** | Todos os endpoints, agrupados por recurso (01 a 12): cadastros, abertura da OS, diagnóstico, orçamento (rejeitado pelo cliente logado e depois aprovado pelo link público), execução, entrega, métricas, cancelamento e exclusões |
| **Fluxo Real** | Só as chamadas do atendimento de uma OS, do serviço solicitado até a entrega, com o papel de cada ator: o gerente cadastra cliente e veículo, o mecânico conduz a OS e o cliente aprova pelo link. Separada em etapas: criação da OS, diagnóstico e orçamento, decisão do cliente, execução e entrega |

1. Importe o arquivo no Postman. No perfil `dev`, os usuários e o catálogo de demonstração já existem (veja [Primeiro acesso](#primeiro-acesso)).
2. Confira a variável `baseUrl`.
3. Rode cada pasta pelo **Collection Runner**, de cima para baixo. Cada requisição guarda os ids que as seguintes usam, e os dados de teste (CPF, placa, e-mails, SKU) são gerados a cada execução.

O Fluxo Real cadastra um cliente novo a cada execução. Para mostrar o caminho do cliente que já passou pela oficina, preencha as variáveis `flowDocumentInput` e `flowPlateInput`, por exemplo com o CPF `52998224725` e a placa `ABC1D23` do João: o fluxo identifica o cliente e o veículo e vai direto à abertura da OS.

## Documentação de arquitetura e DDD

### DDD

O Event Storming foi feito no Miro e exportado para o repositório.

| Documento | Conteúdo |
|---|---|
| Miro | _link do quadro a definir_ |
| [docs/ddd/Event Storming.pdf](<docs/ddd/Event Storming.pdf>) | Exportação do quadro do Miro, em alta resolução |
| [docs/ddd/linguagem-ubiqua.md](docs/ddd/linguagem-ubiqua.md) | Linguagem ubíqua e glossário: cada termo do negócio com o nome correspondente no código, os status, os papéis, os eventos e os termos evitados |

O quadro segue as etapas do Event Storming, cada uma acrescentando uma camada sobre a anterior:

1. **Brainstorm** dos eventos, separado pelos três fluxos pedidos (criação da OS, acompanhamento da OS e gestão de peças e insumos), cada um com a sua linha do tempo.
2. **Linha do tempo** completa, com o caminho padrão e os alternativos: cliente já cadastrado, orçamento rejeitado e refeito, link revogado e cancelamento.
3. **Dúvidas e pontos de atenção**, como quando reservar as peças e o que acontece na rejeição.
4. **Eventos pivotais**, que separam as fases do negócio: peça cadastrada, OS recebida, orçamento concluído, orçamento aprovado e OS finalizada.
5. **Comandos e políticas**, com o ator de cada comando.
6. **Modelos de leitura e sistemas externos**.
7. **Agregados e contextos delimitados**: Catálogo de serviços e peças, Estoque, Cadastro de clientes e veículos, Identidade e acesso, Orçamento e aprovação, e Gestão da OS.

Onde está cada item que o enunciado pede na documentação DDD:

| Item | Onde |
|---|---|
| Event Storming da criação da OS, do acompanhamento e da gestão de peças e insumos | Event Storming, seções 1 e 2 |
| Eventos de domínio | Event Storming (cartões laranja) e linguagem ubíqua, seção "Eventos de domínio" |
| Comandos, atores e políticas | Event Storming, seção 5 |
| Agregados e contextos delimitados | Event Storming, seção 7, e C4 nível 3 |
| Entidades e objetos de valor | C4 nível 4 (diagrama de classes) e linguagem ubíqua |
| Linguagem ubíqua e glossário | [docs/ddd/linguagem-ubiqua.md](docs/ddd/linguagem-ubiqua.md) |
| Relação com o código | Linguagem ubíqua (coluna "No código") e requisitos (coluna "Onde está") |

### Arquitetura

| Documento | Conteúdo |
|---|---|
| [C4 nível 1: contexto](<docs/arquitetura/C4 - 2. Diagrama Contexto.pdf>) | Pessoas que usam o SIAES e sistemas externos |
| [C4 nível 2: contêineres](<docs/arquitetura/C4 - 1. Diagrama Container.pdf>) | API, banco de dados, servidor SMTP, Prometheus e Grafana |
| [C4 nível 3: componentes](<docs/arquitetura/C4 - 3. Diagrama de Componente.pdf>) | Os contextos dentro da API e como se relacionam |
| [C4 nível 4: classes](<docs/arquitetura/C4 - 4. Diagrama de Classes.pdf>) | Agregados, entidades, objetos de valor e enums do domínio |
| [docs/adr/](docs/adr/README.md) | Architecture Decision Records: as 15 decisões de arquitetura, com alternativas e consequências |
| [docs/requisitos.md](docs/requisitos.md) | Requisitos funcionais e não funcionais, regras de negócio, limitações conhecidas e rastreabilidade com o enunciado |

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

## Observabilidade

A API expõe métricas pelo Spring Boot Actuator, no formato do Prometheus. O Prometheus coleta a cada 15 segundos, e o Grafana já sobe com a fonte de dados e o painel **SIAES - Visão geral**, na pasta SIAES. A decisão está no [ADR-0015](docs/adr/0015-observabilidade-prometheus-grafana.md).

| O quê | Onde |
|---|---|
| Painel | http://localhost:3000, usuário `admin`, senha `admin` (troque com `GRAFANA_ADMIN_PASSWORD` fora do desenvolvimento) |
| Consultas e alvos do Prometheus | http://localhost:9090, com o job `siaes` em http://localhost:9090/targets |
| Métricas e health da API | `/actuator/prometheus`, `/actuator/health/liveness` e `/actuator/health/readiness`, na porta de gerenciamento `8716` |

A porta 8716 não é publicada no compose: só o Prometheus a alcança, pela rede interna, e a porta da API (8715) não serve o Actuator. Para ver as métricas cruas:

```bash
docker compose -f devops/docker-compose.yml exec siaes-app wget -qO- localhost:8716/actuator/prometheus
```

O painel mostra:

- requisições por segundo, taxa de erro 5xx, respostas 4xx e latência p95 por endpoint;
- memória heap, threads e conexões do pool;
- eventos de domínio por tipo (counter `siaes_domain_events_total`), incluindo orçamentos aprovados e rejeitados.

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
devops/              # docker-compose, init do PostgreSQL, Prometheus, Grafana, script e template do Trivy
docs/                # enunciado, arquitetura (C4), DDD, collection do Postman e relatório de segurança
```
