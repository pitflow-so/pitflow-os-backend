--liquibase formatted sql

--changeset rafael:002-create-operation-outbox

CREATE TABLE operation_outbox (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    message_type VARCHAR(100) NOT NULL,
    schema_version INTEGER NOT NULL,
    destination VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    available_at TIMESTAMP WITH TIME ZONE NOT NULL,
    locked_until TIMESTAMP WITH TIME ZONE,
    lock_id UUID,
    published_at TIMESTAMP WITH TIME ZONE,
    last_error TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_operation_outbox_pending
    ON operation_outbox (status, available_at, created_at);

CREATE INDEX idx_operation_outbox_lease
    ON operation_outbox (status, locked_until);

--rollback DROP TABLE operation_outbox;
