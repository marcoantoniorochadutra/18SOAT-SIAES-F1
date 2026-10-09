
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY NOT NULL,
    document VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(150),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_document ON customers(document);
CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_email ON customers(email);

CREATE INDEX IF NOT EXISTS idx_customers_document ON customers(document);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
