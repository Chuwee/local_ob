CREATE SCHEMA IF NOT EXISTS ob_cpanel;

-- -----------------------------------------------------
-- Table ob_cpanel.cpanel_sesion
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_sesion (
 idSesion int(11) NOT NULL AUTO_INCREMENT,
 idEvento int(11) NOT NULL,
 idRelacionEntidadRecinto int(11) NOT NULL,
 nombre varchar(50) DEFAULT NULL,
 descripcion varchar(45) DEFAULT NULL,
 fechaInicioSesion datetime NOT NULL,
 fechaFinSesion datetime NOT NULL,
 fechaVenta datetime DEFAULT NULL,
 fechaPublicacion datetime DEFAULT NULL,
 duracion int(11) DEFAULT NULL,
 estado int(11) NOT NULL,
 estadoGeneracionAforo int(11) DEFAULT NULL,
 estadoReplanchadoAforo int(11) DEFAULT NULL,
 aforo int(11) DEFAULT NULL,
 publicado tinyint(1) NOT NULL DEFAULT '0',
 enVenta tinyint(1) NOT NULL DEFAULT '0',
 motivoCancelacionVenta int(11) DEFAULT NULL,
 motivoCancelacionPublicacion int(11) DEFAULT NULL,
 migradoBiPromotor tinyint(1) DEFAULT NULL,
 migradoBiCanal tinyint(1) DEFAULT NULL,
 esAbono tinyint(1) DEFAULT 0,
 allowPartialRefund tinyint(1) DEFAULT 0,
 color int(11) DEFAULT NULL,
 fechaInicioReserva datetime DEFAULT NULL,
 fechaFinReserva datetime DEFAULT NULL,
 reservasActivas tinyint(1) DEFAULT NULL,
 estadoPurgado tinyint(4) DEFAULT NULL,
 idExterno int(11) DEFAULT NULL,
 idImpuesto int(11) DEFAULT NULL,
 idImpuestoRecargo int(11) DEFAULT NULL,
 fechaNoDefinitiva tinyint(1) DEFAULT NULL,
 fechaModificacionExterna datetime DEFAULT NULL,
 espacioValidacionAcceso int(11) DEFAULT NULL,
 fechaRealFinSesion datetime DEFAULT NULL,
 tipoHorarioAccesos tinyint(4) DEFAULT NULL,
 aperturaAccesos datetime DEFAULT NULL,
 cierreAccesos datetime DEFAULT NULL,
 dirtyBI bit(1) NOT NULL DEFAULT '0',
 numMaxLocalidadesCompra int(11) DEFAULT NULL,
 numMaxLocalidadesSesion INT(11) DEFAULT NULL,
 captcha tinyint(1) DEFAULT NULL,
 elementoComTicketTaquilla int(11) DEFAULT NULL,
 elementoComTicket int(11) DEFAULT NULL,
 mostrarHorario tinyint(1) DEFAULT '1',
 vinculoConfigRecinto bit(1) DEFAULT '0',
 tipoVenta int(11) DEFAULT NULL,
 usaAccesosPlantilla bit(1) DEFAULT '1',
 usaLimitesCuposPlantillaEvento tinyint(1) DEFAULT NULL,
 usarDatosFiscalesProductor tinyint(1) DEFAULT NULL,
 idPromotor int(11) DEFAULT NULL,
 razonCancelacionPublicacion varchar(200) DEFAULT NULL,
 idListaSubscripcion int(11) DEFAULT NULL,
 isExternal bit(1) DEFAULT '0',
 isPreview bit(1) NOT NULL DEFAULT '0',
 hideSessionDates tinyint(1) DEFAULT '0',
 showDate bit(1) NOT NULL DEFAULT '1',
 showDatetime bit(1) NOT NULL DEFAULT '1',
 checkOrphanSeats tinyint(1) DEFAULT '0',
 archivado tinyint(1) NOT NULL DEFAULT 0,
 createDate datetime DEFAULT CURRENT_TIMESTAMP,
 updateDate datetime DEFAULT CURRENT_TIMESTAMP,
 reference varchar(100) DEFAULT NULL,
 presaleEnabled bit(1) NULL DEFAULT 0,
 create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 invoicePrefixId int(11) DEFAULT NULL,
 isCB tinyint(11) DEFAULT '0',
 sbEstado tinyint(1) DEFAULT NULL,
 sbSesionRelacionada int(11) DEFAULT NULL,
 externalReference varchar(50) DEFAULT NULL,
 blockingTime int DEFAULT NULL,
 preorderTime int DEFAULT NULL,
 highDemand tinyint(1) NOT NULL DEFAULT '0',
 showUnconfirmedDate tinyint(1) NOT NULL DEFAULT '0',
 PRIMARY KEY (idSesion)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_evento (
    idEvento int(5) not null primary key,
    idEntidad int(5) not null,
    estado int(11) NOT NULL,
    tipoAbono int(5) null,
    tipoEvento int(1) not null,
    nombre varchar(50)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad (
    idEntidad int(5) not null primary key,
    idEvento int(5) not null,
    idOperadora int(5) not null,
    nombre varchar(50)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_recinto_config (
  idRelacionEntRecinto int(5) not null primary key,
  idEntidad int(5) not null,
  idConfiguracion int(5) not null
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_config_recinto (
    idConfiguracion int(5) not null primary key,
    nombreConfiguracion varchar(50) DEFAULT NULL,
    esGrafica bit(1) DEFAULT 1,
    idRecinto int(5) not null,
    tipoPlantilla int(5) not null,
    espacioRecinto int(5)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_recinto (
    idRecinto int(5) not null primary key,
    nombre varchar(200) DEFAULT NULL,
    pais int(11) DEFAULT NULL,
    municipio varchar(50) DEFAULT NULL,
    timeZone int(5) not null
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_time_zone_group (
  zoneId int(11) not null primary key,
  rawOffsetMins int(3) not null,
  olsonId varchar(256) not null,
  displayName varchar(256) not null
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_impuesto (
  idImpuesto int(11) NOT NULL,
  idOperadora int(11) NOT NULL,
  nombre varchar(50) NOT NULL,
  descripcion varchar(150) DEFAULT NULL,
  valor double NOT NULL,
  defecto tinyint(1) DEFAULT NULL,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idImpuesto)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_espacio (
idEspacio int(11) NOT NULL,
idRecinto int(11) NOT NULL,
nombre varchar(200) NOT NULL,
pathImagen varchar(200) DEFAULT NULL,
aforoMaximo int(11) DEFAULT NULL,
descripcion int(11) DEFAULT NULL,
caracteristicasTecnicas int(11) DEFAULT NULL,
orden int(11) DEFAULT NULL,
predeterminado tinyint(1) DEFAULT '0',
dirtyBI bit(1) NOT NULL DEFAULT '0',
PRIMARY KEY (idEspacio)
);

INSERT INTO ob_cpanel.cpanel_entidad (idEntidad, idEvento, idOperadora)
VALUES (1, 2, 1);

INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (idRelacionEntRecinto, idEntidad, idConfiguracion)
VALUES (15,1,100), (16,1,101);

INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, tipoPlantilla) VALUES (101,1,1), (100,1,1);

INSERT INTO ob_cpanel.cpanel_recinto (idRecinto, timeZone) VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, tipoEvento, estado) VALUES (100,1,1,1), (2,1,1,1);

INSERT INTO ob_cpanel.cpanel_time_zone_group (zoneId, rawOffsetMins, olsonId, displayName)
VALUES(1, -720, 'Etc/GMT+12', '(GMT -12:00) International Date Line West');

INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, fechaInicioSesion, fechaFinSesion, estado, isPreview,esAbono)
VALUES  (1, 1, 15, '2018-12-12', '2019-12-12', 1, 0, 0),
        (2, 2, 15, '2018-12-13', '2019-11-12', 1, 0, 0),
        (3, 2, 15, '2018-12-14', '2019-10-12', 1, 0, 0),
        (4, 2, 15, '2018-12-15', '2019-09-12', 1, 0, 0),
        (5, 2, 15, '2018-12-15', '2019-09-12', 3, 0, 0),
        (6, 2, 15, '2018-12-15', '2019-09-12', 3, 1, 0),
        (7, 1, 15, '2018-12-15', '2019-09-12', 3, 1, 1);
;

INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion ,estado, publicado, enVenta) VALUES
(8, 50, 15, 'Yet another season ticket Session', '2020-02-12 12:03:38', '2021-02-12 12:03:38', '2020-02-12 12:03:38', '2020-02-12 12:03:38',1 , 1 , 1),
(9, 52, 15, 'Yet another season ticket Session', '2020-02-12 12:03:38', '2021-02-12 12:03:38', '2020-02-12 12:03:38', '2020-02-12 12:03:38',1 , 1 , 1),
(10, 52, 15, 'Yet another season ticket Session', '2020-02-12 12:03:38', '2021-02-12 12:03:38', '2020-02-12 12:03:38', '2020-02-12 12:03:38',1 , 1 , 1);
