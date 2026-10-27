CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(64) NOT NULL,
    role VARCHAR(30) NOT NULL,
    last_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    customer_id UUID,
    last_login_at TIMESTAMP,
    refresh_token VARCHAR(64),
    refresh_token_expires_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);
CREATE INDEX IF NOT EXISTS idx_users_last_status ON users(last_status);
CREATE INDEX IF NOT EXISTS idx_users_customer_id ON users(customer_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_refresh_token ON users(refresh_token);

CREATE TABLE IF NOT EXISTS user_status_history (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    observation VARCHAR(150),
    CONSTRAINT fk_user_status_history_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_status_history_user_id ON user_status_history(user_id);
