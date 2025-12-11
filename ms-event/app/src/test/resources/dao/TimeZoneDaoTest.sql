drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_time_zone_group
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

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_recinto
(
    idRecinto int(5)       not null primary key,
    nombre    varchar(200) NOT NULL,
    pais      int(11)      NOT NULL,
    municipio varchar(50) DEFAULT NULL,
    timeZone  int(5)       not null
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_config_recinto
(
    idConfiguracion              int(11)     NOT NULL AUTO_INCREMENT,
    idRecinto                    int(11)     NOT NULL,
    nombreConfiguracion          varchar(50) NOT NULL,
    estado                       int(11)     NOT NULL,
    descripcion                  varchar(200)         DEFAULT NULL,
    tipoNumeracion               int(11)              DEFAULT '0',
    configuracionAsociada        int(11)              DEFAULT NULL,
    tipo                         int(11)     NOT NULL,
    aforo                        int(11)              DEFAULT NULL,
    esGrafica                    tinyint(1)  NOT NULL DEFAULT '0',
    creador                      int(11)              DEFAULT NULL,
    fechaUltimaModificacion      datetime             DEFAULT NULL,
    publicada                    tinyint(1)           DEFAULT NULL,
    idEvento                     int(11)              DEFAULT NULL,
    urlImagenDefecto             varchar(500)         DEFAULT NULL,
    idExterno                    int(11)              DEFAULT NULL,
    fechaPropagacionDatosExterna datetime             DEFAULT NULL,
    hayCambiosExternos           tinyint(1)           DEFAULT NULL,
    tipoPlantilla                int(11)              DEFAULT '1',
    estadoProcesoExterno         int(11)              DEFAULT '1',
    currentSequence              bigint(20)           DEFAULT NULL,
    lastSequence                 bigint(20)           DEFAULT NULL,
    dirtyBI                      int(1)      NOT NULL DEFAULT '0',
    espacioRecinto               int(11)              DEFAULT NULL,
    thirdPartyIntegration        bit(1)      NOT NULL DEFAULT '0',
    createDate                   datetime             DEFAULT CURRENT_TIMESTAMP,
    updateDate                   datetime             DEFAULT CURRENT_TIMESTAMP,
    create_date                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date                  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idConfiguracion)
);


CREATE TABLE ob_cpanel.cpanel_zona_precios_config
(
    idZona                 int(11)     NOT NULL AUTO_INCREMENT,
    idConfiguracion        int(11)     NOT NULL,
    codigo                 varchar(50) NOT NULL,
    zonaReferencia         int(11)              DEFAULT NULL,
    descripcion            varchar(200)         DEFAULT NULL,
    color                  varchar(20)          DEFAULT NULL,
    defecto                tinyint(1)           DEFAULT NULL,
    idExterno              int(11)              DEFAULT NULL,
    dirtyBI                bit(1)      NOT NULL DEFAULT '0',
    elementoComDescripcion int(11)              DEFAULT NULL,
    create_date            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    restrictiveAccess      tinyint(1)           DEFAULT NULL,
    prioridad              int(11)     NOT NULL DEFAULT '0'
);


INSERT INTO ob_cpanel.cpanel_time_zone_group (zoneId, rawOffsetMins, useDST, olsonId, displayName, displayZone,
                                              hemisphere)
VALUES (37, 60, 1, 'Europe/Berlin', '(GMT +01:00) Brussels, Copenhagen, Madrid, Paris', 'Romance Standard Time', 'N');

insert into ob_cpanel.cpanel_recinto(idRecinto, nombre, pais, timeZone)
values (1, 'Venue 1', 1, 37);

insert into ob_cpanel.cpanel_config_recinto (idRecinto, nombreConfiguracion, estado, tipo, esGrafica, publicada,
                                             dirtyBI, thirdPartyIntegration)
values (1, 'Venue template 1', 1, 1, 0, 1, 0, 0);

insert into ob_cpanel.cpanel_zona_precios_config (idZona, idConfiguracion, codigo, color, defecto)
values (10, 1, 'ZONE1', 'COLOR', 0);
