# ADR-0014: Análise de vulnerabilidades com Trivy

- Status: Aceita
- Data: 2026-11-01

## Contexto

O enunciado exige relatório de análise de vulnerabilidades. As dependências do Spring Boot recebem CVEs com frequência, e a imagem inclui pacotes do sistema operacional.

## Decisão

- Trivy, em contêiner (versão fixada por digest), em três pontos:
  - `devops/security-scan.sh`: analisa o repositório (segredos e configuração do Dockerfile/compose) e a imagem, e gera `docs/security/trivy-report.md`.
  - Profile Maven `security` (`mvn clean install -Psecurity`): analisa o jar empacotado e falha o build com achados HIGH ou CRITICAL, antes do `install`.
  - CI: job dedicado que publica o SARIF na aba Security do GitHub e o relatório como artefato.

## Alternativas consideradas

- **OWASP Dependency-Check**: plugin Maven nativo, mas exige chave da API do NVD, baixa a base inteira na primeira execução e não analisa a imagem.
- **Snyk / Dependabot**: bons para alertas contínuos, mas dependem de serviço externo e não geram o relatório versionado pedido.

## Consequências

- Positivas: uma ferramenta para dependências, imagem, segredos e configuração; relatório versionado como evidência; o build local pode barrar vulnerabilidades.
- Negativas: exige Docker; o profile é opcional (o build dentro do Dockerfile não tem Docker); o relatório versionado envelhece e precisa ser gerado de novo a cada entrega.
