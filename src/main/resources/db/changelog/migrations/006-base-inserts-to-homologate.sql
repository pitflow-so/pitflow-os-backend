-- liquibase formatted sql

-- changeset rafael:inserts-to-homologate

INSERT INTO public.customer
(id, "name", "document", phone, created_at)
VALUES('366941cf-9853-4514-ae99-1e1ea2b984ea'::uuid, 'Rafael Moreira', '55798042022', '11996195936', '2026-01-17 22:22:47.416');
INSERT INTO public.customer
(id, "name", "document", phone, created_at)
VALUES('9f460d68-9936-4f2e-ba6b-6bb9ac3d3618'::uuid, 'João da Silva', '80120007096', '11988887777', '2026-01-18 14:07:47.385');
INSERT INTO public.customer
(id, "name", "document", phone, created_at)
VALUES('4c4846a4-9c83-4573-b702-b66c81e2eaf0'::uuid, 'Agnaldo Moreira', '89972922006', '11996195936', '2026-01-18 23:37:15.917');
INSERT INTO public.customer
(id, "name", "document", phone, created_at)
VALUES('9e3df500-6b56-4bd8-a2db-7c4732cb5145'::uuid, 'Maria bonita', '44054348084', '11996195956', '2026-01-20 01:13:15.554');

INSERT INTO public.vehicle
(id, customer_id, license_plate, brand, model, model_year, created_at)
VALUES('ea42d83e-fabd-4a4c-b556-8ae6f19da879'::uuid, '366941cf-9853-4514-ae99-1e1ea2b984ea'::uuid, 'ABC1234', 'Toyota', 'Corolla', 2024, '2026-01-17 22:23:30.565');
INSERT INTO public.vehicle
(id, customer_id, license_plate, brand, model, model_year, created_at)
VALUES('db0baaa4-84e6-4df0-88d8-4318d5092cf9'::uuid, '366941cf-9853-4514-ae99-1e1ea2b984ea'::uuid, 'ODA1234', 'Chevrolet', 'Onix', 2019, '2026-01-17 22:24:01.537');
INSERT INTO public.vehicle
(id, customer_id, license_plate, brand, model, model_year, created_at)
VALUES('a6456279-e157-4391-b337-3b139adaab3a'::uuid, '4c4846a4-9c83-4573-b702-b66c81e2eaf0'::uuid, 'ABB1234', 'Chevrolet', 'Onix', 2019, '2026-01-18 23:42:44.553');

INSERT INTO public.part
(id, sku, "name", description, price, stock_quantity, created_at)
VALUES('f0e12339-adf3-4f73-8a64-869f4d44c321'::uuid, 'FIL-12345', 'Filtro de Óleo Sintético', 'Filtro de óleo para motores de alta performance', 45.90, 50, '2026-01-17 22:24:32.514');
INSERT INTO public.part
(id, sku, "name", description, price, stock_quantity, created_at)
VALUES('ac0580ab-d45f-44c7-b07d-83a33b8c709b'::uuid, 'PAF-12345', 'Pastilha de freio', 'Pastilha de freio de cerâmica', 100.99, 32, '2026-01-17 22:26:19.860');

INSERT INTO public.service
(id, "name", description, price, created_at)
VALUES('3ad26f19-d339-446c-8185-e8bf4235ac1e'::uuid, 'Alinhamento e Balanceamento', 'Serviço completo para as 4 rodas incluindo conferência de suspensão', 150.00, '2026-01-17 22:24:15.430');
INSERT INTO public.service
(id, "name", description, price, created_at)
VALUES('9d0937f4-9939-4988-abc2-3b32c0964603'::uuid, 'Troca de Filtro de óleo', 'Troca de filtro de óleo', 200.00, '2026-01-18 23:46:55.132');

-- password=teste123
INSERT INTO public.mechanics
(id, "name", username, "password", "role")
VALUES('eb6c0f4a-1671-4aa9-9de2-e16d1fc83ad2'::uuid, 'Jorge leao', 'leao', '$2a$10$na5QA7GTxg0/ddJ81oklWOYemCD.CGR6.D5Mpx.ZboeT3Su5XVh8m', 'ROLE_MECHANIC');

--rollback DROP TABLE mechanics