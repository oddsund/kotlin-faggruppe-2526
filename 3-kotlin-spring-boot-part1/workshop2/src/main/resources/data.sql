-- Initial test data loaded on startup

INSERT INTO kunde (id, navn, er_aktiv)
VALUES (1, 'Aktiv Kunde', true),
       (2, 'Inaktiv Kunde', false),
       (3, 'Test Kunde', true);

INSERT INTO produkt_lager (produkt_id, antall_paa_lager)
VALUES ('P1', 100),
       ('P2', 50),
       ('P3', 0), -- Utsolgt
       ('P4', 200);