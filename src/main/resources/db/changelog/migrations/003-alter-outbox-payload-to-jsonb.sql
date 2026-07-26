--liquibase formatted sql

--changeset rafael:003-alter-outbox-payload-to-jsonb

ALTER TABLE operation_outbox
    ALTER COLUMN payload TYPE JSONB
    USING payload::jsonb;

--rollback ALTER TABLE operation_outbox
--rollback     ALTER COLUMN payload TYPE TEXT
--rollback     USING payload::text;
