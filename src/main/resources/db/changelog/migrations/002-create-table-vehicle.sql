--liquibase formatted sql

--changeset rafael:002-create-table-vehicle
CREATE TABLE IF NOT EXISTS vehicle (
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    license_plate VARCHAR(7) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    model_year INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_vehicle PRIMARY KEY (id),
    CONSTRAINT uc_vehicle_license_plate UNIQUE (license_plate),
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

--rollback DROP TABLE vehicle;
