CREATE TABLE IF NOT EXISTS stock_items (
    supplies_id UUID PRIMARY KEY NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_stock_items_supplies_id FOREIGN KEY (supplies_id) REFERENCES supplies(id),
    CONSTRAINT ck_stock_items_quantity CHECK (quantity >= 0),
    CONSTRAINT ck_stock_items_reserved_quantity CHECK (reserved_quantity >= 0 AND reserved_quantity <= quantity)
);
