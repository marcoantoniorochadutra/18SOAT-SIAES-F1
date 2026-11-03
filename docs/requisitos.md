# Análise de requisitos

Requisitos funcionais e não funcionais do SIAES, levantados a partir do enunciado do Tech Challenge da Fase 1 ([`docs/Fase 1 - Tech Challenge (1).pdf`](Fase%201%20-%20Tech%20Challenge%20(1).pdf)), com o que o código entrega para cada um, onde está e como verificar.

Legenda de situação: ✅ atendido · ⚠️ atendido com ressalva (ver [limitações](#limitações-conhecidas)) · ➕ além do pedido.

## Contexto

Uma oficina mecânica de médio porte controla atendimento, diagnóstico, execução e entrega com anotações e planilhas. Isso causa erros de priorização, falhas no controle de peças, falta de visibilidade do status, perda de histórico e um fluxo de orçamento ineficiente. O SIAES é o MVP do back-end que organiza esse fluxo em torno da Ordem de Serviço (OS).

### Atores

| Ator | Papel (`UserRole`) | Interesse principal |
|---|---|---|
| Administrador | `ADMIN` | Operação completa do sistema |
| Gerente | `MANAGER` | Cadastros, catálogo, estoque, usuários e métricas |
| Mecânico | `MECHANIC` | Abrir e conduzir a OS: diagnóstico, orçamento, finalização, entrega. Consulta clientes, veículos, catálogo e saldo de estoque, sem alterá-los |
| Cliente com conta | `CUSTOMER` | Acompanhar as próprias OS e decidir os próprios orçamentos |
| Cliente sem conta | — | Decidir o orçamento pelo link recebido |
| Sistema | — | Reserva de estoque, mudança automática de status, envio do link, histórico |

## Requisitos funcionais

### Criação da Ordem de Serviço

| ID | Requisito | Como o sistema atende | Onde está | Situação |
|---|---|---|---|---|
| RF01 | Identificar o cliente por CPF ou CNPJ | Cadastro com documento validado e único; busca por documento em `GET /customers?document=` e `GET /workOrders?document=` | `customer/domain/model/Customer.java`, `sk/document/domain/Document.java` | ⚠️ |
| RF02 | Cadastrar o veículo com placa, marca, modelo e ano | `POST /api/v1/vehicles`, vinculado a um cliente | `vehicle/domain/model/Vehicle.java`, `Plate.java` | ✅ |
| RF03 | Abrir a OS | `POST /api/v1/workOrders` com cliente e veículo; nasce em `RECEIVED`; o veículo precisa pertencer ao cliente | `WorkOrder.java`, `CreateWorkOrderAppService` | ✅ |
| RF04 | Incluir os serviços solicitados | Itens de serviço do catálogo no orçamento (`QuotationMaintenance`) | `QuotationController` (`/api/v1/quotations`), `QuotationAssembler` | ✅ |
| RF05 | Incluir peças e insumos | Itens de peça no orçamento (`QuotationSupplies`), com reserva imediata no estoque | `QuotationStockHelper`, `StockItem.reserve` | ✅ |
| RF06 | Gerar o orçamento automaticamente | Preço unitário copiado do catálogo no momento da inclusão; total = soma dos itens (`getTotalAmount`) | `QuotationAssembler`, `Quotation.java` | ✅ |
| RF07 | Enviar o orçamento ao cliente para aprovação | Ao concluir (`POST /quotations/{id}/completion`), o sistema gera um link assinado, válido por 72h, e envia por e-mail; reenvio em `POST /quotations/{id}/approvalLink` | `SendQuotationForApprovalAppService`, `EmailQuotationApprovalNotifier` | ✅ |
| RF08 | Registrar a aprovação ou rejeição | Dois canais (`QuotationDecisionChannel`): cliente logado (`PATCH /quotations/{id}/decision`) e link público (`POST /external/quotations/{token}/decision`, confirmando os 4 últimos dígitos do documento). Registra canal, autor e data | `DecideQuotation*AppService`, `QuotationExternalController` | ✅ |

### Acompanhamento da Ordem de Serviço

| ID | Requisito | Como o sistema atende | Onde está | Situação |
|---|---|---|---|---|
| RF09 | Status Recebida, Em diagnóstico, Aguardando aprovação, Em execução, Finalizada e Entregue | `RECEIVED`, `IN_DIAGNOSIS`, `AWAITING_APPROVAL`, `IN_EXECUTION`, `FINISHED`, `DELIVERED`, mais `CANCELLED` | `WorkOrderStatus.java` | ✅ ➕ |
| RF10 | Controlar as alterações de status e aplicar as regras de transição | Só transições permitidas (`canChangeTo`); não existe endpoint para trocar status à mão | `WorkOrder.transitionTo` | ✅ |
| RF11 | Alterar o status automaticamente conforme as ações | Diagnóstico → `IN_DIAGNOSIS`; orçamento concluído → `AWAITING_APPROVAL`; aprovado → `IN_EXECUTION`; finalização → `FINISHED`; entrega → `DELIVERED`. As mudanças disparadas pelo orçamento chegam por eventos de domínio | `QuotationCompletedSubscriber`, `QuotationApprovedSubscriber` (workorder) | ✅ |
| RF12 | Permitir ao cliente consultar o andamento via API | `GET /workOrders` e `GET /workOrders/{id}` com papel `CUSTOMER` retornam só as OS do próprio cliente, com o histórico de status | `WorkOrderController`, `WorkOrderStatusHistory` | ✅ |
| RF13 | Cancelar a OS | `POST /workOrders/{id}/cancellation` antes da execução; descarta orçamentos em rascunho e libera as reservas | `WorkOrder.cancel`, `WorkOrderCancelledSubscriber` | ➕ |

### Gestão administrativa

| ID | Requisito | Como o sistema atende | Onde está | Situação |
|---|---|---|---|---|
| RF14 | CRUD de clientes | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` em `/api/v1/customers`; a exclusão propaga para veículos, usuários, OS e orçamentos | `CustomerController`, `CustomerDeletedEvent` | ✅ |
| RF15 | CRUD de veículos | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` em `/api/v1/vehicles` | `VehicleController` | ✅ |
| RF16 | CRUD de serviços | `POST`, `GET`, `GET /{id}`, `PUT /{id}` e ativar/inativar em `/api/v1/services`. A remoção é lógica, para preservar orçamentos antigos | `MaintenanceController` | ✅ |
| RF17 | CRUD de peças e insumos | `POST`, `GET`, `GET /{id}`, `PUT /{id}` e ativar/inativar em `/api/v1/supplies`, com remoção lógica | `SuppliesController` | ✅ |
| RF18 | Controle de estoque | Saldo, reservado e disponível; entrada, ajuste com motivo, reserva, liberação e baixa; histórico de movimentações (`GET /supplies/{id}/stock/movements`) | `StockItem`, `StockMovement`, `StockController` | ⚠️ |
| RF19 | Listar Ordens de Serviço | `GET /api/v1/workOrders` paginado, com filtros | `WorkOrderController` | ✅ |
| RF20 | Detalhar Ordem de Serviço | `GET /api/v1/workOrders/{id}`, com cliente, veículo, diagnóstico e histórico de status | `WorkOrderController` | ✅ |
| RF21 | Monitorar o tempo médio de execução | `GET /api/v1/workOrders/metrics/executionTime`: quantidade de OS, média, mínimo e máximo de execução e média por status, com filtro de período (`from`, `to`) | `ExecutionTimeMetrics`, `StatusTimeline` | ✅ |
| RF22 | Gerenciar usuários e autenticação | Cadastro de usuários (`POST /api/v1/users`), login, refresh e logout (`/api/v1/auth`) | `UserController`, `AuthenticationController` | ➕ |

### Regras de negócio

| ID | Regra | Onde é garantida |
|---|---|---|
| RN01 | CPF/CNPJ com dígitos verificadores válidos e único por cliente | `Document`, `Customer` |
| RN02 | Placa no padrão antigo (`AAA9999`) ou Mercosul (`AAA9A99`), única | `Plate` |
| RN03 | O veículo da OS precisa pertencer ao cliente da OS | `CreateWorkOrderAppService` (`WorkOrderVehicleCustomerMismatchException`) |
| RN04 | O orçamento só é editado em rascunho e só aceito com a OS em diagnóstico ou aguardando aprovação | `Quotation`, `WorkOrder` |
| RN05 | Serviços e peças inativos não entram em novos orçamentos | `QuotationAssembler` |
| RN06 | Reserva só com saldo disponível; `0 ≤ reservado ≤ estoque`; aprovação consome a reserva, rejeição e cancelamento liberam | `StockItem` |
| RN07 | Ajuste de estoque exige motivo e não pode consumir unidades reservadas | `StockItem.adjust` |
| RN08 | O link de aprovação é de uso único, expira em 72h e é revogado após 5 tentativas erradas dos dígitos do documento | `QuotationApprovalToken` |
| RN09 | A OS não é cancelada com orçamento aguardando decisão do cliente | `WorkOrder.cancel` (`WorkOrderOpenQuotationException`) |
| RN10 | Toda mudança de status de OS, orçamento e usuário fica no histórico | `*StatusHistory` |

## Requisitos não funcionais

| ID | Categoria | Requisito | Como o sistema atende | Evidência | Situação |
|---|---|---|---|---|---|
| RNF01 | Arquitetura | Back-end monolítico organizado em camadas | Monólito modular: um pacote por contexto delimitado, cada um com `domain`, `application` e `infrastructure` (hexagonal). O domínio não depende de Spring | [`docs/arquitetura/c4.md`](arquitetura/c4.md) | ✅ |
| RNF02 | Persistência | Banco de livre escolha, com justificativa | PostgreSQL 17, com o schema versionado pelo Flyway e `ddl-auto: validate` | README, seção "Por que PostgreSQL" | ✅ |
| RNF03 | Integridade e transações | Tratar as transações dos fluxos principais | App services com `@Transactional`; agregados que mudam juntos (orçamento, estoque e OS) mudam na mesma transação por eventos síncronos; efeitos secundários (histórico de estoque, e-mail) rodam após o commit (`AFTER_COMMIT`); timeout padrão de 30s | `sk/domain/event`, `*Subscriber` | ✅ |
| RNF04 | Concorrência | Evitar atualizações perdidas e estoque negativo | Lock otimista (`@Version`) em OS, orçamento e estoque; chaves únicas e `CHECK` no banco | `*JpaEntity`, migrações em `db/migration` | ✅ |
| RNF05 | Segurança: autenticação | JWT nas APIs administrativas | Access token JWT (HMAC) e refresh token rotativo; logout revoga o token | `JwtTokenIssuer`, `UserAuthenticationFilter`, `LogoutUserAppService` | ✅ |
| RNF06 | Segurança: autorização | Acesso por papel | `@Authentication(minimumRole, deniedRoles)` em cada endpoint; endpoint sem anotação é recusado com 401; o cliente só vê os próprios dados | `@Authentication`, `@PublicEndpoint` | ✅ |
| RNF07 | Segurança: dados sensíveis | Validar CPF/CNPJ e placa | Value objects `Document` e `Plate`, mais Bean Validation na entrada | `sk/document`, `vehicle/domain/model/vo` | ✅ |
| RNF08 | Segurança: credenciais | Senhas e segredos protegidos | Senha com política forte e BCrypt; segredos do JWT e do link por variável de ambiente, obrigatórios no perfil `prod`; link público assinado (HMAC) | `Password`, `application-prod.yml` | ✅ |
| RNF09 | Segurança: vulnerabilidades | Análise de vulnerabilidades com relatório | Trivy no repositório e na imagem: 0 achados; `mvn clean install -Psecurity` falha com HIGH ou CRITICAL; job do Trivy no CI | [`docs/security/trivy-report.md`](security/trivy-report.md) | ✅ |
| RNF10 | Qualidade: testes | Testes unitários e de integração dos fluxos principais | 824 testes unitários (JUnit 5 e Mockito) e 65 de integração (`*IT`, contra PostgreSQL real) | `mvn verify` | ✅ |
| RNF11 | Qualidade: cobertura | Mínimo de 80% nos domínios críticos | JaCoCo com verificação de 80% de linhas que quebra o build; último resultado: 99,0% de linhas e 94,4% de branches | `target/site/jacoco/index.html` | ✅ |
| RNF12 | Qualidade: análise estática | Código revisado continuamente | SonarCloud no CI; `main` protegida, com alterações só por pull request | `.github/workflows/ci.yml` | ✅ |
| RNF13 | Interoperabilidade | APIs RESTful documentadas | REST com versionamento (`/api/v1`), paginação padronizada e Swagger UI (springdoc) com textos em pt_BR e en | `http://localhost:8715/swagger-ui.html`, `*ControllerOpenApi` | ✅ |
| RNF14 | Usabilidade da API | Erros claros e consistentes | `GlobalExceptionHandler` com `ProblemDetail` (RFC 9457) e mensagens internacionalizadas (pt_BR e en) | `sk/infrastructure/web`, `messages/` | ✅ |
| RNF15 | Implantação | Dockerfile e docker-compose com o ambiente completo | Imagem multi-stage com runtime enxuto (jlink), usuário sem privilégios e `HEALTHCHECK`; compose com aplicação, PostgreSQL e Mailpit | `Dockerfile`, `devops/docker-compose.yml` | ✅ |
| RNF16 | Manutenibilidade | Execução local simples | README com pré-requisitos, execução, testes, Swagger, variáveis de ambiente e usuários de demonstração (seed no perfil `dev`) | `readme.md` | ✅ |
| RNF17 | Desempenho | Responder bem sob carga moderada | Threads virtuais, pool Hikari configurável, `open-in-view` desligado, consultas paginadas, efeitos secundários assíncronos | `application.yml`, `AsyncConfig` | ✅ |
| RNF18 | Disponibilidade | Detectar falha e desligar sem perder requisições | `HEALTHCHECK` no contêiner e `shutdown: graceful` | `Dockerfile`, `application.yml` | ✅ |
| RNF19 | Auditabilidade | Rastrear quem fez o quê e quando | Histórico de status de OS, orçamento e usuário; histórico de movimentações de estoque; canal e autor da decisão do orçamento | `*StatusHistory`, `StockMovement` | ✅ |
| RNF20 | Internacionalização | Mensagens em mais de um idioma | Bundles pt_BR e en para erros, validações, Swagger e notificações | `src/main/resources/messages` | ➕ |

## Restrições

- Java 26 e Spring Boot 4.0; build com Maven.
- Entrega como MVP de back-end, sem front-end; o "aplicativo" do cliente é representado pela API e pelo link de aprovação.
- O link de aprovação é enviado só por e-mail; sem e-mail cadastrado, a oficina repassa o link manualmente.

## Limitações conhecidas

| Item | Requisito | Situação | Impacto |
|---|---|---|---|
| Abertura da OS por id do cliente | RF01 | A OS é criada com `customerId` e `vehicleId`. O CPF/CNPJ identifica o cliente na busca (`?document=`), que devolve o id usado na abertura | Um passo a mais para quem atende; a regra de identificação por documento continua valendo |
| Reserva de estoque sem referência por orçamento | RF18 | `StockItem` valida liberação e baixa contra o total reservado, não contra a reserva de cada orçamento | Em cenário anômalo, um orçamento poderia liberar unidades reservadas por outro; o saldo total nunca fica negativo |
| Orçamento sem resposta | RF08 | O link expira em 72h, mas o orçamento continua aguardando decisão | A OS fica em `AWAITING_APPROVAL` até a decisão; um novo link pode ser gerado |
| Ciclo de dependência entre `auth`, `user` e `customer` | RNF01 | `AuthenticatedUser` usa ids de outros contextos | Acoplamento entre contextos de suporte; não afeta os fluxos |

## Rastreabilidade com o enunciado

| Seção do enunciado | Requisitos |
|---|---|
| Criação da Ordem de Serviço | RF01 a RF08 |
| Acompanhamento da Ordem de Serviço | RF09 a RF12 |
| Gestão administrativa | RF14 a RF21 |
| Segurança e qualidade | RNF05 a RNF11 |
| Requisitos técnicos | RNF01 a RNF03, RNF13, RNF15, RNF16 |
| Entregáveis (vulnerabilidades, Swagger, README) | RNF09, RNF13, RNF16 |
