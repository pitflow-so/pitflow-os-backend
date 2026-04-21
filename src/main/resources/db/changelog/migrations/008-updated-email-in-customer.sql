-- liquibase formatted sql

-- changeset rafael:008-updated-email-in-customer
UPDATE customer set email = 'rafaelsmoreiras@gmail.com'
where id = '366941cf-9853-4514-ae99-1e1ea2b984ea'
;