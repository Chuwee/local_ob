drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_gira
(
    idGira             int(11)     NOT NULL AUTO_INCREMENT,
    idEntidad          int(11)     NOT NULL,
    nombre             varchar(50) NOT NULL,
    descripcion        varchar(200)         DEFAULT NULL,
    referenciaPromotor varchar(50) NOT NULL,
    estado             int(11)     NOT NULL,
    dirtyBI            bit(1)      NOT NULL DEFAULT 0,
    create_date        timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date        timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`idGira`)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_evento
(
    idEvento        int(11)     NOT NULL AUTO_INCREMENT,
    idEntidad       int(11)     NOT NULL,
    idGira          int(11),
    nombre          varchar(50) NOT NULL,
    aforo           int(11),
    estado          int(11),
    archivado       bit(1),
    fechaInicio     timestamp,
    PRIMARY KEY (idEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad
(
    idEntidad   int(11)     NOT NULL PRIMARY KEY,
    idOperadora int(11)     NOT NULL,
    nombre      varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_admin_entidades (
    idEntidadAdmin int(11) NOT NULL,
    idEntidad int(11) NOT NULL,
    PRIMARY KEY (idEntidadAdmin,idEntidad),
    KEY fk_entidad_entidades (idEntidad),
    CONSTRAINT fk_entidad_admin_entidades FOREIGN KEY (idEntidadAdmin) REFERENCES cpanel_entidad (idEntidad),
    CONSTRAINT fk_entidad_entidades FOREIGN KEY (idEntidad) REFERENCES cpanel_entidad (idEntidad)
);

INSERT INTO ob_cpanel.cpanel_gira (idGira, idEntidad, nombre, descripcion, referenciaPromotor, estado, dirtyBI)
VALUES (1, 2, 'Gira1', NULL, 'Gira', 2, 1),
       (2, 2, 'Gira2', NULL, 'Gira', 2, 1),
       (3, 2, 'Gira3', NULL, 'Gira', 1, 1);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, idGira, nombre, aforo, estado, archivado, fechaInicio)
VALUES (1, 2, 1, 'event1', 100, 1, 0, NULL),
       (2, 2, 1, 'event2', 100, 1, 0, NULL),
       (3, 2, NULL, 'event3', 100, 1, 0, NULL);

INSERT INTO ob_cpanel.cpanel_entidad (idEntidad, idOperadora, nombre)
VALUES (2, 1, 'Entidad 103'),
       (6707, 1, 'entityadmin'),
       (5894, 1, 'Entidad Admin managed'),
       (324, 1, 'Entidad Admin managed 2');

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES (6707, 324),
       (6707, 5894);