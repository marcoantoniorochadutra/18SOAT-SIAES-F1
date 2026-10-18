CREATE TABLE IF NOT EXISTS maintenances (
    id UUID PRIMARY KEY NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(150) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    estimated_duration_minutes INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_maintenances_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_maintenances_description ON maintenances(description);
