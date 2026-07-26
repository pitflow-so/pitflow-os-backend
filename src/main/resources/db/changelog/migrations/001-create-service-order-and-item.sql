--liquibase formatted sql

--changeset rafael:001-create-service-order-and-item

CREATE TABLE IF NOT EXISTS service_orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    diagnosis_started_at TIMESTAMP,
    execution_started_at TIMESTAMP,
    finished_at TIMESTAMP,
    delivered_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_description TEXT
);


CREATE TABLE IF NOT EXISTS service_order_items (
    service_order_id UUID NOT NULL,
    catalog_id UUID NOT NULL,
    description VARCHAR(255) NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    item_type VARCHAR(50) NOT NULL,

    PRIMARY KEY (service_order_id, catalog_id),
    CONSTRAINT fk_service_order FOREIGN KEY (service_order_id) REFERENCES service_orders (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_so_customer_id ON service_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_so_vehicle_id ON service_orders(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_so_status ON service_orders(status);

--rollback DROP TABLE service_orders;
--rollback DROP TABLE service_order_items;
