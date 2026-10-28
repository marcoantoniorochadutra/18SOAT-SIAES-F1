CREATE TABLE IF NOT EXISTS quotations (
    id UUID PRIMARY KEY NOT NULL,
    work_order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    description VARCHAR(500),
    odometer BIGINT NOT NULL,
    last_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_quotations_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_quotations_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT ck_quotations_odometer CHECK (odometer >= 0)
);

CREATE INDEX IF NOT EXISTS idx_quotations_work_order_id ON quotations(work_order_id);
CREATE INDEX IF NOT EXISTS idx_quotations_customer_id ON quotations(customer_id);
CREATE INDEX IF NOT EXISTS idx_quotations_vehicle_id ON quotations(vehicle_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_quotations_open_work_order_id ON quotations(work_order_id) WHERE last_status NOT IN ('REJECTED', 'DISCARDED');

CREATE TABLE IF NOT EXISTS quotation_supplies (
    id UUID PRIMARY KEY NOT NULL,
    quotation_id UUID NOT NULL,
    supplies_id UUID NOT NULL,
    description VARCHAR(150) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_quotation_supplies_quotation_id FOREIGN KEY (quotation_id) REFERENCES quotations(id) ON DELETE CASCADE,
    CONSTRAINT fk_quotation_supplies_supplies_id FOREIGN KEY (supplies_id) REFERENCES supplies(id),
    CONSTRAINT ck_quotation_supplies_quantity CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_quotation_supplies_quotation_id ON quotation_supplies(quotation_id);

CREATE TABLE IF NOT EXISTS quotation_maintenances (
    id UUID PRIMARY KEY NOT NULL,
    quotation_id UUID NOT NULL,
    maintenance_id UUID NOT NULL,
    description VARCHAR(150) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_quotation_maintenances_quotation_id FOREIGN KEY (quotation_id) REFERENCES quotations(id) ON DELETE CASCADE,
    CONSTRAINT fk_quotation_maintenances_maintenance_id FOREIGN KEY (maintenance_id) REFERENCES maintenances(id),
    CONSTRAINT ck_quotation_maintenances_quantity CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_quotation_maintenances_quotation_id ON quotation_maintenances(quotation_id);

CREATE TABLE IF NOT EXISTS quotation_status_history (
    id UUID PRIMARY KEY NOT NULL,
    quotation_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_quotation_status_history_quotation_id FOREIGN KEY (quotation_id) REFERENCES quotations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_quotation_status_history_quotation_id ON quotation_status_history(quotation_id);
