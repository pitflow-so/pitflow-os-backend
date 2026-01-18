--liquibase formatted sql

--changeset rafael:003-create-inventory-tables
CREATE TABLE IF NOT EXISTS part (
    id UUID NOT NULL,
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_part PRIMARY KEY (id),
    CONSTRAINT uc_part_sku UNIQUE (sku)
);
COMMENT ON COLUMN part.sku IS 'Unidade de Manutenção de Estoque:  É a "identidade" de cada item no estoque, permitindo diferenciá-lo de outros.';


CREATE TABLE IF NOT EXISTS service (
    id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_service PRIMARY KEY (id)
);

--rollback DROP TABLE part;
--rollback DROP TABLE service;