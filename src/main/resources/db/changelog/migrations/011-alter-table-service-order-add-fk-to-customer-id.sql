-- liquibase formatted sql

-- changeset rafael:011-alter-table-service-order-add-fk-to-customer-id

ALTER TABLE service_orders
    ADD CONSTRAINT fk_service_orders_customer
    FOREIGN KEY (customer_id) REFERENCES customer(id);
