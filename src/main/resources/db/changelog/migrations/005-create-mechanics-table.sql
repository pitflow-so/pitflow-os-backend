-- liquibase formatted sql

-- changeset rafael:create-mechanics-table

CREATE TABLE IF NOT EXISTS mechanics (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
--rollback DROP TABLE mechanics