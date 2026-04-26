-- liquibase formatted sql

-- changeset rafael:009-update-email-other-base-customers
UPDATE customer set email = 'dummy@gmail.com'
where id in ('9f460d68-9936-4f2e-ba6b-6bb9ac3d3618', '4c4846a4-9c83-4573-b702-b66c81e2eaf0', '9e3df500-6b56-4bd8-a2db-7c4732cb5145')
;
