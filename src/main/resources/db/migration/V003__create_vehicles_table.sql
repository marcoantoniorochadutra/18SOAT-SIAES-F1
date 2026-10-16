CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY NOT NULL,
    customer_id UUID NOT NULL,
    plate VARCHAR(12) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    CONSTRAINT fk_vehicles_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_vehicles_plate ON vehicles(plate);
CREATE INDEX IF NOT EXISTS idx_vehicles_customer_id ON vehicles(customer_id);
