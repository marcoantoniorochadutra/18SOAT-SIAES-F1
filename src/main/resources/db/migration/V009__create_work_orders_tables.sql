CREATE TABLE IF NOT EXISTS work_orders (
    id UUID PRIMARY KEY NOT NULL,
    customer_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    last_status VARCHAR(30) NOT NULL,
    diagnosis VARCHAR(2000),
    diagnosed_by UUID,
    diagnosed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_work_orders_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_work_orders_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT fk_work_orders_diagnosed_by FOREIGN KEY (diagnosed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_work_orders_customer_id ON work_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_vehicle_id ON work_orders(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_last_status ON work_orders(last_status);
CREATE INDEX IF NOT EXISTS idx_work_orders_finished_at ON work_orders(finished_at);

CREATE TABLE IF NOT EXISTS work_order_status_history (
    id UUID PRIMARY KEY NOT NULL,
    work_order_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_work_order_status_history_work_order_id FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_work_order_status_history_work_order_id ON work_order_status_history(work_order_id);

ALTER TABLE quotations
    ADD CONSTRAINT fk_quotations_work_order_id FOREIGN KEY (work_order_id) REFERENCES work_orders(id);
