SET REFERENTIAL_INTEGRITY FALSE;
drop schema if exists ob_cpanel;
SET REFERENTIAL_INTEGRITY TRUE;

CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_tier
(
    idTier       int(11) primary key auto_increment,
    idZona       int(11)     not null,
    nombre       varchar(50) not null,
    fecha_inicio datetime    not null,
    timeZone     int(11)     not null,
    precio       double      not null,
    venta        tinyint(1)  not null default 1,
    limite       int(6)      null,
    condicion    integer     not null default 1,
    create_date datetime not null default current_timestamp,
    update_date datetime not null default current_timestamp,
    unique key zona_precio_fecha (idZona, fecha_inicio),
    unique key zona_precio_nombre (idZona, nombre)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_tier_cupo
(
    idTier int(11) NOT NULL,
    idCupo int(11) NOT NULL,
    limite int(6)  NOT NULL,
    PRIMARY KEY (idTier, idCupo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_zona_precios_config
(
    idZona                 int(11)     NOT NULL AUTO_INCREMENT,
    idConfiguracion        int(11)     NOT NULL,
    codigo                 varchar(50) NOT NULL,
    zonaReferencia         int(11)              DEFAULT NULL,
    descripcion            varchar(200)         DEFAULT NULL,
    color                  varchar(20)          DEFAULT NULL,
    defecto                tinyint(1)           DEFAULT NULL,
    idExterno              int(11)              DEFAULT NULL,
    dirtyBI                bit(1)      NOT NULL DEFAULT 0,
    elementoComDescripcion int(11)              DEFAULT NULL,
    create_date            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    restrictiveAccess      tinyint(1)           DEFAULT NULL,
    prioridad              int(11)     NOT NULL DEFAULT 0,
    PRIMARY KEY (idZona)
);


CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_config_recinto
(
    idConfiguracion              int(11)     NOT NULL AUTO_INCREMENT,
    idRecinto                    int(11)     NOT NULL,
    nombreConfiguracion          varchar(50) NOT NULL,
    estado                       int(11)     NOT NULL,
    descripcion                  varchar(200)         DEFAULT NULL,
    tipoNumeracion               int(11)              DEFAULT 0,
    configuracionAsociada        int(11)              DEFAULT NULL,
    tipo                         int(11)     NOT NULL DEFAULT 0,
    aforo                        int(11)              DEFAULT NULL,
    esGrafica                    tinyint(1)  NOT NULL DEFAULT 0,
    creador                      int(11)              DEFAULT NULL,
    fechaAlta                    datetime             DEFAULT NULL,
    fechaUltimaModificacion      datetime             DEFAULT NULL,
    publicada                    tinyint(1)           DEFAULT NULL,
    idEvento                     int(11)              DEFAULT NULL,
    urlImagenDefecto             varchar(500)         DEFAULT NULL,
    idExterno                    int(11)              DEFAULT NULL,
    fechaPropagacionDatosExterna datetime             DEFAULT NULL,
    hayCambiosExternos           tinyint(1)           DEFAULT NULL,
    tipoPlantilla                int(11)              DEFAULT 1,
    estadoProcesoExterno         int(11)              DEFAULT 1,
    currentSequence              bigint(20)           DEFAULT NULL,
    lastSequence                 bigint(20)           DEFAULT NULL,
    dirtyBI                      bit(1)      NOT NULL DEFAULT 0,
    espacioRecinto               int(11)              DEFAULT NULL,
    thirdPartyIntegration        bit(1)      NOT NULL DEFAULT 0,
    create_date                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idConfiguracion)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_recinto
(
    idRecinto int(5)       not null primary key,
    nombre    varchar(200) NOT NULL,
    pais      int(11)      NOT NULL,
    municipio varchar(50) DEFAULT NULL,
    timeZone  int(5)       not null
);


CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_cupos_config
(
    idCupo          int(11)     NOT NULL AUTO_INCREMENT,
    idConfiguracion int(11)     NOT NULL,
    codigo          varchar(50) NOT NULL,
    descripcion     varchar(200)         DEFAULT NULL,
    color           varchar(20)          DEFAULT NULL,
    defecto         tinyint(1)           DEFAULT NULL,
    capacidad       int(11)              DEFAULT NULL,
    create_date     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date     timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idCupo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_evento
(
    idEvento int(11) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (idEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_time_zone_group
(
    zoneId        int(11)      NOT NULL AUTO_INCREMENT,
    rawOffsetMins int(3)       NOT NULL,
    useDST        tinyint(1)   NOT NULL,
    olsonId       varchar(256) NOT NULL,
    displayName   varchar(256) NOT NULL,
    displayZone   varchar(256) NOT NULL,
    hemisphere    varchar(1) DEFAULT NULL,
    PRIMARY KEY (zoneId)
);

insert into ob_cpanel.cpanel_tier(idZona, nombre, fecha_inicio, precio, venta, timeZone)
values (1, 'Early Bird', '2025-01-01 00:00:00.000', 10.0, true, 37),
       (1, 'First Access', '2025-02-01 00:00:00.000', 15.0, true, 37),
       (2, 'Early Bird', '2025-01-01 00:00:00.000', 20.0, true, 37),
       (3, 'Early Bird', '2025-01-01 00:00:00.000', 25.0, true, 37),
       (4, 'Early Bird', '2025-01-01 00:00:00.000', 30.0, true, 37),
       (5, 'Early Bird', '2025-01-01 00:00:00.000', 90.0, true, 37);

insert into ob_cpanel.cpanel_zona_precios_config (idZona, idConfiguracion, codigo, descripcion)
values (1, 10, 'ZP1', 'Zona de precio 1 config 10'),
       (2, 10, 'ZP2', 'Zona de precio 2 config 10'),
       (3, 20, 'ZP3', 'Zona de precio 3 config 20'),
       (4, 20, 'ZP4', 'Zona de precio 4 config 20'),
       (5, 30, 'ZP5', 'Zona de precio 5 config 30'),
       (6, 40, 'ZP6', 'Zona de precio 6 config 40'),
       (7, 40, 'ZP7', 'Zona de precio 7 config 40');

insert into ob_cpanel.cpanel_recinto (idRecinto, nombre, pais, timeZone)
values (100, 'Venue 100', 1, 37),
       (101, 'Venue 101', 1, 37);

insert into ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, idEvento)
values (10, 100, 'config 10 del recinto 100 evento 1234', 1, 1234),
       (20, 101, 'config 20 del recinto 101 evento 1234', 1, 1234),
       (30, 100, 'config 30 del recinto 100 evento 1234', 1, 12345),
       (40, 101, 'config 40 del recinto 101 evento 1234', 1, 123456);

insert into ob_cpanel.cpanel_evento
values (1234),
       (12345),
       (123456);

insert into ob_cpanel.cpanel_tier_cupo (idTier, idCupo, limite)
values (1, 100, 1000),
       (1, 200, 2000),
       (1, 300, 3000);

insert into ob_cpanel.cpanel_cupos_config (idCupo, idConfiguracion, codigo, descripcion)
values (100, 10, 'CP1', 'cupo 1'),
       (200, 10, 'CP2', 'cupo 2'),
       (300, 10, 'CP3', 'cupo 3');

INSERT INTO ob_cpanel.cpanel_time_zone_group (zoneId, rawOffsetMins, useDST, olsonId, displayName, displayZone,
                                              hemisphere)
VALUES (37, 60, 1, 'Europe/Berlin', '(GMT +01:00) Brussels, Copenhagen, Madrid, Paris', 'Romance Standard Time', 'N');
