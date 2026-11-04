# Architecture Decision Records

Decisões de arquitetura do SIAES, no formato: contexto, decisão, alternativas consideradas e consequências. Uma decisão substituída não é apagada: ganha o status "Substituída por ADR-XXXX".

| ADR | Decisão | Status |
|---|---|---|
| [0001](0001-monolito-modular-hexagonal.md) | Monólito modular com arquitetura hexagonal por bounded context | Aceita |
| [0002](0002-postgresql-com-flyway.md) | PostgreSQL com migrações Flyway | Aceita |
| [0003](0003-eventos-de-dominio-em-processo.md) | Integração entre contextos por eventos de domínio em processo | Aceita |
| [0004](0004-referencia-por-id-e-lock-otimista.md) | Agregados referenciados por id e lock otimista | Aceita |
| [0005](0005-autenticacao-jwt-com-refresh-rotativo.md) | Autenticação JWT com refresh token rotativo | Aceita |
| [0006](0006-autorizacao-declarativa-por-anotacao.md) | Autorização declarativa por anotação, recusando por padrão | Aceita |
| [0007](0007-status-da-os-por-acoes-de-negocio.md) | Status da OS alterado só por ações de negócio | Aceita |
| [0008](0008-reserva-de-estoque-no-orcamento.md) | Reserva de estoque na criação do orçamento | Aceita |
| [0009](0009-aprovacao-por-link-assinado.md) | Aprovação do orçamento por link assinado | Aceita |
| [0010](0010-erros-problemdetail-i18n.md) | Erros como ProblemDetail com mensagens i18n | Aceita |
| [0011](0011-paginacao-propria.md) | Paginação própria no shared kernel | Aceita |
| [0012](0012-remocao-logica-do-catalogo.md) | Remoção lógica de serviços e peças | Aceita |
| [0013](0013-imagem-docker-com-jlink.md) | Imagem Docker com runtime jlink sobre Alpine | Aceita |
| [0014](0014-analise-de-vulnerabilidades-com-trivy.md) | Análise de vulnerabilidades com Trivy | Aceita |
| [0015](0015-observabilidade-prometheus-grafana.md) | Observabilidade com Prometheus e Grafana | Aceita |

Visões relacionadas: [C4 nível 2: contêineres](<../arquitetura/C4 - 2. Diagrama Container.pdf>), [C4 nível 3: componentes](<../arquitetura/C4 - 3. Diagrama de Componente.pdf>), [Event Storming](../ddd/event-storming.pdf), [linguagem ubíqua](../ddd/linguagem-ubiqua.md).
