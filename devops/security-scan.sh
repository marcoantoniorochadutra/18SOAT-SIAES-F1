#!/usr/bin/env bash
set -euo pipefail

IMAGE="${1:-siaes-app:latest}"
OUT_DIR="${2:-docs/security}"
TRIVY_IMAGE="${TRIVY_IMAGE:-aquasec/trivy:0.76.1@sha256:790fd66d1697395d8693d1acaa1ea702406a20f109b6fc7bac8db623eca6c06c}"

cd "$(dirname "$0")/.."
mkdir -p "$OUT_DIR"

export MSYS_NO_PATHCONV=1
ROOT="$(pwd -W 2>/dev/null || pwd)"

trivy() {
  docker run --rm -v "$ROOT:/src" -v trivy-cache:/root/.cache/trivy \
    -v /var/run/docker.sock:/var/run/docker.sock "$TRIVY_IMAGE" "$@"
}

trivy fs --quiet --offline-scan --scanners secret,misconfig \
  --skip-dirs "/src/$OUT_DIR,/src/target,/src/out,/src/graft,/src/.trail,/src/.claude,/src/.git" \
  --format json --output "/src/$OUT_DIR/trivy-repository.json" /src

trivy image --quiet --format json --output "/src/$OUT_DIR/trivy-image.json" "$IMAGE"

report() {
  trivy convert --format template --template "@/src/devops/trivy/report.md.tpl" "/src/$OUT_DIR/$1"
}

{
  echo "# Relatório de análise de vulnerabilidades"
  echo
  echo "| | |"
  echo "|---|---|"
  echo "| Data | $(date -u '+%Y-%m-%d %H:%M UTC') |"
  echo "| Commit | \`$(git rev-parse --short HEAD 2>/dev/null || echo 'n/a')\` |"
  echo "| Ferramenta | Trivy $(trivy --version --format json | sed -n 's/.*"Version":"\([^"]*\)".*/\1/p' | head -1) |"
  echo "| Imagem analisada | \`$IMAGE\` |"
  echo
  echo "## 1. Repositório (segredos e configuração do Dockerfile/compose)"
  echo
  report trivy-repository.json
  echo
  echo "## 2. Imagem da aplicação (pacotes do Alpine, runtime Java e dependências)"
  echo
  report trivy-image.json
} > "$OUT_DIR/trivy-report.md"

echo "Report written to $OUT_DIR/trivy-report.md"
