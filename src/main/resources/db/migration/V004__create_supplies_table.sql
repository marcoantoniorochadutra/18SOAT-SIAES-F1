CREATE TABLE IF NOT EXISTS supplies (
    id UUID PRIMARY KEY NOT NULL,
    sku VARCHAR(50) NOT NULL,
    description VARCHAR(150) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_supplies_sku UNIQUE (sku)
);

CREATE INDEX IF NOT EXISTS idx_supplies_description ON supplies(description);
