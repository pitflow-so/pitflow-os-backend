--liquibase formatted sql

--changeset rafael:007-alter-table-customer-add-email
ALTER TABLE customer
ADD Email varchar(255);
