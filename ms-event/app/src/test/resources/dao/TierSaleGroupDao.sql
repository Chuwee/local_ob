create schema if not exists ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_tier_cupo
(
    idTier INT(11) NOT NULL,
    idCupo INT(11) NOT NULL,
    limite INT(6)  NOT NULL,
    PRIMARY KEY (idTier, idCupo)
);


insert into ob_cpanel.cpanel_tier_cupo
values (1, 1, 10),
       (1, 2, 20),
       (1, 3, 30),
       (2, 4, 10),
       (2, 5, 20),
       (2, 6, 30);
