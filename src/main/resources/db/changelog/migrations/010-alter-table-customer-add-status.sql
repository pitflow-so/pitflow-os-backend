-- liquibase formatted sql

-- changeset rafael:010-alter-table-customer-add-status
ALTER TABLE customer
    ADD status varchar(20) default 'ACTIVE';

COMMENT ON COLUMN customer.status IS 'Status disponíveis do cliente: ACTIVE, INACTIVE, SUSPENDED, PENDING';
