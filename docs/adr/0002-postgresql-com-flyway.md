# ADR-0002: PostgreSQL com migrações Flyway

- Status: Aceita
- Data: 2026-11-01

## Contexto

Orçamento, estoque e OS mudam juntos e precisam de atomicidade. Há disputa por estoque, regras de unicidade (documento, placa, SKU) e necessidade de histórico. O enunciado deixa o banco livre, mas exige justificativa.

## Decisão

PostgreSQL 17 como banco único, schema `siaes`. O schema é versionado por migrações Flyway (`db/migration`) e o Hibernate só valida (`ddl-auto: validate`). Regras de integridade também no banco: chaves estrangeiras, índices únicos, `CHECK` de quantidades e um índice único parcial para no máximo um orçamento aberto por OS. Dados de demonstração ficam em `db/seed`, carregados só no perfil `dev`.

## Alternativas consideradas

- **MySQL**: também relacional e ACID, mas sem índice parcial e com MVCC menos favorável a leituras concorrentes com escrita.
- **MongoDB**: esquema flexível, mas transações entre documentos e integridade referencial ficam na aplicação, o oposto do que o domínio pede.
- **`ddl-auto: update`**: menos arquivos, mas sem histórico, sem revisão e com risco de alterações destrutivas.

## Consequências

- Positivas: transações ACID e integridade garantidas no banco; schema reproduzível em qualquer ambiente; caminho de produção em qualquer provedor gerenciado. Justificativa completa no [README](../../readme.md#por-que-postgresql).
- Negativas: toda mudança de schema exige migração; enquanto o projeto não está em produção, as migrações existentes são editadas e os bancos de desenvolvimento recriados.
