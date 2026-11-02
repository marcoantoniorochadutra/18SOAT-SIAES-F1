# ADR-0013: Imagem Docker com runtime jlink sobre Alpine

- Status: Aceita
- Data: 2026-11-01

## Contexto

O enunciado exige Dockerfile e docker-compose. A imagem deve ser pequena, ter pouca superfície de ataque (o relatório de vulnerabilidades analisa a imagem) e rodar sem privilégios.

## Decisão

Dockerfile em três estágios:

1. **build** (`maven:3.9-eclipse-temurin-26-alpine`): empacota o jar com cache do `.m2` e extrai as camadas do Spring Boot.
2. **runtime** (`eclipse-temurin:26-jdk-alpine`): `jdeps` descobre os módulos usados e `jlink` gera um runtime Java mínimo, com os locales `en` e `pt-BR`.
3. **final** (`alpine:3.23`): só o runtime gerado e as camadas da aplicação, usuário `siaes` sem privilégios, `HEALTHCHECK` no `/v3/api-docs` e limite de memória relativo ao contêiner (`MaxRAMPercentage=75`).

O `devops/docker-compose.yml` sobe a aplicação, o PostgreSQL e o Mailpit.

## Alternativas consideradas

- **Imagem JRE completa** (`eclipse-temurin:26-jre`): mais simples, mas maior e com mais pacotes para vulnerabilidades.
- **Distroless (glibc)**: menor superfície, mas o JDK Temurin Alpine é musl e o runtime do jlink precisa rodar sobre musl.
- **Imagem nativa (GraalVM)**: subida rápida, mas build lento e restrições com reflexão e bibliotecas.

## Consequências

- Positivas: imagem enxuta, com 0 vulnerabilidades no Trivy na última análise; camadas que aproveitam o cache entre builds.
- Negativas: um módulo Java novo não detectado pelo `jdeps` (uso por reflexão) só aparece em execução e precisa entrar na lista do `jlink`; o build da imagem não roda os testes, que ficam no CI.
