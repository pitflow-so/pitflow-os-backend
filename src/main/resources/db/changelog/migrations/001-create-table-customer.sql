--liquibase formatted sql

--changeset rafael:001-create-table-customer
CREATE TABLE IF NOT EXISTS customer (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    document VARCHAR(14) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uc_customer_document UNIQUE (document)
);

--rollback DROP TABLE customer;