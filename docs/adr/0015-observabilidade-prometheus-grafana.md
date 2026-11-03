# ADR-0015: Observabilidade com Prometheus e Grafana

- Status: Aceita
- Data: 2026-11-02

## Contexto

A API não expunha métricas. O `application-dev.yml` tinha um bloco do Datadog sem a dependência correspondente, ou seja, configuração morta. A oficina precisa acompanhar a saúde, a latência e o volume da API, e a equipe técnica precisa de um painel pronto ao subir o ambiente.

O `UserAuthenticationFilter` protege só as rotas servidas pelo `RequestMappingHandlerMapping` (os controllers). Os endpoints do Actuator usam outro handler mapping, então, na porta da API, eles ficariam abertos sem autenticação.

## Decisão

- **Métricas** com Spring Boot Actuator e o registry Prometheus do Micrometer. Toda série recebe o label `application="siaes"`, e as requisições HTTP têm histograma de latência para cálculo de p95 e p99.
- **Porta de gerenciamento separada** (`management.server.port`, padrão 8716). No compose ela não é publicada: só o Prometheus a alcança, pela rede interna. A porta da API (8715) não serve o Actuator.
- **Exposição mínima:** `health`, `info` e `prometheus`. Health com probes de liveness e readiness e sem detalhes. O `HEALTHCHECK` da imagem usa `/actuator/health/liveness`.
- **Métrica de negócio** `siaes_domain_events_total{event}`, contada por um único listener transversal (`DomainEventMetricsSubscriber`) depois do commit da transação. Domínio e app services não mudam, e um evento de transação desfeita não é contado.
- **Prometheus e Grafana** no `devops/docker-compose.yml`, com tags fixas. O Grafana sobe com a fonte de dados e o painel "SIAES - Visão geral" provisionados por arquivo, em `devops/grafana/`.
- **Testes:** o health check de e-mail do Actuator é desligado no perfil `it`, porque os testes de integração substituem o `JavaMailSender` por um mock. O `ActuatorIT` sobe a aplicação com servidor real e confere que as métricas respondem na porta de gerenciamento e não na da API.

## Alternativas consideradas

- **Datadog:** serviço pago, com agente externo. Era a configuração morta que existia.
- **OpenTelemetry Collector:** traria traces e logs junto, mas com mais peças do que o necessário agora. O Micrometer permite migrar depois sem mudar as métricas.
- **Actuator na porta da API com autenticação:** exigiria estender o `UserAuthenticationFilter` ao handler mapping do Actuator e entregar um token ao Prometheus.

## Consequências

- Positivas: painel pronto com `docker compose up`; as métricas ficam isoladas da rede pública sem mexer na segurança da API; o counter de eventos dá visão de negócio (orçamentos aprovados e rejeitados, OS canceladas) sem tocar no domínio.
- Negativas: mais dois contêineres no ambiente de desenvolvimento; a senha padrão do Grafana (`admin`) vale só para desenvolvimento e precisa ser trocada por `GRAFANA_ADMIN_PASSWORD` fora dele; o Prometheus guarda só 7 dias de séries.
