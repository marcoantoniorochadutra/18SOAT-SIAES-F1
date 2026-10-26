ALTER TABLE quotations
    ADD COLUMN decision_channel VARCHAR(30),
    ADD COLUMN decided_by UUID,
    ADD COLUMN decided_at TIMESTAMP,
    ADD COLUMN rejection_reason VARCHAR(500),
    ADD CONSTRAINT fk_quotations_decided_by FOREIGN KEY (decided_by) REFERENCES users(id);

CREATE TABLE IF NOT EXISTS quotation_approval_tokens (
    id UUID PRIMARY KEY NOT NULL,
    quotation_id UUID NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_quotation_approval_tokens_quotation_id FOREIGN KEY (quotation_id) REFERENCES quotations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_quotation_approval_tokens_quotation_id ON quotation_approval_tokens(quotation_id, created_at DESC);
