DROP SCHEMA IF EXISTS ob_cpanel CASCADE;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_pack_item
(
    idPackItem        INT NOT NULL AUTO_INCREMENT,
    idPack            INT NOT NULL,
    idItem            INT NOT NULL,
    tipoItem          INT NOT NULL DEFAULT 1,
    principal         BIT(1)       DEFAULT NULL,
    idConfiguracion   INT          DEFAULT NULL,
    idZonaPrecio      INT          DEFAULT NULL,
    idVariante        INT          DEFAULT NULL,
    zonaPrecioMapping TINYINT(1) DEFAULT NULL
);

INSERT INTO ob_cpanel.cpanel_pack_item
(`idPackItem`, `idPack`, `idItem`, `tipoItem`, `principal`, `idConfiguracion`, `idZonaPrecio`, `idVariante`,
 `zonaPrecioMapping`)
VALUES (3210, 3455, 646936, 1, 1, 10, NULL, NULL, NULL),
       (3217, 3455, 646939, 1, 0, 10, NULL, NULL, 1),
       (3218, 3455, 7214, 2, 0, 11, NULL, 8417, NULL),
       (3214, 3459, 87002, 3, 1, 110897, NULL, NULL, NULL),
       (3223, 3459, 646939, 1, 0, NULL, NULL, NULL, 1),
       (3314, 3493, 646936, 1, 1, NULL, NULL, NULL, NULL),
       (3315, 3493, 646937, 1, 0, NULL, 441398, NULL, NULL),
       (3390, 3544, 646936, 1, 1, NULL, NULL, NULL, NULL),
       (3392, 3544, 7214, 2, 0, NULL, NULL, 8417, NULL),
       (3393, 3544, 648090, 1, 0, NULL, NULL, NULL, NULL);

CREATE TABLE ob_cpanel.cpanel_pack_zona_precio_mapping
(
    `idSourcePackItem`   int NOT NULL,
    `idSourceZonaPrecio` int NOT NULL,
    `idTargetPackItem`   int NOT NULL,
    `idTargetZonaPrecio` int NOT NULL
);

INSERT INTO ob_cpanel.cpanel_pack_zona_precio_mapping
(`idSourcePackItem`, `idSourceZonaPrecio`, `idTargetPackItem`, `idTargetZonaPrecio`)
VALUES (3210, 429844, 3217, 441447),
       (3210, 429845, 3217, 441448),
       (3214, 429844, 3223, 441447),
       (3214, 429845, 3223, 441448);

CREATE TABLE ob_cpanel.cpanel_sesion
(
    `idSesion`                 int NOT NULL AUTO_INCREMENT,
    `idEvento`                 int NOT NULL,
    `idRelacionEntidadRecinto` int NOT NULL
);

INSERT INTO ob_cpanel.cpanel_sesion (`idSesion`, `idEvento`, `idRelacionEntidadRecinto`)
VALUES (646936, 87002, 110795),
       (646939, 92547, 116484),
       (646937, 87002, 116471);

CREATE TABLE ob_cpanel.cpanel_tarifa
(
    `idTarifa`    int         NOT NULL AUTO_INCREMENT,
    `idEvento`    int          DEFAULT NULL,
    `nombre`      varchar(50) NOT NULL,
    `descripcion` varchar(200) DEFAULT NULL,
    `defecto`     tinyint(1) DEFAULT NULL
);

INSERT INTO ob_cpanel.cpanel_tarifa (`idTarifa`, `idEvento`, `nombre`, `descripcion`, `defecto`)
VALUES (233247, 87002, 'Tarifa general', 'General A', 1),
       (233248, 87002, 'Tarifa especial', 'Especial A', 0),
       (249973, 92547, 'Tarifa general', 'General B', 1),
       (249974, 92547, 'Tarifa especial', 'Especial B', 0);

CREATE TABLE ob_cpanel.cpanel_asignacion_zona_precios
(
    `idTarifa` int NOT NULL,
    `idZona`   int NOT NULL,
    `precio` double NOT NULL
);

INSERT INTO ob_cpanel.cpanel_asignacion_zona_precios (`idTarifa`, `idZona`, `precio`)
VALUES (233247, 429844, 10.0),
       (233247, 429845, 20.0),
       (233248, 429844, 5.0),
       (233248, 429845, 15.0),
       (249973, 441447, 1.0),
       (249973, 441448, 5.0),
       (249974, 441447, 11.0),
       (249974, 441448, 15.0),
       (233247, 441398, 2.0);

CREATE TABLE ob_cpanel.cpanel_zona_precios_config
(
    `idZona`          int NOT NULL AUTO_INCREMENT,
    `idConfiguracion` int NOT NULL,
    `descripcion`     varchar(200) DEFAULT NULL
);

INSERT INTO ob_cpanel.cpanel_zona_precios_config (`idZona`, `idConfiguracion`, `descripcion`)
VALUES (429844, 110897, 'General A'),
       (429845, 110897, 'Premium A'),
       (441447, 116586, 'General B'),
       (441448, 116586, 'Premium B'),
       (441398, 116573, 'General C');

CREATE TABLE ob_cpanel.cpanel_entidad_recinto_config
(
    `idRelacionEntRecinto` int NOT NULL AUTO_INCREMENT,
    `idEntidad`            int NOT NULL,
    `idConfiguracion`      int NOT NULL
);

INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (`idRelacionEntRecinto`, `idEntidad`, `idConfiguracion`)
VALUES (110795, 4594, 110897),
       (116471, 4594, 116573);

CREATE TABLE ob_cpanel.cpanel_config_recinto
(
    `idConfiguracion` int NOT NULL AUTO_INCREMENT,
    `idEvento`        int DEFAULT NULL
);

INSERT INTO ob_cpanel.cpanel_config_recinto (`idConfiguracion`, `idEvento`)
VALUES (110897, 87002),
       (116573, 87002);