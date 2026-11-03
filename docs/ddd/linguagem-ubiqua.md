# Linguagem ubíqua

Vocabulário comum do SIAES, usado da mesma forma nas conversas com a oficina, na documentação e no código. Cada termo em português aponta para o nome no código (classe, enum ou evento), que fica em inglês. Quando um termo significa coisas diferentes em dois contextos, a definição diz em qual contexto ele vale.

Os requisitos que usam esses termos estão em [requisitos.md](../requisitos.md).

## Conceitos centrais

| Termo | No código | Definição |
|---|---|---|
| Ordem de serviço (OS) | `WorkOrder` | Registro de um atendimento: um veículo de um cliente que entrou na oficina para algum serviço. Concentra o status, o diagnóstico e o histórico. É o centro do sistema |
| Orçamento | `Quotation` | Proposta de serviços e peças para uma OS, com o valor total. Uma OS pode ter vários orçamentos ao longo do tempo, mas só um em aberto por vez |
| Cliente | `Customer` | Pessoa física ou jurídica dona de veículos, identificada pelo CPF ou CNPJ. Não é o mesmo que usuário: um cliente pode existir sem conta no sistema |
| Veículo | `Vehicle` | Carro de um cliente, identificado pela placa, com marca, modelo e ano. Pertence a um único cliente |
| Serviço | `Maintenance` | Item do catálogo de mão de obra (troca de óleo, alinhamento, passar scanner...), com código, descrição, preço e duração estimada. A rota é `/services`; o nome da classe é `Maintenance`, mas na conversa é sempre "serviço" |
| Peça (ou insumo) | `Supplies` | Item do catálogo de peças e insumos, com SKU, descrição e preço unitário |
| Estoque da peça | `StockItem` | Quantidade de uma peça na oficina: total, reservado e disponível. Cada peça tem um estoque |
| Usuário | `User` | Conta de acesso ao sistema, com e-mail, senha e papel. Um usuário com papel de cliente está vinculado a um cliente |

## Atendimento: cliente, veículo e abertura da OS

| Termo | No código | Definição |
|---|---|---|
| Documento | `Document`, `DocumentType` | CPF ou CNPJ do cliente, guardado só com os dígitos e validado pelos dígitos verificadores. É único por cliente |
| Placa | `Plate` | Placa no padrão antigo (`AAA9999`) ou Mercosul (`AAA9A99`), guardada sem pontuação e em maiúsculas. É única |
| Serviço solicitado | — | O cliente chega à oficina pedindo um serviço. Acontece no balcão, antes de qualquer registro no sistema |
| Cliente identificado | `GET /customers?document=` | O cliente já tem cadastro e foi encontrado pelo CPF/CNPJ. Nada é gravado |
| Abrir a OS | `WorkOrder` em `RECEIVED` | Criar a OS para um cliente e um veículo dele. O veículo precisa pertencer ao cliente |
| Diagnóstico | `Diagnosis` | O que o mecânico constatou no veículo, com autor e data. É registrado na OS em diagnóstico e pode ser corrigido enquanto ela estiver nesse status |

## Orçamento e aprovação

