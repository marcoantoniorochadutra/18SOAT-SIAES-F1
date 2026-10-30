# Relatório de análise de vulnerabilidades

| | |
|---|---|
| Data | 2026-10-29 03:12 UTC |
| Commit | `e4de52d` |
| Ferramenta | Trivy 0.76.1 |
| Imagem analisada | `siaes-app:latest` |

## 1. Repositório (segredos e configuração do Dockerfile/compose)

| CRITICAL | HIGH | MEDIUM | LOW | UNKNOWN |
|---|---|---|---|---|
| 0 | 0 | 0 | 1 | 0 |

#### `Dockerfile` (dockerfile)

| Severidade | Regra | Problema | Como resolver |
|---|---|---|---|
| LOW | [DS-0026](https://avd.aquasec.com/misconfig/ds-0026) | No HEALTHCHECK defined | Add HEALTHCHECK instruction in Dockerfile |

## 2. Imagem da aplicação (pacotes do Alpine, runtime Java e dependências)

| CRITICAL | HIGH | MEDIUM | LOW | UNKNOWN |
|---|---|---|---|---|
| 6 | 11 | 11 | 0 | 0 |

#### `Java` (jar)

| Severidade | Vulnerabilidade | Pacote | Versão instalada | Corrigida em | Descrição |
|---|---|---|---|---|---|
| HIGH | [CVE-2026-89407](https://avd.aquasec.com/nvd/cve-2026-89407) | `com.fasterxml.jackson.core:jackson-core` | 2.21.4 | 2.18.11, 2.21.7, 2.22.3 | com.fasterxml.jackson/jackson-core: tools.jackson.core/jackson-core: Jackson-core: Denial of Service via regular expression backtracking |
| HIGH | [CVE-2026-89425](https://avd.aquasec.com/nvd/cve-2026-89425) | `com.fasterxml.jackson.core:jackson-core` | 2.21.4 | 2.21.7, 2.22.3, 2.18.11 | com.fasterxml.jackson.core/jackson-core: Jackson-core: Denial of Service via unbounded StringBuilder growth during malformed token processing |
| HIGH | [CVE-2026-68497](https://avd.aquasec.com/nvd/cve-2026-68497) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.10, 2.21.6, 2.22.2 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: jackson-databind: CPU Denial of Service via unbounded numeric parsing |
| HIGH | [CVE-2026-91776](https://avd.aquasec.com/nvd/cve-2026-91776) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.11, 2.21.7, 2.22.3 | jackson-databind: com.fasterxml.jackson/jackson-core: jackson-databind: Denial of Service via unbounded cache growth in TypeDeserializerBase |
| HIGH | [CVE-2026-91777](https://avd.aquasec.com/nvd/cve-2026-91777) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.21.7, 2.18.11, 2.22.3 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core:jackson-databind: Jackson-databind: Denial of Service via quadratic forward-reference completion |
| MEDIUM | [CVE-2026-19032](https://avd.aquasec.com/nvd/cve-2026-19032) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.10, 2.21.6, 2.22.2 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: Jackson-databind: Uncontrolled URI scheme resolution in Path deserialization |
| MEDIUM | [CVE-2026-54515](https://avd.aquasec.com/nvd/cve-2026-54515) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 3.1.4, 2.18.9, 2.21.5, 2.22.1 | jackson-databind: jackson-databind: Ignored properties can be unexpectedly modified |
| MEDIUM | [CVE-2026-59889](https://avd.aquasec.com/nvd/cve-2026-59889) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.21.5, 2.18.9, 2.22.1 | com.fasterxml.jackson.core/jackson-databind: Jackson-databind: Privilege escalation via improper handling of @JsonUnwrapped properties |
| MEDIUM | [CVE-2026-77310](https://avd.aquasec.com/nvd/cve-2026-77310) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.9, 2.21.5, 2.22.1 | com.fasterxml.jackson.core/jackson-databind: jackson-databind: Server-Side Request Forgery and internal host enumeration via eager DNS resolution |
| MEDIUM | [CVE-2026-83557](https://avd.aquasec.com/nvd/cve-2026-83557) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.10, 2.21.6, 2.22.2 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: jackson-databind: Path traversal via incomplete type validation |
| MEDIUM | [GHSA-mhm7-754m-9p8w](https://github.com/advisories/GHSA-mhm7-754m-9p8w) | `com.fasterxml.jackson.core:jackson-databind` | 2.21.4 | 2.18.9, 2.21.5 | jackson-databind: `@JsonView` bypass for creator properties with `@JsonTypeInfo(include=As.EXTERNAL_PROPERTY)` |
| MEDIUM | [CVE-2026-49844](https://avd.aquasec.com/nvd/cve-2026-49844) | `org.apache.logging.log4j:log4j-api` | 2.25.4 | 2.25.5, 2.26.1 | org.apache.logging.log4j/log4j-api: Apache Log4j API: Malformed JSON output due to improper encoding of floating-point values |
| CRITICAL | [CVE-2026-65182](https://avd.aquasec.com/nvd/cve-2026-65182) | `org.apache.tomcat.embed:tomcat-embed-core` | 11.0.22 | 11.0.25, 10.1.58, 9.0.121 | tomcat: Apache Tomcat: Security constraint bypass due to improper access control |
| CRITICAL | [CVE-2026-65637](https://avd.aquasec.com/nvd/cve-2026-65637) | `org.apache.tomcat.embed:tomcat-embed-core` | 11.0.22 | 11.0.25, 10.1.59, 9.0.121 | tomcat: Apache Tomcat: Improper Input Validation allows HTTP/2 no-authority bypass of strict SNI validation |
| CRITICAL | [CVE-2026-65905](https://avd.aquasec.com/nvd/cve-2026-65905) | `org.apache.tomcat.embed:tomcat-embed-core` | 11.0.22 | 11.0.25, 10.1.58, 9.0.121 | tomcat: Apache Tomcat: Authentication bypass via limited replay attack in DIGEST authenticator |
| CRITICAL | [CVE-2026-68525](https://avd.aquasec.com/nvd/cve-2026-68525) | `org.apache.tomcat.embed:tomcat-embed-core` | 11.0.22 | 11.0.25, 10.1.58, 9.0.121 | tomcat: Apache Tomcat: Unauthorized resource access via FORM authentication bypass |
| HIGH | [CVE-2026-54291](https://avd.aquasec.com/nvd/cve-2026-54291) | `org.postgresql:postgresql` | 42.7.11 | 42.7.12 | org.postgresql/postgresql: com.ongres.scram/scram-client: pgjdbc: Man-in-the-middle protection bypass via SCRAM-SHA-256-PLUS downgrade |
| CRITICAL | [CVE-2026-47884](https://avd.aquasec.com/nvd/cve-2026-47884) | `org.springframework:spring-webmvc` | 7.0.8 | 7.0.9 | org.springframework/spring-webmvc: Spring Framework: Remote Code Execution via improper path limitation in XsltView |
| CRITICAL | [CVE-2026-47890](https://avd.aquasec.com/nvd/cve-2026-47890) | `org.springframework:spring-webmvc` | 7.0.8 | 7.0.9 | org.springframework/spring-webmvc: org.springframework/spring-webflux: Spring Framework: Stream corruption in Server-Sent Events (SSE) with view fragments |
| HIGH | [CVE-2026-89407](https://avd.aquasec.com/nvd/cve-2026-89407) | `tools.jackson.core:jackson-core` | 3.1.4 | 3.1.7, 3.2.2 | com.fasterxml.jackson/jackson-core: tools.jackson.core/jackson-core: Jackson-core: Denial of Service via regular expression backtracking |
| HIGH | [CVE-2026-89425](https://avd.aquasec.com/nvd/cve-2026-89425) | `tools.jackson.core:jackson-core` | 3.1.4 | 3.1.7, 3.2.3 | com.fasterxml.jackson.core/jackson-core: Jackson-core: Denial of Service via unbounded StringBuilder growth during malformed token processing |
| HIGH | [CVE-2026-68497](https://avd.aquasec.com/nvd/cve-2026-68497) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.2.2, 3.1.6 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: jackson-databind: CPU Denial of Service via unbounded numeric parsing |
| HIGH | [CVE-2026-91776](https://avd.aquasec.com/nvd/cve-2026-91776) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.7, 3.2.3 | jackson-databind: com.fasterxml.jackson/jackson-core: jackson-databind: Denial of Service via unbounded cache growth in TypeDeserializerBase |
| HIGH | [CVE-2026-91777](https://avd.aquasec.com/nvd/cve-2026-91777) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.7, 3.2.3 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core:jackson-databind: Jackson-databind: Denial of Service via quadratic forward-reference completion |
| MEDIUM | [CVE-2026-19032](https://avd.aquasec.com/nvd/cve-2026-19032) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.6, 3.2.2 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: Jackson-databind: Uncontrolled URI scheme resolution in Path deserialization |
| MEDIUM | [CVE-2026-59889](https://avd.aquasec.com/nvd/cve-2026-59889) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.5, 3.2.1 | com.fasterxml.jackson.core/jackson-databind: Jackson-databind: Privilege escalation via improper handling of @JsonUnwrapped properties |
| MEDIUM | [CVE-2026-77310](https://avd.aquasec.com/nvd/cve-2026-77310) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.5, 3.2.1 | com.fasterxml.jackson.core/jackson-databind: jackson-databind: Server-Side Request Forgery and internal host enumeration via eager DNS resolution |
| MEDIUM | [CVE-2026-83557](https://avd.aquasec.com/nvd/cve-2026-83557) | `tools.jackson.core:jackson-databind` | 3.1.4 | 3.1.6, 3.2.2 | com.fasterxml.jackson.core/jackson-databind: tools.jackson.core/jackson-databind: jackson-databind: Path traversal via incomplete type validation |
