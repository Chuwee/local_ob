drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

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
    create_date            timestamp   NOT NULL,
    update_date            timestamp   NOT NULL,
    restrictiveAccess      tinyint(1)           DEFAULT NULL,
    prioridad              int(11)     NOT NULL DEFAULT '0'
);

CREATE TABLE ob_cpanel.cpanel_config_recinto
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
    fechaAlta                    datetime    NOT NULL,
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
    dirtyBI                      bit(1)      NOT NULL DEFAULT 0,
    espacioRecinto               int(11)              DEFAULT NULL,
    thirdPartyIntegration        bit(1)      NOT NULL DEFAULT '0',
    socialDistancing int(1) default 0 COMMENT '0 - N 1 - GROUP 2 - AUTOMATIC',
    socialDistancingHLock  tinyint(1)           DEFAULT NULL,
    socialDistancingVLock  tinyint(1)           DEFAULT NULL,
    `socialDistancingMaxGroupSize` int(2) DEFAULT NULL,
    create_date                  timestamp   NOT NULL,
    update_date                  timestamp   NOT NULL,
    useExternalTickets  tinyint(1)  DEFAULT 0,
    inventoryProvider              varchar(10),
    externalVenueTemplateId        varchar(255)         DEFAULT NULL
);

INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion,
                                             tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador,
                                             fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto,
                                             idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla,
                                             estadoProcesoExterno, currentSequence, lastSequence, dirtyBI,
                                             espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES (2474, 39, 'Plantilla evento por defecto', 0, NULL, NULL, 422, 3, 946, 0, 1, '2013-11-20 17:20:10.000',
        '2013-11-21 11:12:32.000', 0, 781, NULL, 0, NULL, NULL, 1, NULL, NULL, NULL, 0, 60, 0,
        '2017-05-23 10:25:21.000', '2017-05-23 10:25:21.000')
     , (2478, 39, 'Evento por defecto', 1, NULL, NULL, 1563, 3, 946, 1, 1, '2013-11-21 10:03:35.000', NULL, 0, 781,
        NULL, 0, NULL, NULL, 1, NULL, NULL, NULL, 0, 60, 0, '2017-05-23 10:25:21.000', '2017-05-23 10:25:21.000');


INSERT INTO ob_cpanel.cpanel_zona_precios_config (idZona, idConfiguracion, codigo, zonaReferencia, descripcion, color,
                                                  defecto, idExterno, dirtyBI, elementoComDescripcion, create_date,
                                                  update_date, restrictiveAccess, prioridad)
VALUES (1, 2474, 'ZP1', NULL, 'Patio Butacas Premium', '39168.0', 1, NULL, 0, NULL, '2017-05-25 09:09:00.000',
        '2017-05-25 09:09:00.000', NULL, 0)
     , (2, 2474, 'Palcos 1ª Planta', NULL, 'Palcos 1ª Planta', '39321.0', 0, NULL, 0, NULL, '2017-05-25 09:09:00.000',
        '2017-05-25 09:09:00.000', NULL, 0);


INSERT INTO ob_cpanel.cpanel_zona_precios_config (idZona, idConfiguracion, codigo, zonaReferencia, descripcion, color,
                                                  defecto, idExterno, dirtyBI, elementoComDescripcion, create_date,
                                                  update_date, restrictiveAccess, prioridad)
VALUES (3, 2478, 'Mesas Premium', NULL, 'Patio Butacas Premium', '13260.0', 1, NULL, 0, NULL, '2017-05-25 09:09:00.000',
        '2017-05-25 09:09:00.000', NULL, 0)
     , (4, 2478, 'Palcos 1ª Planta', NULL, 'Palcos 1ª Planta', '52377.0', 0, NULL, 0, NULL, '2017-05-25 09:09:00.000',
        '2017-05-25 09:09:00.000', NULL, 0)
     , (5, 2478, 'Patio Butacas', NULL, 'Patio Butacas', '1.6576033E7', 0, NULL, 0, NULL, '2017-05-25 09:09:00.000',
        '2017-05-25 09:09:00.000', NULL, 0);