| Termo | No código | Definição |
|---|---|---|
| Item de serviço | `QuotationMaintenance` | Serviço do catálogo incluído no orçamento, com quantidade e o preço do momento da inclusão |
| Item de peça | `QuotationSupplies` | Peça incluída no orçamento, com quantidade e o preço do momento da inclusão. Reserva unidades no estoque |
| Valor total | `Quotation.getTotalAmount` | Soma dos itens (preço unitário vezes quantidade). Nunca é informado à mão |
| Quilometragem | `Quotation.odometer` | Hodômetro do veículo no momento do orçamento |
| Rascunho | `QuotationStatus.DRAFT` | Orçamento em montagem. Só nesse status ele pode ser editado |
| Concluir o orçamento | `Quotation.complete` | Fechar o rascunho e enviá-lo ao cliente. Depois disso o orçamento não muda mais |
| Link de aprovação | `QuotationApprovalToken` | Link assinado, enviado por e-mail, com o qual o cliente sem conta vê e decide o orçamento. Vale 72 horas e é de uso único |
| Confirmação do documento | `QuotationApprovalToken.confirm` | Os 4 últimos dígitos do CPF/CNPJ que o cliente informa ao decidir pelo link. Cinco erros revogam o link |
| Link revogado | `QuotationApprovalToken.revoke` | Link inutilizado depois de cinco erros na confirmação do documento. A oficina gera outro |
| Decisão do cliente | `QuotationDecision` | Aprovar (`APPROVE`) ou rejeitar (`REJECT`) o orçamento. Só o cliente decide; a oficina nunca decide por ele |
| Canal da decisão | `QuotationDecisionChannel` | Por onde o cliente decidiu: pela conta no sistema (`CUSTOMER_ACCOUNT`) ou pelo link (`APPROVAL_LINK`) |
| Motivo da rejeição | `Quotation.rejectionReason` | Texto opcional que o cliente informa ao rejeitar |
| Novo orçamento | — | Depois de uma rejeição, o mecânico pode montar outro orçamento para a mesma OS. O rejeitado continua registrado |
| Orçamento descartado | `QuotationStatus.DISCARDED` | Rascunho que deixou de valer porque a OS foi cancelada |

## Estoque

| Termo | No código | Definição |
|---|---|---|
| Quantidade em estoque | `StockItem.quantity` | Unidades fisicamente na oficina |
| Reservado | `StockItem.reservedQuantity` | Unidades separadas para orçamentos ainda não decididos. Não podem ser usadas por outro orçamento nem saem do estoque |
| Disponível | `StockItem.getAvailableQuantity` | Quantidade em estoque menos o reservado. Só o disponível pode ser reservado |
| Reservar | `StockItem.reserve` | Separar unidades para um orçamento, na criação ou edição do rascunho |
| Liberar a reserva | `StockItem.releaseReservation` | Devolver ao disponível as unidades reservadas, quando o orçamento é rejeitado, editado ou descartado |
| Consumir a reserva (baixa) | `StockItem.consumeReservation` | Tirar do estoque as unidades reservadas, quando o orçamento é aprovado |
| Entrada de mercadoria | `StockMovementType.ENTRY` | Unidades que chegaram do fornecedor |
| Ajuste de estoque | `StockMovementType.ADJUSTMENT` | Correção manual da quantidade (contagem, perda, devolução), sempre com motivo. Nunca consome unidades reservadas |
| Saldo inicial | `StockMovementType.INITIAL` | Quantidade informada no cadastro da peça |
| Movimentação de estoque | `StockMovement` | Registro de cada operação no estoque, com tipo, quantidade, motivo, autor, data e o saldo depois dela |

## Status

### Status da OS (`WorkOrderStatus`)

| Status | No código | Significado |
|---|---|---|
| Recebida | `RECEIVED` | A OS foi aberta e o veículo está na oficina |
| Em diagnóstico | `IN_DIAGNOSIS` | O mecânico está avaliando o veículo e montando o orçamento |
| Aguardando aprovação | `AWAITING_APPROVAL` | Há um orçamento concluído esperando a decisão do cliente, ou um orçamento foi rejeitado e a oficina decide o próximo passo |
| Em execução | `IN_EXECUTION` | O orçamento foi aprovado e o serviço está sendo feito |
| Finalizada | `FINISHED` | O serviço terminou; o veículo aguarda a retirada |
| Entregue | `DELIVERED` | O veículo foi entregue ao cliente. Estado final |
| Cancelada | `CANCELLED` | A OS foi encerrada antes da execução por um motivo externo. Estado final |

O status muda só por ações de negócio (iniciar diagnóstico, concluir orçamento, aprovar, finalizar, entregar, cancelar). Não existe "trocar o status" à mão. Cada mudança fica no **histórico de status** (`WorkOrderStatusHistory`), de onde sai o tempo médio de execução.

### Status do orçamento (`QuotationStatus`)

| Status | No código | Significado |
|---|---|---|
| Rascunho | `DRAFT` | Em montagem, editável |
| Aguardando aprovação | `AWAITING_APPROVAL` | Concluído e enviado; espera a decisão do cliente |
| Aprovado | `APPROVED` | O cliente aceitou; a reserva virou baixa |
| Rejeitado | `REJECTED` | O cliente recusou; a reserva foi liberada |
| Descartado | `DISCARDED` | Rascunho anulado pelo cancelamento da OS |

