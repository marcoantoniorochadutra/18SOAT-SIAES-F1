CREATE TABLE IF NOT EXISTS stock_movements (
    id UUID PRIMARY KEY NOT NULL,
    supplies_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    quantity INTEGER NOT NULL,
    stock_after INTEGER NOT NULL,
    reserved_after INTEGER NOT NULL,
    reason VARCHAR(255),
    reference_id UUID,
    performed_by UUID,
    performed_by_name VARCHAR(150),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_stock_movements_supplies_id FOREIGN KEY (supplies_id) REFERENCES stock_items(supplies_id),
    CONSTRAINT fk_stock_movements_performed_by FOREIGN KEY (performed_by) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_stock_movements_supplies_id_occurred_at ON stock_movements(supplies_id, occurred_at);
CREATE INDEX IF NOT EXISTS idx_stock_movements_reference_id ON stock_movements(reference_id);
