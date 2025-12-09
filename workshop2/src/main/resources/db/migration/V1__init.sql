-- Initial database schema for ordre-api
-- Basert på eksisterende JPA entities (KundeEntity og ProduktLagerEntity)

-- Kunde tabell
CREATE TABLE kunde
(
    id       BIGSERIAL PRIMARY KEY,
    navn     VARCHAR(255) NOT NULL,
    er_aktiv BOOLEAN      NOT NULL
);

-- ProduktLager tabell
CREATE TABLE produkt_lager
(
    produkt_id       VARCHAR(255) PRIMARY KEY,
    antall_paa_lager INTEGER NOT NULL
);

-- Index for raskere oppslag
CREATE INDEX idx_kunde_er_aktiv ON kunde (er_aktiv);
