# ADR-0009: Aprovação do orçamento por link assinado

- Status: Aceita
- Data: 2026-11-01

## Contexto

O cliente precisa autorizar o orçamento "via aplicativo", mas nem todo cliente tem conta no sistema. Exigir cadastro e login para uma única decisão cria atrito; um link sem proteção permitiria que qualquer pessoa com a URL decidisse.

## Decisão

- Ao concluir o orçamento, o sistema cria um `QuotationApprovalToken` e envia por e-mail o link `<id>.<HMAC-SHA256(id)>`, assinado com `QUOTATION_APPROVAL_SECRET`.
- O link é de uso único, expira em `QUOTATION_APPROVAL_TIME_TO_LIVE` (72h) e pode ser revogado. Um novo link pode ser emitido por `POST /quotations/{id}/approvalLink`.
- Para decidir, o cliente confirma os 4 últimos dígitos do CPF/CNPJ; 5 tentativas erradas revogam o token.
- A assinatura é comparada em tempo constante. O cliente com conta continua podendo decidir pelo endpoint autenticado.

## Alternativas consideradas

- **Só cliente com conta**: mais seguro, mas obriga cadastro para uma decisão pontual.
- **Token aleatório sem assinatura**: funciona, mas exige consulta ao banco para qualquer valor recebido; a assinatura descarta links forjados antes de chegar ao banco.
- **Código por SMS (OTP)**: segundo fator mais forte, mas depende de um provedor de SMS.

## Consequências

- Positivas: decisão sem cadastro, com proteção contra força bruta e reuso; canal, autor e data da decisão ficam registrados.
- Negativas: quem tiver o e-mail e souber os 4 dígitos do documento consegue decidir; o orçamento não expira junto com o link e fica aguardando decisão.