### Status do usuário (`UserStatus`)

| Status | No código | Significado |
|---|---|---|
| Ativo | `ACTIVE` | Status com que todo usuário é criado |
| Inativo | `INACTIVE` | Previsto no modelo para bloquear a conta, mas ainda sem fluxo que o aplique |

## Pessoas e papéis (`UserRole`)

| Termo | No código | Definição |
|---|---|---|
| Administrador | `ADMIN` | Acesso completo |
| Gerente | `MANAGER` | Cadastra clientes, veículos, serviços, peças, estoque e usuários; acompanha o tempo médio de execução |
| Mecânico | `MECHANIC` | Abre e conduz a OS e monta o orçamento. Consulta clientes, veículos, catálogo e saldo de estoque, sem alterá-los |
| Cliente com conta | `CUSTOMER` | Acompanha as próprias OS e decide os próprios orçamentos pelo sistema |
| Cliente sem conta | — | Não tem usuário. Decide o orçamento só pelo link de aprovação |
| Papel mínimo | `@Authentication(minimumRole)` | Os papéis seguem uma hierarquia (administrador > gerente > mecânico > cliente). Um endpoint exige um papel mínimo, e quem está acima também acessa |

## Eventos de domínio

Fatos que já aconteceram, sempre no passado. Os publicados em código fazem outros contextos reagirem.

| Evento | No código | Quem reage |
|---|---|---|
| Cliente removido | `CustomerDeletedEvent` | Remove a conta, os veículos, as OS e os orçamentos do cliente |
| Peça cadastrada | `SuppliesCreatedEvent` | Cria o estoque da peça |
| Estoque movimentado | `StockMovedEvent` | Grava a movimentação de estoque |
| Orçamento concluído | `QuotationCompletedEvent` | Coloca a OS em aguardando aprovação e envia o link por e-mail |
| Orçamento aprovado | `QuotationApprovedEvent` | Consome a reserva e inicia a execução da OS |
| Orçamento rejeitado | `QuotationRejectedEvent` | Libera a reserva; a OS segue aguardando |
| OS cancelada | `WorkOrderCancelledEvent` | Descarta o rascunho e libera a reserva |

## Medidas

| Termo | No código | Definição |
|---|---|---|
| Tempo de execução | `WorkOrder.timeInStatus` | Tempo que uma OS passou em execução, do início da execução até a finalização |
| Tempo médio de execução | `ExecutionTimeMetrics`, `GET /workOrders/metrics/executionTime` | Quantidade de OS finalizadas no período e a média, o mínimo e o máximo do tempo de execução delas, mais o tempo médio em cada status |
| Linha do tempo de status | `StatusTimeline` | Sequência de status de uma OS com as datas, usada para calcular os tempos |
| Eventos de domínio por tipo | `siaes_domain_events_total` | Métrica que conta cada evento de domínio confirmado, exibida no Grafana |

## Termos evitados

| Não use | Use | Por quê |
|---|---|---|
| Manutenção | Serviço | "Manutenção" é só o nome da classe (`Maintenance`). Para a oficina é serviço |
| Status alterado | O fato de cada status (Diagnóstico iniciado, Execução iniciada...) | Genérico demais; esconde qual ação de negócio aconteceu |
| OS aguardando aprovação, OS em execução (como evento) | OS colocada em aguardando aprovação, Execução iniciada | São nomes de estado. Evento descreve o que aconteceu |
| Orçamento enviado | Orçamento concluído, Link de aprovação enviado | O envio é consequência da conclusão, e o link é o que de fato chega ao cliente |
| Aprovar pela oficina | — | Só o cliente decide o orçamento |
| Excluir serviço ou peça | Inativar | Serviço e peça não são apagados, para preservar os orçamentos antigos |
| Usuário (no sentido de cliente) | Cliente | Cliente é o dono do veículo; usuário é a conta de acesso, que o cliente pode não ter |
