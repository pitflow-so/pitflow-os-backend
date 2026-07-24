--liquibase formatted sql

--changeset rafael:012-insert-inventory-data

INSERT INTO public.part
(id, sku, "name", description, price, stock_quantity, created_at)
VALUES('f0e12339-adf3-4f73-8a64-869f4d44c321'::uuid, 'FIL-12345', 'Filtro de Óleo Sintético', 'Filtro de óleo para motores de alta performance', 45.90, 50, '2026-01-17 22:24:32.514')
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.part
(id, sku, "name", description, price, stock_quantity, created_at)
VALUES('ac0580ab-d45f-44c7-b07d-83a33b8c709b'::uuid, 'PAF-12345', 'Pastilha de freio', 'Pastilha de freio de cerâmica', 100.99, 32, '2026-01-17 22:26:19.860')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.service
(id, "name", description, price, created_at)
VALUES('3ad26f19-d339-446c-8185-e8bf4235ac1e'::uuid, 'Alinhamento e Balanceamento', 'Serviço completo para as 4 rodas incluindo conferência de suspensão', 150.00, '2026-01-17 22:24:15.430')
ON CONFLICT (id) DO NOTHING;
INSERT INTO public.service
(id, "name", description, price, created_at)
VALUES('9d0937f4-9939-4988-abc2-3b32c0964603'::uuid, 'Troca de Filtro de óleo', 'Troca de filtro de óleo', 200.00, '2026-01-18 23:46:55.132')
ON CONFLICT (id) DO NOTHING;
