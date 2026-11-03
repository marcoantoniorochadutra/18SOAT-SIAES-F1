# ADR-0001: Monólito modular com arquitetura hexagonal por bounded context

- Status: Aceita
- Data: 2026-11-01

## Contexto

O enunciado pede um back-end monolítico em camadas e a aplicação de DDD. O domínio tem regras que atravessam várias áreas (orçamento, estoque, OS) e precisa ser testado sem banco nem framework. O time é pequeno e o MVP precisa de implantação simples.

## Decisão

Um único deployable Spring Boot, com um pacote por bounded context (`workorder`, `quotation`, `inventory`, `supplies`, `maintenance`, `customer`, `vehicle`, `user`, `auth`) e um shared kernel (`sk`). Dentro de cada contexto, arquitetura hexagonal em três camadas:

- `domain`: agregados, value objects, eventos e portas, sem dependência de Spring.
- `application`: casos de uso (interface + `Command`) e app services transacionais.
- `infrastructure`: controllers, subscribers, adapters JPA, JWT e SMTP.

Dependências apontam para dentro: `infrastructure → application → domain`.

## Alternativas consideradas

- **Monólito em camadas técnicas** (`controller`, `service`, `repository` globais): mais simples no início, mas mistura contextos e deixa o domínio dependente de JPA.
- **Microsserviços**: isolamento e escala independentes, mas exigem mensageria, consistência eventual e operação distribuída, desproporcionais a um MVP.

## Consequências

- Positivas: regras testáveis sem Spring; fronteiras explícitas que permitem extrair um contexto depois; um único processo e banco para operar.
- Negativas: mais classes por funcionalidade (porta, adapter, mapper, entidade JPA); a disciplina de dependências é por convenção e revisão, sem ferramenta que a imponha (ex.: ArchUnit).
- Ainda há acoplamento entre `auth`, `user` e `customer` pelo `AuthenticatedUser`, registrado como limitação em [requisitos.md](../requisitos.md#limitações-conhecidas).
