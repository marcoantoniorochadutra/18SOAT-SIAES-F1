{{- $counts := dict "CRITICAL" 0 "HIGH" 0 "MEDIUM" 0 "LOW" 0 "UNKNOWN" 0 -}}
{{- range . -}}
{{- range .Vulnerabilities }}{{ $_ := set $counts .Severity (add1 (get $counts .Severity)) }}{{ end -}}
{{- range .Misconfigurations }}{{ $_ := set $counts .Severity (add1 (get $counts .Severity)) }}{{ end -}}
{{- range .Secrets }}{{ $_ := set $counts .Severity (add1 (get $counts .Severity)) }}{{ end -}}
{{- end -}}
| CRITICAL | HIGH | MEDIUM | LOW | UNKNOWN |
|---|---|---|---|---|
| {{ get $counts "CRITICAL" }} | {{ get $counts "HIGH" }} | {{ get $counts "MEDIUM" }} | {{ get $counts "LOW" }} | {{ get $counts "UNKNOWN" }} |
{{ range . }}
{{- if or .Vulnerabilities .Misconfigurations .Secrets }}
#### `{{ .Target }}`{{ if .Type }} ({{ .Type }}){{ end }}
{{ if .Vulnerabilities }}
| Severidade | Vulnerabilidade | Pacote | Versão instalada | Corrigida em | Descrição |
|---|---|---|---|---|---|
{{- range .Vulnerabilities }}
| {{ .Severity }} | [{{ .VulnerabilityID }}]({{ .PrimaryURL }}) | `{{ .PkgName }}` | {{ .InstalledVersion }} | {{ if .FixedVersion }}{{ .FixedVersion }}{{ else }}sem correção{{ end }} | {{ .Title | replace "|" "\\|" | replace "\n" " " }} |
{{- end }}
{{ end }}
{{- if .Misconfigurations }}
| Severidade | Regra | Problema | Como resolver |
|---|---|---|---|
{{- range .Misconfigurations }}
| {{ .Severity }} | [{{ .ID }}]({{ .PrimaryURL }}) | {{ .Title | replace "|" "\\|" }} | {{ .Resolution | replace "|" "\\|" | replace "\n" " " }} |
{{- end }}
{{ end }}
{{- if .Secrets }}
| Severidade | Regra | Descrição | Linha |
|---|---|---|---|
{{- range .Secrets }}
| {{ .Severity }} | {{ .RuleID }} | {{ .Title | replace "|" "\\|" }} | {{ .StartLine }} |
{{- end }}
{{ end }}
{{- end }}
{{- else }}
Nenhum achado.
{{ end -}}
