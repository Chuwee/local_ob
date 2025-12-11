drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_idioma (
                               `idIdioma` int(11) NOT NULL AUTO_INCREMENT,
                               `codigo` varchar(15) DEFAULT NULL,
                               `descripcion` varchar(200)  DEFAULT NULL,
                               `idiomaPlataforma` tinyint(1) DEFAULT NULL,
                               PRIMARY KEY (`idIdioma`),
                               UNIQUE KEY `codigo_UNIQUE` (`codigo`));

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_taxonomia_base (
        `idTaxonomia` int(11) NOT NULL AUTO_INCREMENT,
        `idTaxonomiaSuperior` int(11) DEFAULT NULL,
        `codigo` varchar(50) NOT NULL,
        `descripcion` varchar(300) DEFAULT NULL,
        `orden` int(11) DEFAULT NULL,
        `tipo` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1: Taxonomia de eventos\n2: Taxonomia de productos no ticketing',
        PRIMARY KEY (`idTaxonomia`),
        UNIQUE KEY `uk_taxonomia_codigo` (`codigo`),
        KEY `taxonomia_taxonomia_superior` (`idTaxonomiaSuperior`),
        CONSTRAINT `taxonomia_taxonomia_superior`
          FOREIGN KEY (`idTaxonomiaSuperior`)
            REFERENCES `cpanel_taxonomia_base` (`idTaxonomia`) ON DELETE NO ACTION ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_taxonomia_propia (
                                         `idTaxonomia` int(11) NOT NULL AUTO_INCREMENT,
                                         `idTaxonomiaSuperior` int(11) DEFAULT NULL,
                                         `idEntidad` int(11) NOT NULL,
                                         `referencia` varchar(50) DEFAULT NULL,
                                         `descripcion` varchar(300) DEFAULT NULL,
                                         `orden` int(11) DEFAULT NULL,
                                         `tipo` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1: Taxonomia de eventos\n2: Taxonomia de productos no ticketing',
                                         PRIMARY KEY (`idTaxonomia`),
                                         KEY `taxonomia_propia_taxonomia_superior` (`idTaxonomiaSuperior`),
                                         KEY `taxonomia_propia_entidad` (`idEntidad`));

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_gira (
                             `idGira` int(11) NOT NULL AUTO_INCREMENT,
                             `idEntidad` int(11) NOT NULL,
                             `nombre` varchar(50) NOT NULL,
                             `descripcion` varchar(200) DEFAULT NULL,
                             `referenciaPromotor` varchar(50) NOT NULL,
                             `estado` int(11) NOT NULL,
                             `dirtyBI` bit(1) NOT NULL DEFAULT 0,
                             PRIMARY KEY (`idGira`)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promotor (
                                 `idPromotor` int(11) NOT NULL AUTO_INCREMENT,
                                 `idEntidad` int(11) DEFAULT NULL,
                                 `nombre` varchar(200) NOT NULL,
                                 `razonSocial` varchar(100) NOT NULL,
                                 `nif` varchar(11) NOT NULL,
                                 `estado` tinyint(4) NOT NULL COMMENT '0:Borrado\n1:Activo\n2:Inactivo',
                                 `predeterminado` tinyint(1) DEFAULT NULL,
                                 `direccion` varchar(200) DEFAULT NULL,
                                 `pais` int(11) DEFAULT NULL,
                                 `provincia` int(11) DEFAULT NULL,
                                 `municipio` varchar(50) DEFAULT NULL,
                                 `cPostal` varchar(10) DEFAULT NULL,
                                 `personaContacto` varchar(250) DEFAULT NULL,
                                 `emailContacto` varchar(200) DEFAULT NULL,
                                 `telefonoContacto` varchar(30) DEFAULT NULL,
                                 `dirtyBI` bit(1) NOT NULL DEFAULT 0,
                                 useSimplifiedInvoice tinyint(1) DEFAULT NULL,
                                 PRIMARY KEY (`idPromotor`)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_evento
(
  idEvento                                  int(11)     NOT NULL AUTO_INCREMENT,
  idEntidad                                 int(11)     NOT NULL,
  idGira                                    int(11)              DEFAULT NULL,
  idCurrency                                int(11)              DEFAULT NULL,
  nombre                                    varchar(50) NOT NULL,
  descripcion                               varchar(200)         DEFAULT NULL,
  tipoEvento                                int(11)              DEFAULT 1 COMMENT '1: Onebox\n2: Avet',
  fechaInicio                               datetime             DEFAULT NULL,
  fechaInicioTZ                             int(11)              DEFAULT NULL,
  fechaFin                                  datetime             DEFAULT NULL,
  fechaFinTZ                                int(11)              DEFAULT NULL,
  estado                                    int(11)     NOT NULL,
  estadoPublicacion                         int(11)              DEFAULT NULL,
  idTaxonomia                               int(11)     NOT NULL,
  idTaxonomiaPropia                         int(11)              DEFAULT NULL,
  referenciaPromotor                        varchar(20)          DEFAULT NULL,
  nombreResponsable                         varchar(50)          DEFAULT NULL,
  apellidosResponsable                      varchar(200)         DEFAULT NULL,
  emailResponsable                          varchar(200)         DEFAULT NULL,
  telefonoResponsable                       varchar(25)          DEFAULT NULL,
  cargoResponsable                          varchar(150)         DEFAULT NULL,
  fechaAlta                                 datetime    NOT NULL,
  fechaModificacion                         datetime             DEFAULT NULL,
  fechaCambioEstado                         datetime             DEFAULT NULL,
  fechaVenta                                datetime             DEFAULT NULL,
  fechaVentaTZ                              int(11)              DEFAULT NULL,
  fechaPublicacion                          datetime             DEFAULT NULL,
  fechaPublicacionTZ                        int(11)              DEFAULT NULL,
  destacado                                 tinyint(1)           DEFAULT NULL,
  aforo                                     int(11)              DEFAULT NULL,
  elementoComTicket                         int(11)              DEFAULT NULL,
  recomendarRecargosCanal                   tinyint(1)           DEFAULT NULL,
  recargoMaximo                             double               DEFAULT NULL,
  recargoMinimo                             double               DEFAULT NULL,
  idPlantillaTicket                         int(11)              DEFAULT NULL,
  creadoPor                                 int(11)              DEFAULT NULL,
  modificadoPor                             int(11)              DEFAULT NULL,
  objetivoSobreEntradas                     int(11)              DEFAULT NULL,
  objetivoSobreVentas                       double               DEFAULT NULL,
  idListaSubscripcion                       int(11)              DEFAULT NULL,
  invitacionUsaPlantillaTicket              tinyint(1)           DEFAULT NULL,
  idPlantillaTicketInvitacion               int(11)              DEFAULT NULL,
  elementoComTicketInvitacion               int(11)              DEFAULT NULL,
  archivado                                 tinyint(1)           DEFAULT NULL,
  idPlantillaTicketTaquilla                 int(11)              DEFAULT NULL,
  idPlantillaTicketTaquillaInvitacion       int(11)              DEFAULT NULL,
  elementoComTicketTaquilla                 int(11)              DEFAULT NULL,
  elementoComTicketTaquillaInvitacion       int(11)              DEFAULT NULL,
  elementoComEmail                          int(11)              DEFAULT NULL,
  tipoAbono                                 tinyint(4)           DEFAULT NULL,
  permiteReservas                           tinyint(1)           DEFAULT NULL,
  tipoCaducidadReserva                      tinyint(4)           DEFAULT NULL COMMENT '1:Sin plazo\n2:Pasados x tiempo despues de la \n   reserva \n',
  numUnidadesCaducidad                      int(11)              DEFAULT NULL,
  tipoUnidadesCaducidad                     tinyint(4)           DEFAULT NULL COMMENT '1: Dias\n2: Semanas\n3: Meses\n4: Horas',
  tipoFechaLimiteReserva                    tinyint(4)           DEFAULT NULL COMMENT '1: Sin fecha limite\n2: X tiempo antes de la sesion\n3: Con fecha limite',
  numUnidadesLimite                         int(11)              DEFAULT NULL,
  tipoUnidadesLimite                        tinyint(4)           DEFAULT NULL COMMENT '1: Dias\n2: Semanas\n3: Meses',
  tipoLimite                                tinyint(4)           DEFAULT NULL COMMENT '1:Antes\n2:Despues',
  fechaLimite                               datetime             DEFAULT NULL,
  fechaInicioReserva                        datetime             DEFAULT NULL,
  fechaInicioReservaTZ                      int(11)              DEFAULT NULL,
  fechaFinReserva                           datetime             DEFAULT NULL,
  fechaFinReservaTZ                         int(11)              DEFAULT NULL,
  idPromotor                                int(11)              DEFAULT NULL,
  usarDatosFiscalesProductor                tinyint(1)           DEFAULT NULL,
  usaElementosComGira                       tinyint(1)           DEFAULT NULL,
  recomendarRecargosInvCanal                tinyint(1)           DEFAULT NULL,
  recargoInvMaximo                          double               DEFAULT NULL,
  recargoInvMinimo                          double               DEFAULT NULL,
  recomendarRecargosPromocionCanal          tinyint(1)           DEFAULT NULL,
  recargoPromocionMaximo                    double               DEFAULT NULL,
  recargoPromocionMinimo                    double               DEFAULT NULL,
  usaNombreSesion                           tinyint(4)           DEFAULT NULL COMMENT '1:Utiliza elemntos ticket normalmente\n2:Utiliza nombre sesion como titulo\n3:Utiliza nombres sesion subtitulo',
  permitirInformesRecinto                   tinyint(1)           DEFAULT NULL,
  estadoPurgadoPdfs                         tinyint(4)           DEFAULT NULL COMMENT '0:Sin iniciar\n1:En proceso\n2:Finalizado OK\n3:Finalizado KO',
  idExterno                                 int(11)              DEFAULT NULL,
  idConfiguracionDefecto                    int(11)              DEFAULT NULL,
  idCalificacionEdad                        varchar(2)  NOT NULL DEFAULT 'E0',
  dirtyBI                                   bit(1)      NOT NULL DEFAULT 0,
  numHorasCaducidadReserva                  int(11)              DEFAULT NULL,
  numHorasLimiteReserva                     int(11)              DEFAULT NULL,
  esSupraEvento                             tinyint(1)           DEFAULT NULL,
  permiteGrupos                             tinyint(1)           DEFAULT NULL,
  idPlantillaTicketGrupos                   int(11)              DEFAULT NULL,
  idPlantillaTicketTaquillaGrupos           int(11)              DEFAULT NULL,
  idPlantillaTicketInvitacionGrupos         int(11)              DEFAULT NULL,
  idPlantillaTicketTaquillaInvitacionGrupos int(11)              DEFAULT NULL,
  precioGrupos                              int(11)              DEFAULT NULL COMMENT '1 - Fijo \n2 - Variable',
  acompanyantesGrupoPagan                   tinyint(1)           DEFAULT NULL,
  elementosComPassbook                      int(11)              DEFAULT NULL,
  entradaRegalo                             tinyint(1)           DEFAULT 0,
  createDate                                datetime             DEFAULT CURRENT_TIMESTAMP,
  updateDate                                datetime             DEFAULT CURRENT_TIMESTAMP,
  create_date                               timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date                               timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  attendantsSessionsByDefault               tinyint(1)           DEFAULT NULL,
  nominal                                   tinyint(1)  NOT NULL DEFAULT 0,
  useTieredPricing                          tinyint(1) NOT NULL DEFAULT 0,
  useOldPassbook                            tinyint(1) NOT NULL DEFAULT 0,
  invoicePrefixId                           int(11)             DEFAULT NULL,
  allowChannelUseAlternativeCharges         bit(1)      NOT NULL DEFAULT 0,
  externalReference                        varchar(50)          DEFAULT NULL,
  taxMode                                   int(11)          DEFAULT 0,
  PRIMARY KEY (idEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_idioma_com_evento (
                        `idIdioma` int(11) NOT NULL,
                        `idEvento` int(11) NOT NULL,
                        `defecto` tinyint(1) NOT NULL,
                        PRIMARY KEY (`idIdioma`,`idEvento`),
                        KEY `fk_idioma_plantilla_plantilla` (`idEvento`),
                        KEY `fk_idioma_evento_idioma` (`idIdioma`),
                        CONSTRAINT `fk_idioma_evento_evento` FOREIGN KEY (`idEvento`) REFERENCES `cpanel_evento` (`idEvento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
                        CONSTRAINT `fk_idioma_evento_idioma` FOREIGN KEY (`idIdioma`) REFERENCES `cpanel_idioma` (`idIdioma`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_config_recinto(
  idConfiguracion int(11) NOT NULL AUTO_INCREMENT,
  idRecinto int(11) NOT NULL,
  nombreConfiguracion varchar(50) NOT NULL,
  estado int(11) NOT NULL ,
  descripcion varchar(200) DEFAULT NULL,
  tipoNumeracion int(11) DEFAULT '0',
  configuracionAsociada int(11) DEFAULT NULL,
  tipo int(11) NOT NULL,
  aforo int(11) DEFAULT NULL,
  esGrafica tinyint(1) NOT NULL DEFAULT '0',
  creador int(11) DEFAULT NULL,
  fechaAlta datetime NOT NULL,
  fechaUltimaModificacion datetime DEFAULT NULL,
  publicada tinyint(1) DEFAULT NULL,
  idEvento int(11) DEFAULT NULL,
  urlImagenDefecto varchar(500) DEFAULT NULL,
  idExterno int(11) DEFAULT NULL,
  fechaPropagacionDatosExterna datetime DEFAULT NULL,
  hayCambiosExternos tinyint(1) DEFAULT NULL,
  tipoPlantilla int(11) DEFAULT '1' ,
  estadoProcesoExterno int(11) DEFAULT '1',
  currentSequence bigint(20) DEFAULT NULL,
  lastSequence bigint(20) DEFAULT NULL,
  dirtyBI int(1) NOT NULL DEFAULT '0',
  espacioRecinto int(11) DEFAULT NULL,
  thirdPartyIntegration bit(1) NOT NULL DEFAULT '0',
  createDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  updateDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idConfiguracion)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_recinto (
  idRecinto int(11) NOT NULL AUTO_INCREMENT,
  idTipoRecinto int(11) NOT NULL,
  idEntidad int(11) NOT NULL,
  nombre varchar(200) NOT NULL,
  codigo varchar(20) DEFAULT NULL,
  direccion varchar(400) DEFAULT NULL,
  pais int(11) NOT NULL,
  provincia int(11) NOT NULL,
  municipio varchar(50) DEFAULT NULL,
  codigoPostal varchar(10) DEFAULT NULL,
  coordenada varchar(150) DEFAULT NULL,
  pathLogo varchar(200) DEFAULT NULL,
  descripcion int(11) DEFAULT NULL,
  caracterirsticasTecnicasGrl int(11) DEFAULT NULL,
  caracteristicasTecnicasTaquilla int(11) DEFAULT NULL,
  caracteristicasTecnicasCtrlAcceso int(11) DEFAULT NULL,
  aforoMaximo int(11) DEFAULT NULL,
  nombreContacto varchar(200) DEFAULT NULL,
  apellidosContacto varchar(200) DEFAULT NULL,
  cargoContacto varchar(200) DEFAULT NULL,
  telefonoContacto varchar(25) DEFAULT NULL,
  correoContacto varchar(200) DEFAULT NULL,
  controlAcceso varchar(15) NOT NULL,
  taquillaOnebox tinyint(1) DEFAULT NULL,
  estado int(11) DEFAULT NULL,
  fechaAlta date NOT NULL,
  empresaGestora varchar(150) DEFAULT NULL,
  empresaPropietaria varchar(150) DEFAULT NULL,
  multisala tinyint(1) DEFAULT NULL,
  url varchar(250) DEFAULT NULL,
  publico tinyint(1) DEFAULT NULL,
  zonaHoraria int(11) DEFAULT NULL,
  dirtyBI bit(1) NOT NULL DEFAULT '0',
  idCalendario int(11) DEFAULT NULL,
  timeZone int(11) NOT NULL,
  googlePlaceId varchar(500) DEFAULT NULL,
  createDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  updateDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idRecinto)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad (
  idEntidad int(11) NOT NULL PRIMARY KEY,
  idOperadora int(11) NOT NULL,
  nombre varchar(50) NOT NULL,
  estado int(11) NOT NULL
);
CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_sesion
(
  idSesion                       int(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  idEvento                       int(11)     NOT NULL,
  idRelacionEntidadRecinto       int(11)     NOT NULL,
  nombre                         varchar(50) NOT NULL,
  descripcion                    varchar(45)          DEFAULT NULL,
  fechaInicioSesion              datetime    NOT NULL,
  fechaFinSesion                 datetime    NOT NULL,
  fechaVenta                     datetime             DEFAULT NULL,
  fechaPublicacion               datetime             DEFAULT NULL,
  duracion                       int(11)              DEFAULT NULL,
  estado                         int(11)     NOT NULL COMMENT '0:borrada\n1:planificada\n2:en programacion\n3:lista\n4:cancelada\n5:no realizada\n6:en proceso\n7:finalizada',
  estadoGeneracionAforo          int(11)              DEFAULT NULL COMMENT 'Estado del proceso de generacion del aforo:\n 0: no generado\n 1: en proceso\n 2: generado\n 3: error',
  estadoReplanchadoAforo         int(11)              DEFAULT NULL,
  aforo                          int(11)              DEFAULT NULL,
  publicado                      tinyint(1)  NOT NULL DEFAULT '0',
  enVenta                        tinyint(1)  NOT NULL DEFAULT '0',
  motivoCancelacionVenta         int(11)              DEFAULT NULL COMMENT '0:parada tecnica\n1:parada por motivos internos\n2:locallidades agotadas',
  motivoCancelacionPublicacion   int(11)              DEFAULT NULL COMMENT '0:parada tecnica\n1:parada por motivos internos\n2:locallidades agotadas',
  migradoBiPromotor              tinyint(1)           DEFAULT NULL,
  migradoBiCanal                 tinyint(1)           DEFAULT NULL,
  esAbono                        tinyint(1)           DEFAULT NULL,
  color                          int(11)              DEFAULT NULL,
  fechaInicioReserva             datetime             DEFAULT NULL,
  fechaFinReserva                datetime             DEFAULT NULL,
  reservasActivas                tinyint(1)           DEFAULT NULL,
  estadoPurgado                  tinyint(4)           DEFAULT NULL COMMENT '0:Sin iniciar\n1:Pendiente purgar\n2:En proceso\n3:Finalizado OK\n4:Finalizado KO',
  idExterno                      int(11)              DEFAULT NULL,
  idImpuesto                     int(11)              DEFAULT NULL,
  idImpuestoRecargo              int(11)              DEFAULT NULL,
  fechaNoDefinitiva              tinyint(1)           DEFAULT NULL,
  fechaModificacionExterna       datetime             DEFAULT NULL,
  espacioValidacionAcceso        int(11)              DEFAULT NULL,
  fechaRealFinSesion             datetime             DEFAULT NULL,
  tipoHorarioAccesos             tinyint(4)           DEFAULT NULL COMMENT '1: Configuracion aoutomatica\n2: Horario especifico',
  aperturaAccesos                datetime             DEFAULT NULL,
  cierreAccesos                  datetime             DEFAULT NULL,
  dirtyBI                        bit(1)      NOT NULL DEFAULT '0',
  numMaxLocalidadesCompra        int(11)              DEFAULT NULL,
  captcha                        tinyint(1)           DEFAULT NULL,
  elementoComTicketTaquilla      int(11)              DEFAULT NULL,
  elementoComTicket              int(11)              DEFAULT NULL,
  mostrarHorario                 tinyint(1)           DEFAULT '1',
  vinculoConfigRecinto           bit(1)               DEFAULT '0',
  tipoVenta                      int(11)              DEFAULT NULL,
  usaAccesosPlantilla            bit(1)               DEFAULT '1',
  usaLimitesCuposPlantillaEvento tinyint(1)           DEFAULT NULL,
  usarDatosFiscalesProductor     tinyint(1)           DEFAULT NULL,
  idPromotor                     int(11)              DEFAULT NULL,
  razonCancelacionPublicacion    varchar(200)         DEFAULT NULL,
  idListaSubscripcion            int(11)              DEFAULT NULL,
  isExternal                     bit(1)               DEFAULT '0',
  isPreview                      bit(1)      NOT NULL DEFAULT '0',
  hideSessionDates               tinyint(1)           DEFAULT '0',
  showDate                       bit(1)      NOT NULL DEFAULT '1',
  showDatetime                   bit(1)      NOT NULL DEFAULT '1',
  createDate                     datetime             DEFAULT CURRENT_TIMESTAMP,
  updateDate                     datetime             DEFAULT CURRENT_TIMESTAMP,
  create_date                    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date                    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  archivado                      tinyint(1)  NOT NULL DEFAULT '0',
  externalReference                        varchar(50)          DEFAULT NULL
  );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_recinto_config
(
  idRelacionEntRecinto int(11)   NOT NULL, -- AUTO_INCREMENT,
  idEntidad            int(11)   NOT NULL,
  idConfiguracion      int(11)   NOT NULL,
  createDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  updateDate           datetime           DEFAULT CURRENT_TIMESTAMP,
  create_date          timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date          timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idRelacionEntRecinto)
);


-- <editor-fold desc="Time_Zone_group">
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


CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_season_ticket (
  `idEvento` int(11) NOT NULL,
  `isMemberMandatory` bit(1) NOT NULL DEFAULT '0',
  `allowRenewal` bit(1) NOT NULL DEFAULT '0',
  `renewalInitDate` datetime DEFAULT NULL,
  `renewalEndDate` datetime DEFAULT NULL,
  `renewalEnabled` bit(1) DEFAULT NULL,
  `allowChangeSeat` bit(1) NOT NULL DEFAULT '0',
  `changeSeatInitDate` datetime DEFAULT NULL,
  `changeSeatEndDate` datetime DEFAULT NULL,
  `changeSeatEnabled` bit(1) NOT NULL DEFAULT '0',
  `enableChangedSeatQuota` bit(1) NOT NULL DEFAULT '0',
  `changedSeatQuotaId` int DEFAULT NULL,
  `changedSeatStatus` varchar(50) DEFAULT NULL,
  `changedSeatBlockReasonId` int DEFAULT NULL,
  `maxChangeSeatValueEnabled` bit(1) DEFAULT '0',
  `maxChangeSeatValue` int(11),
  `allowTransferTicket` bit(1) NOT NULL DEFAULT '0',
  `transferTicketMaxDelayTime` int(11) NULL,
  `recoveryTicketMaxDelayTime` int(11) NULL,
  `enableMaxTicketTransfers` bit(1) NOT NULL DEFAULT '0',
  `maxTicketTransfers` int(11) NULL,
  `allowReleaseSeat` bit(1) NOT NULL DEFAULT '0',
  `transferTicketMinDelayTime` int(11) NULL,
  `transferPolicy` tinyint(1) NULL,
  `customerMaxSeats` int NULL,
  `registerMandatory` bit(1) NOT NULL DEFAULT '0',
  `autoRenewal` bit(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idEvento`),
  KEY `fk_season_ticket_evento` (`idEvento`),
  CONSTRAINT `fk_season_ticket_evento`
     FOREIGN KEY (`idEvento`)
     REFERENCES `cpanel_evento` (`idEvento`)
     ON DELETE NO ACTION ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_admin_entidades (
    idEntidadAdmin int(11) NOT NULL,
    idEntidad int(11) NOT NULL,
    PRIMARY KEY (idEntidadAdmin,idEntidad),
    KEY fk_entidad_entidades (idEntidad),
    CONSTRAINT fk_entidad_admin_entidades FOREIGN KEY (idEntidadAdmin) REFERENCES cpanel_entidad (idEntidad),
    CONSTRAINT fk_entidad_entidades FOREIGN KEY (idEntidad) REFERENCES cpanel_entidad (idEntidad)
);

INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (1, -720, 0, 'Etc/GMT+12', '(GMT -12:00) International Date Line West', 'Dateline Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (2, -660, 0, 'Etc/GMT+11', '(GMT -11:00) Coordinated Universal Time-11', 'GMT -11', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (3, -600, 0, 'Pacific/Honolulu', '(GMT -10:00) Hawaii', 'Hawaiian Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (4, -540, 1, 'America/Anchorage', '(GMT -09:00) Alaska', 'Alaskan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (5, -480, 1, 'America/Tijuana', '(GMT -08:00) Baja California', 'Pacific Standard Time (Mexico)', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (6, -480, 1, 'America/Los_Angeles', '(GMT -08:00) Pacific Time (US & Canada)', 'Pacific Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (7, -420, 0, 'America/Phoenix', '(GMT -07:00) Arizona', 'US Mountain Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (8, -420, 1, 'America/Chihuahua', '(GMT -07:00) Chihuahua, La Paz, Mazatlan', 'Mountain Standard Time (Mexico)', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (9, -420, 1, 'America/Denver', '(GMT -07:00) Mountain Time (US & Canada)', 'Mountain Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (10, -360, 0, 'America/Guatemala', '(GMT -06:00) Central America', 'Central America Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (11, -360, 1, 'America/Chicago', '(GMT -06:00) Central Time (US & Canada)', 'Central Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (12, -360, 1, 'America/Mexico_City', '(GMT -06:00) Guadalajara, Mexico City, Monterrey', 'Central Standard Time (Mexico)', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (13, -360, 0, 'America/Regina', '(GMT -06:00) Saskatchewan', 'Canada Central Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (14, -300, 0, 'America/Bogota', '(GMT -05:00) Bogota, Lima, Quito', 'SA Pacific Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (15, -300, 1, 'America/New_York', '(GMT -05:00) Eastern Time (US & Canada)', 'Eastern Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (16, -300, 1, 'America/Indianapolis', '(GMT -05:00) Indiana (East)', 'US Eastern Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (17, -270, 0, 'America/Caracas', '(GMT -04:30) Caracas', 'Venezuela Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (18, -240, 1, 'America/Asuncion', '(GMT -04:00) Asuncion', 'Paraguay Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (19, -240, 1, 'America/Halifax', '(GMT -04:00) Atlantic Time (Canada)', 'Atlantic Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (20, -240, 1, 'America/Cuiaba', '(GMT -04:00) Cuiaba', 'Central Brazilian Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (21, -240, 0, 'America/La_Paz', '(GMT -04:00) Georgetown, La Paz, Manaus, San Juan', 'SA Western Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (22, -240, 1, 'America/Santiago', '(GMT -04:00) Santiago', 'Pacific SA Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (23, -210, 1, 'America/St_Johns', '(GMT -03:30) Newfoundland', 'Newfoundland Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (24, -180, 1, 'America/Sao_Paulo', '(GMT -03:00) Brasilia', 'E. South America Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (25, -180, 0, 'America/Buenos_Aires', '(GMT -03:00) Buenos Aires', 'Argentina Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (26, -180, 0, 'America/Cayenne', '(GMT -03:00) Cayenne, Fortaleza', 'SA Eastern Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (27, -180, 1, 'America/Godthab', '(GMT -03:00) Greenland', 'Greenland Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (28, -180, 1, 'America/Montevideo', '(GMT -03:00) Montevideo', 'Montevideo Standard Time', 'S');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (29, -120, 0, 'Etc/GMT+2', '(GMT -02:00) Coordinated Universal Time-02', 'GMT -02', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (30, -120, 0, 'Etc/GMT+2', '(GMT -02:00) Mid-Atlantic', 'Mid-Atlantic Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (31, -60, 1, 'Atlantic/Azores', '(GMT -01:00) Azores', 'Azores Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (32, -60, 0, 'Atlantic/Cape_Verde', '(GMT -01:00) Cape Verde Is.', 'Cape Verde Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (33, 0, 1, 'Africa/Casablanca', '(GMT) Casablanca', 'Morocco Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (34, 0, 0, 'Etc/GMT', '(GMT) Coordinated Universal Time', 'GMT ', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (35, 0, 1, 'Europe/London', '(GMT) Dublin, Edinburgh, Lisbon, London', 'GMT Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (36, 0, 0, 'Atlantic/Reykjavik', '(GMT) Monrovia, Reykjavik', 'Greenwich Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (37, 60, 1, 'Europe/Berlin', '(GMT +01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna', 'W. Europe Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (38, 60, 1, 'Europe/Budapest', '(GMT +01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague', 'Central Europe Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (39, 60, 1, 'Europe/Berlin', '(GMT +01:00) Brussels, Copenhagen, Madrid, Paris', 'Romance Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (40, 60, 1, 'Europe/Warsaw', '(GMT +01:00) Sarajevo, Skopje, Warsaw, Zagreb', 'Central European Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (41, 60, 0, 'Africa/Lagos', '(GMT +01:00) West Central Africa', 'W. Central Africa Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (42, 60, 1, 'Africa/Windhoek', '(GMT +02:00) Windhoek', 'Namibia Standard Time', 'S');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (43, 120, 1, 'Asia/Amman', '(GMT +02:00) Amman', 'Jordan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (44, 120, 1, 'Europe/Istanbul', '(GMT +02:00) Athens, Bucharest, Istanbul', 'GTB Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (45, 120, 1, 'Asia/Beirut', '(GMT +02:00) Beirut', 'Middle East Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (46, 120, 0, 'Africa/Cairo', '(GMT +02:00) Cairo', 'Egypt Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (47, 120, 1, 'Asia/Damascus', '(GMT +02:00) Damascus', 'Syria Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (48, 120, 0, 'Africa/Johannesburg', '(GMT +02:00) Harare, Pretoria', 'South Africa Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (49, 120, 1, 'Europe/Kiev', '(GMT +02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius', 'FLE Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (50, 120, 1, 'Asia/Jerusalem', '(GMT +02:00) Jerusalem', 'Israel Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (51, 180, 0, 'Europe/Minsk', '(GMT +02:00) Minsk', 'E. Europe Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (52, 180, 0, 'Asia/Baghdad', '(GMT +03:00) Baghdad', 'Arabic Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (53, 180, 0, 'Asia/Riyadh', '(GMT +03:00) Kuwait, Riyadh', 'Arab Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (54, 180, 0, 'Africa/Nairobi', '(GMT +03:00) Nairobi', 'E. Africa Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (55, 210, 1, 'Asia/Tehran', '(GMT +03:30) Tehran', 'Iran Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (56, 240, 0, 'Europe/Moscow', '(GMT +03:00) Moscow, St. Petersburg, Volgograd', 'Russian Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (57, 240, 0, 'Asia/Dubai', '(GMT +04:00) Abu Dhabi, Muscat', 'Arabian Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (58, 240, 1, 'Asia/Baku', '(GMT +04:00) Baku', 'Azerbaijan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (59, 240, 0, 'Indian/Mauritius', '(GMT +04:00) Port Louis', 'Mauritius Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (60, 240, 0, 'Asia/Tbilisi', '(GMT +04:00) Tbilisi', 'Georgian Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (61, 240, 0, 'Asia/Yerevan', '(GMT +04:00) Yerevan', 'Caucasus Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (62, 270, 0, 'Asia/Kabul', '(GMT +04:30) Kabul', 'Afghanistan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (63, 300, 0, 'Asia/Karachi', '(GMT +05:00) Islamabad, Karachi', 'Pakistan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (64, 300, 0, 'Asia/Tashkent', '(GMT +05:00) Tashkent', 'West Asia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (65, 330, 0, 'Asia/Calcutta', '(GMT +05:30) Chennai, Kolkata, Mumbai, New Delhi', 'India Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (66, 330, 0, 'Asia/Colombo', '(GMT +05:30) Sri Jayawardenepura', 'Sri Lanka Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (67, 345, 0, 'Asia/Katmandu', '(GMT +05:45) Kathmandu', 'Nepal Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (68, 360, 0, 'Asia/Yekaterinburg', '(GMT +05:00) Ekaterinburg', 'Ekaterinburg Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (69, 360, 0, 'Asia/Almaty', '(GMT +06:00) Astana', 'Central Asia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (70, 360, 0, 'Asia/Dhaka', '(GMT +06:00) Dhaka', 'Bangladesh Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (71, 390, 0, 'Asia/Rangoon', '(GMT +06:30) Yangon (Rangoon)', 'Myanmar Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (72, 420, 0, 'Asia/Novosibirsk', '(GMT +06:00) Novosibirsk', 'N. Central Asia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (73, 420, 0, 'Asia/Bangkok', '(GMT +07:00) Bangkok, Hanoi, Jakarta', 'SE Asia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (74, 480, 0, 'Asia/Krasnoyarsk', '(GMT +07:00) Krasnoyarsk', 'North Asia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (75, 480, 0, 'Asia/Shanghai', '(GMT +08:00) Beijing, Chongqing, Hong Kong, Urumqi', 'China Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (76, 480, 0, 'Asia/Singapore', '(GMT +08:00) Kuala Lumpur, Singapore', 'Singapore Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (77, 480, 0, 'Australia/Perth', '(GMT +08:00) Perth', 'W. Australia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (78, 480, 0, 'Asia/Taipei', '(GMT +08:00) Taipei', 'Taipei Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (79, 480, 0, 'Asia/Ulaanbaatar', '(GMT +08:00) Ulaanbaatar', 'Ulaanbaatar Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (80, 540, 0, 'Asia/Irkutsk', '(GMT +08:00) Irkutsk', 'North Asia East Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (81, 540, 0, 'Asia/Tokyo', '(GMT +09:00) Osaka, Sapporo, Tokyo', 'Tokyo Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (82, 540, 0, 'Asia/Seoul', '(GMT +09:00) Seoul', 'Korea Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (83, 570, 1, 'Australia/Adelaide', '(GMT +09:30) Adelaide', 'Cen. Australia Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (84, 570, 0, 'Australia/Darwin', '(GMT +09:30) Darwin', 'AUS Central Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (85, 600, 0, 'Asia/Yakutsk', '(GMT +09:00) Yakutsk', 'Yakutsk Standard Time', 'N');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (86, 600, 0, 'Australia/Brisbane', '(GMT +10:00) Brisbane', 'E. Australia Standard Time', 'S');
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (87, 600, 1, 'Australia/Sydney', '(GMT +10:00) Canberra, Melbourne, Sydney', 'AUS Eastern Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (88, 600, 0, 'Pacific/Port_Moresby', '(GMT +10:00) Guam, Port Moresby', 'West Pacific Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (89, 600, 1, 'Australia/Hobart', '(GMT +10:00) Hobart', 'Tasmania Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (90, 660, 0, 'Asia/Vladivostok', '(GMT +10:00) Vladivostok', 'Vladivostok Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (91, 660, 0, 'Pacific/Guadalcanal', '(GMT +11:00) Solomon Is., New Caledonia', 'Central Pacific Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (92, 720, 0, 'Asia/Magadan', '(GMT +11:00) Magadan', 'Magadan Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (93, 720, 1, 'Pacific/Auckland', '(GMT +12:00) Auckland, Wellington', 'New Zealand Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (94, 720, 0, 'Etc/GMT-12', '(GMT +12:00) Coordinated Universal Time+12', 'GMT +12', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (95, 720, 1, 'Pacific/Fiji', '(GMT +12:00) Fiji, Marshall Is.', 'Fiji Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (96, 720, 0, 'Asia/Kamchatka', '(GMT +12:00) Petropavlovsk-Kamchatsky - Old', 'Kamchatka Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (97, 780, 0, 'Pacific/Tongatapu', '(GMT +13:00) Nuku''alofa', 'Tonga Standard Time', null);
INSERT INTO ob_cpanel.cpanel_time_zone_group VALUES (98, 780, 1, 'Pacific/Apia', '(GMT -11:00) Samoa', 'Samoa Standard Time', null);
-- </editor-fold>

INSERT INTO ob_cpanel.cpanel_idioma
(`idIdioma`, `codigo`, `descripcion`, `idiomaPlataforma`) VALUES
('1', 'es_ES', 'Castellano', '1'),
('2', 'ca_ES', 'Català', '1'),
('3', 'en_US', 'English', '1'),
('4', 'ca_ES_valencia', 'Valencià', '0'),
('5', 'it_IT', 'Italiano', '0'),
('6', 'pt_PT', 'Português', '0'),
('7', 'fr_FR', 'Français', '0'),
('8', 'de_DE', 'Deutsch', '0');

INSERT INTO ob_cpanel.cpanel_taxonomia_base (idTaxonomia, idTaxonomiaSuperior,codigo,descripcion,orden,tipo)
VALUES (15, NULL,'ART','Artes escénicas',1,1);

INSERT INTO ob_cpanel.cpanel_taxonomia_propia
(idTaxonomia, idTaxonomiaSuperior,idEntidad,referencia,descripcion,orden,tipo)
VALUES (1, NULL,3,'001654','Custom category',1,1);

INSERT INTO ob_cpanel.cpanel_gira (idGira, idEntidad,nombre,descripcion,referenciaPromotor,estado,dirtyBI)
VALUES (1, 2,'Gira',NULL,'Gira',2,1);

INSERT INTO ob_cpanel.cpanel_promotor
(idPromotor, idEntidad,nombre,razonSocial,nif,estado,predeterminado,direccion,pais,provincia,municipio,cPostal,
 personaContacto,emailContacto,telefonoContacto,dirtyBI)
VALUES (96, 2,'Entidad TEST','Entidad TEST','666666666D',1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado,
 estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable,
 emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta,
 fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal,
 recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas,
 idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado,
 idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla,
 elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva,
 numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite,
 fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor,
 usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo,
 usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad,
 dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos,
 idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos,
 precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo,
 recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(1, 103, 1, 'A night at the Dorsia with Patrick Bateman', NULL, 1, '2016-06-30 20:00:00.000', 37,
       '2016-07-01 20:00:00.000', 37, 5, NULL, 15, 1, '5467457347', 'Mr Admin', 'Operadora', 'operadmin@onebox.es',
       '5+965969', NULL, '2016-04-20 14:13:43.000', '2017-03-09 11:40:06.000', '2017-03-09 11:40:06.000',
       '2016-04-20 08:00:00.000', 37, '2016-04-20 08:00:00.000', 37, NULL, 0, 13, 0, NULL, NULL, 1, 2, 2, 0, 0, NULL, 1,
       1, 11, 0, 6, 6, 14, 12, 22, 1, 1, 1, 1, 1, 1, 1, 4, 1, NULL, '2016-04-20 08:00:00.000', 37,
       '2016-07-01 20:00:00.000', 37, 96, 1, 0, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL,
       NULL, NULL, NULL, NULL, NULL, 4, 0, NULL, NULL, 0, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_idioma_com_evento
(`idIdioma`, `idEvento`, `defecto`) VALUES
('1', '1', '0'),
('2', '1', '1'),
('3', '1', '0');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(32, 399, NULL, 'Summer Festival Sound', NULL, 1, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, 1, 1, 1, 1, 1, 4, 1, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(9, 2, 'Config Evento NN', 1, NULL, NULL, 6, 3, 10000, 0, 1, '2016-04-20 14:33:30.000', NULL, 0, 1, NULL, 0, NULL, NULL, 1, NULL, NULL, NULL, 1, 2, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(64, 9, 'Plantilla no gráfica_QA_Entidad', 1, NULL, NULL, NULL, 3, 10000, 0, 1, '2017-02-14 08:02:52.000', NULL, 0, 32, NULL, 0, NULL, NULL, 1, NULL, NULL, NULL, 0, 13, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_recinto
(idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, create_date, update_date)
VALUES(1, 4, 103, 'Recinto super molon', NULL, 'Joanot Martorell', 1, 8, 'Barcelona', '08014', NULL, NULL, NULL, NULL, NULL, NULL, 1000, 'Irenica', 'Amo', '', '111222333', 'iamo@oneboxtm.com', 'A', 0, 1, '2016-04-19', 'Fundació Bancària La Caixa', 'FLC', NULL, '', 1, NULL, 1, NULL, 37, 'sdfsdfsdfsdf', '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');
INSERT INTO ob_cpanel.cpanel_recinto
(idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, create_date, update_date)
VALUES(12, 5, 412, 'Dorado Stadium', NULL, 'Roger de Flor 1', 1, 8, 'Barcelona', '08010', NULL, '268790_1542112803461.jpg', NULL, NULL, NULL, NULL, 999999999, 'asdads', 'asdad', 'asdasd', '6786786786', 'asdadsd@asdad.asd', 'A', 1, 1, '2018-11-09', 'asdasd', 'asdasd', NULL, 'asdasd', 1, NULL, 1, 5, 37, '42rsdfsdfsdfsdf', '2018-11-09 12:42:22.000', '2018-11-13 12:40:03.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(58, 400, NULL, 'Evento Enero', NULL, 1, '2018-05-12 08:00:00.000', 37, '2019-09-01 08:30:00.000', 37, 3, NULL, 20, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '0800000', NULL, '2018-05-04 07:48:47.000', '2018-12-10 15:29:07.000', '2018-05-14 14:59:20.000', '2018-05-11 08:00:00.000', 37, '2018-05-11 08:00:00.000', 37, NULL, 900, NULL, NULL, NULL, NULL, 18, 2, 2, NULL, 0.0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, '2018-05-14 08:00:00.000', 37, '2018-05-12 08:00:00.000', 37, 462, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2018-05-04 07:48:47.000', '2018-12-10 15:29:07.000');
INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(67, 412, NULL, 'The Strokes LIVE', NULL, 1, '2018-11-09 09:00:00.000', 37, '2021-12-31 09:30:00.000', 37, 3, NULL, 30, 5, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '687179089', NULL, '2018-11-06 12:10:50.000', '2018-12-13 12:51:40.000', '2018-11-06 12:51:11.000', '2018-11-06 09:00:00.000', 37, '2018-11-06 09:00:00.000', 37, NULL, 8400, 266, 0, NULL, NULL, 24, 2, 2, 500, 0.0, NULL, 1, 24, 264, 0, NULL, NULL, 267, 265, 237, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, '2018-11-06 09:00:00.000', 37, '2018-11-06 09:00:00.000', 37, 472, 1, 0, 0, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 73, 0, NULL, NULL, 0, 0, '2018-11-06 12:10:50.000', '2018-12-14 10:48:20.000');
INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(68, 412, NULL, 'QUEEN', NULL, 1, '2020-02-01 22:00:00.000', 37, '2020-02-02 22:30:00.000', 37, 3, NULL, 34, NULL, 'QUEEN', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '687478955', NULL, '2018-11-09 12:33:19.000', '2018-12-13 15:32:22.000', '2018-11-15 12:03:20.000', '2018-11-12 09:00:00.000', 37, '2018-11-12 09:00:00.000', 37, NULL, 200998, 270, 0, NULL, NULL, 24, 2, 2, 10000000, 0.0, NULL, 1, 24, 268, 0, NULL, NULL, 271, 269, 245, 0, 1, 1, 1, 1, 1, 1, 4, 1, NULL, '2018-11-12 09:00:00.000', 37, '2020-02-02 22:00:00.000', 37, 472, 1, 0, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, 'E0', 1, 0, 0, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, 74, 0, NULL, NULL, 0, 0, '2018-11-09 12:33:19.000', '2018-12-13 15:32:22.000');

INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(121, 1, 'Plantilla gráfica', 1, NULL, NULL, NULL, 3, 150, 1, 1, '2018-05-14 14:57:34.000', NULL, 0, 58, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, 0, 1, 0, '2018-05-14 14:57:34.000', '2018-05-14 14:58:50.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(132, 1, 'Plantilla no gráfica', 1, NULL, NULL, NULL, 3, NULL, 0, 1, '2018-09-25 11:44:48.000', NULL, 0, 58, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, 1, 0, '2018-09-25 11:44:48.000', '2018-09-25 11:44:48.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(137, 1, 'Plantilla dos Zonas NN DDorado', 1, NULL, NULL, 136, 3, 700, 1, 1, '2018-11-06 12:42:22.000', NULL, 0, 67, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, 1, 0, '2018-11-06 12:42:22.000', '2018-11-06 12:42:23.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(142, 12, 'Plantilla Grafica Dorado Stadium', 1, NULL, NULL, 141, 3, 100499, 1, 1, '2018-11-09 13:01:36.000', NULL, 0, 68, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, 0, 16, 0, '2018-11-09 13:01:36.000', '2018-11-15 12:02:13.000');
INSERT INTO ob_cpanel.cpanel_config_recinto
(idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date)
VALUES(10, 1, 'Plantilla Grafica Dorado Stadium', 1, NULL, NULL, 141, 3, 100499, 1, 1, '2018-11-09 13:01:36.000', NULL, 0, 50, NULL, NULL, NULL, NULL, 1, 50, NULL, NULL, 0, 16, 0, '2018-11-09 13:01:36.000', '2018-11-15 12:02:13.000');

INSERT INTO ob_cpanel.cpanel_entidad
(idEntidad, idOperadora, nombre, estado) VALUES
(103, 1, 'Entidad 103', 1),
(399, 1, 'Entidad 399', 1),
(400, 1, 'Entidad 400', 1),
(412, 1, 'Entidad 412', 1);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, createDate, updateDate, create_date, update_date, attendantsSessionsByDefault, nominal)
VALUES (285, 141, null, 'Timezones', null, 1, '2019-04-01 07:00:00', 53, '2019-04-03 15:30:00', 10, 3, null, 17, null, '', 'Operadora', 'Onebox', 'all@onebox.es', '123456789', null, '2019-04-01 12:04:05', '2019-04-01 12:11:03', '2019-04-01 12:11:03', '2019-04-01 03:00:00', 73, '2019-04-01 03:00:00', 73, null, 3000, null, null, null, null, 26, 2, 2, null, 0, 6, 0, null, null, 0, 70, null, null, null, null, 0, 0, 0, 1, 1, 2, 1, 4, 1, null, '2019-04-01 07:00:00', 53, '2019-04-01 07:00:00', 53, 27, 1, 0, null, null, null, null, null, null, null, 0, null, null, null, 'E0', true, 0, 0, 0, 0, null, null, null, null, null, null, null, 0, '2019-04-01 12:04:05', '2019-04-02 08:57:11', '2019-04-01 12:04:05', '2019-04-02 08:57:11', null, 0);

INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, createDate, updateDate, create_date, update_date, archivado)
VALUES (40583, 285, 551, 'Timezone +3', null, '2019-04-01 07:00:00', '2019-04-03 07:30:00', '2019-04-01 07:00:00', '2019-04-01 07:00:00', null, 3, 2, null, 1000, 1, 1, null, null, null, null, 0, 0, '2019-04-01 07:00:00', '2019-04-01 07:00:00', 0, 0, null, 13, 13, null, null, null, null, 1, null, null, true, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, false, false, 0, true, true, '2019-04-01 12:10:08', '2019-04-02 08:57:11', '2019-04-01 12:10:08', '2019-04-02 08:57:11', 0);
INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, createDate, updateDate, create_date, update_date, archivado)
VALUES (40584, 285, 553, 'Timezone +7', null, '2019-04-03 03:00:00', '2019-04-03 07:30:00', '2019-04-01 03:00:00', '2019-04-01 03:00:00', null, 3, 2, null, 1000, 1, 1, null, null, null, null, 0, 0, null, null, 0, 0, null, 11, 11, null, null, null, '2019-04-03 07:00:00', 1, null, null, false, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, false, false, 0, true, true, '2019-04-01 12:10:24', '2019-04-02 08:56:34', '2019-04-01 12:10:24', '2019-04-02 08:56:34', 0);
INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, createDate, updateDate, create_date, update_date, archivado)
VALUES (40585, 285, 552, 'Timezone -6', null, '2019-04-03 15:00:00', '2019-04-03 15:30:00', '2019-04-01 15:00:00', '2019-04-01 15:00:00', null, 3, 2, null, 1000, 1, 1, null, null, null, null, 0, 0, null, null, 0, 0, null, 11, 11, null, null, null, null, 1, null, null, true, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, false, false, 0, true, true, '2019-04-01 12:10:41', '2019-04-02 08:56:52', '2019-04-01 12:10:41', '2019-04-02 08:56:52', 0);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, createDate, updateDate, create_date, update_date, attendantsSessionsByDefault, nominal)
VALUES (286, 141, null, 'Timezones', null, 1, '2019-04-01 07:00:00', 53, '2019-04-03 15:30:00', 10, 3, null, 17, null, '', 'Operadora', 'Onebox', 'all@onebox.es', '123456789', null, '2019-04-01 12:04:05', '2019-04-01 12:11:03', '2019-04-01 12:11:03', '2019-04-01 03:00:00', 73, '2019-04-01 03:00:00', 73, null, 3000, null, null, null, null, 26, 2, 2, null, 0, 6, 1, null, null, 0, 70, null, null, null, null, 0, 0, 0, 1, 1, 2, 1, 4, 1, null, null, null, null, null, 27, 1, 0, null, null, null, null, null, null, null, 0, null, null, null, 'E0', true, 0, 0, 0, 0, null, null, null, null, null, null, null, 0, '2019-04-01 12:04:05', '2019-04-02 08:57:11', '2019-04-01 12:04:05', '2019-04-02 08:57:11', null, 0);
INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, createDate, updateDate, create_date, update_date, archivado)
VALUES (40586, 286, 551, 'Timezone +3', null, '2019-04-01 07:00:00', '2019-04-03 07:30:00', '2019-04-01 07:00:00', '2019-04-01 07:00:00', null, 3, 2, null, 1000, 1, 1, null, null, null, null, 0, 0, null, null, 0, 0, null, 13, 13, null, null, null, null, 1, null, null, true, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, false, false, 0, true, true, '2019-04-01 12:10:08', '2019-04-02 08:57:11', '2019-04-01 12:10:08', '2019-04-02 08:57:11', 0);

INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (idRelacionEntRecinto, idEntidad, idConfiguracion, createDate, updateDate, create_date, update_date) VALUES (551, 141, 551, '2019-04-01 12:04:49', '2019-04-01 12:04:49', '2019-04-01 12:04:49', '2019-04-01 12:04:49');
INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (idRelacionEntRecinto, idEntidad, idConfiguracion, createDate, updateDate, create_date, update_date) VALUES (553, 141, 553, '2019-04-01 12:08:32', '2019-04-01 12:08:32', '2019-04-01 12:08:32', '2019-04-01 12:08:32');
INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (idRelacionEntRecinto, idEntidad, idConfiguracion, createDate, updateDate, create_date, update_date) VALUES (552, 141, 552, '2019-04-01 12:05:43', '2019-04-01 12:05:43', '2019-04-01 12:05:43', '2019-04-01 12:05:43');

INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, createDate, updateDate, create_date, update_date) VALUES (551, 97, '+3', 1, null, null, null, 3, 1000, 0, 2, '2019-04-01 12:04:49', null, 0, 285, null, null, null, null, 1, null, null, null, false, 149, false, '2019-04-01 12:04:49', '2019-04-01 12:09:34', '2019-04-01 12:04:49', '2019-04-01 12:09:34');
INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, createDate, updateDate, create_date, update_date) VALUES (553, 100, '+7', 1, null, null, null, 3, 1000, 0, 2, '2019-04-01 12:08:32', null, 0, 285, null, null, null, null, 1, null, null, null, true, 152, false, '2019-04-01 12:08:32', '2019-04-01 12:09:21', '2019-04-01 12:08:32', '2019-04-01 12:09:21');
INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, createDate, updateDate, create_date, update_date) VALUES (552, 96, '-6', 1, null, null, null, 3, 1000, 0, 2, '2019-04-01 12:05:43', null, 0, 285, null, null, null, null, 1, null, null, null, true, 148, false, '2019-04-01 12:05:43', '2019-04-01 12:05:53', '2019-04-01 12:05:43', '2019-04-01 12:05:53');

INSERT INTO ob_cpanel.cpanel_recinto (idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, createDate, updateDate, create_date, update_date) VALUES (97, 8, 141, 'Timezone +3', null, null, 172, 241, 'UTC +3', null, null, null, null, null, null, null, 1000, 'test', 'test', '', '123456789', 'test@test.com', 'A', 0, 1, '2019-04-01', 'jooq +3', 'jooq +3', null, '', 1, null, true, null, 53, 'arwerwer', '2019-04-01 11:51:49', '2019-04-01 11:52:54', '2019-04-01 11:51:49', '2019-04-01 11:52:54');
INSERT INTO ob_cpanel.cpanel_recinto (idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, createDate, updateDate, create_date, update_date) VALUES (100, 9, 141, 'Timezone +7', null, '', 1, 38, 'asd', '', null, null, null, null, null, null, 1000, 'test', 'test', '', '1231456798', 'test@test.com', 'A', 0, 1, '2019-04-01', 'jooq +7', 'jooq +7', null, '', 1, null, true, null, 73, 'sdfsdfsd', '2019-04-01 12:07:18', '2019-04-01 12:08:01', '2019-04-01 12:07:18', '2019-04-01 12:08:01');
INSERT INTO ob_cpanel.cpanel_recinto (idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, createDate, updateDate, create_date, update_date) VALUES (96, 4, 141, 'Timezone -6', null, '', 53, 308, 'La concha', '', null, null, null, null, null, null, 1000, 'test', 'test', '', '123456789', 'test@test.com', 'A', 0, 1, '2019-04-01', 'jooq -6', 'jooq -6', null, '', 1, null, true, null, 10, 'sdfsdfsfd', '2019-04-01 11:44:23', '2019-04-01 12:18:06', '2019-04-01 11:44:23', '2019-04-01 12:18:06');

INSERT INTO ob_cpanel.cpanel_entidad (idEntidad, idOperadora, nombre, estado) VALUES (141, 1, 'Entidad ESP', 1);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, createDate, updateDate, create_date, update_date, attendantsSessionsByDefault, nominal)
VALUES (83, 140, null, 'Finalizado No tocar 20', null, 1, '2015-01-30 08:00:00', 39, '2015-05-01 06:00:00', 39, 7, null, 22, null, '', 'Operadora', 'Onebox', 'all@onebox.es', '788909098', null, '2015-01-26 17:47:01', '2015-01-29 15:42:16', '2015-01-28 14:18:41', '2015-01-27 12:00:00', 39, '2015-01-27 12:00:00', 39, null, 320, 434, 0, null, null, 31, 2, 2, 0, 0, null, 1, 31, 433, 0, 32, 32, 435, 433, 650, 0, 0, 1, 1, 1, 2, 1, 4, 1, null, '2015-01-26 21:00:00', 39, '2015-02-01 09:00:00', 39, 26, 1, 0, 0, null, null, 0, null, null, null, 0, 2, null, null, 'E0', true, 0, 0, 0, 0, null, null, null, null, null, null, 39, 0, null, null, '2017-10-25 10:00:39', '2017-10-25 10:00:39', null, 0);
INSERT INTO ob_cpanel.cpanel_evento (idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, createDate, updateDate, create_date, update_date, attendantsSessionsByDefault, nominal)
VALUES (129, 140, null, 'Evento actividad facturas', null, 3, '2015-02-11 09:00:00', 39, '2015-02-26 09:30:00', 39, 7, null, 15, null, '', 'Operadora', 'Onebox', 'all@onebox.es', '666666666', null, '2015-02-10 13:48:48', '2015-02-10 13:56:44', '2015-02-10 13:56:44', '2015-02-10 09:00:00', 39, '2015-02-10 09:00:00', 39, null, 123, null, 0, null, null, 31, 2, 2, 0, 0, null, 1, null, null, 0, null, null, null, null, null, 0, 0, 0, 1, 1, 2, 1, 4, 1, null, '2015-02-10 09:00:00', 39, '2015-02-10 09:00:00', 39, 26, 1, 0, 0, null, null, 0, null, null, null, 0, 2, null, null, 'E0', true, 0, 0, 0, 0, null, null, null, null, 1, null, null, 0, null, null, '2017-10-25 10:00:39', '2017-10-25 10:00:39', null, 0);

INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, create_date, update_date) VALUES (521, 107, 251, 'REAL MADRID CF - RCD ESPANYOL', null, '2018-09-22 18:45:00', '2018-09-22 19:15:00', '2018-09-13 10:00:00', '2018-09-13 10:00:00', null, 2, 1, null, 0, 1, 1, null, null, null, null, 0, null, null, null, 0, null, 15450, 3, 3, null, null, null, null, 1, null, null, true, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, true, false, 0, true, true, '2019-04-12 10:17:39', '2019-04-12 10:17:39');
INSERT INTO ob_cpanel.cpanel_entidad_recinto_config (idRelacionEntRecinto, idEntidad, idConfiguracion, create_date, update_date) VALUES (251, 524, 251, '2018-10-09 16:24:19', '2018-10-09 16:24:19');
INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion, idRecinto, nombreConfiguracion, estado, descripcion, tipoNumeracion, configuracionAsociada, tipo, aforo, esGrafica, creador, fechaAlta, fechaUltimaModificacion, publicada, idEvento, urlImagenDefecto, idExterno, fechaPropagacionDatosExterna, hayCambiosExternos, tipoPlantilla, estadoProcesoExterno, currentSequence, lastSequence, dirtyBI, espacioRecinto, thirdPartyIntegration, create_date, update_date) VALUES (251, 23, 'Real Madrid Liga 18-19', 1, null, null, 249, 3, null, 1, 1, '2018-10-09 16:24:19', '2018-10-09 16:24:19', 0, 107, null, 107, null, null, 2, null, null, null, true, 23, false, '2018-10-09 16:24:19', '2018-10-09 16:30:21');
INSERT INTO ob_cpanel.cpanel_recinto (idRecinto, idTipoRecinto, idEntidad, nombre, codigo, direccion, pais, provincia, municipio, codigoPostal, coordenada, pathLogo, descripcion, caracterirsticasTecnicasGrl, caracteristicasTecnicasTaquilla, caracteristicasTecnicasCtrlAcceso, aforoMaximo, nombreContacto, apellidosContacto, cargoContacto, telefonoContacto, correoContacto, controlAcceso, taquillaOnebox, estado, fechaAlta, empresaGestora, empresaPropietaria, multisala, url, publico, zonaHoraria, dirtyBI, idCalendario, timeZone, googlePlaceId, create_date, update_date) VALUES (23, 5, 524, 'Santiago Bernabeu', null, null, 1, 28, 'Madrid', null, null, null, null, null, null, null, 80000, 'a', 'a', 'a', '123', 'a@a.com', 'A', 0, 1, '2018-10-09', 'a', 'a', null, 'a', 1, null, false, null, 37, 'fsdfsdfsdf', '2018-10-09 13:56:00', '2018-10-09 13:56:12');


INSERT INTO ob_cpanel.cpanel_evento
(idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, entradaRegalo, elementosComPassbook, nominal, create_date, update_date) VALUES
(103, NULL, 'Deleted event', NULL, 1, NULL, NULL, NULL, NULL, 0, NULL, 28, NULL, '', 'Administrador', 'Operadora Onebox', 'all@onebox.es', '987654321', NULL, '2018-10-31 14:58:22.000', '2018-10-31 14:58:46.000', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, NULL, NULL, NULL, NULL, 4, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '2018-10-31 14:58:22.000', '2018-10-31 14:59:19.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, entradaRegalo, elementosComPassbook, nominal, create_date, update_date) VALUES
(103, NULL, 'Existing name', NULL, 1, NULL, NULL, NULL, NULL, 1, NULL, 28, NULL, '', 'Administrador', 'Operadora Onebox', 'all@onebox.es', '987654321', NULL, '2018-10-31 14:58:22.000', '2018-10-31 14:58:46.000', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, NULL, NULL, NULL, NULL, 4, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '2018-10-31 14:58:22.000', '2018-10-31 14:59:19.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, entradaRegalo, elementosComPassbook, nominal, create_date, update_date) VALUES
(103, NULL, 'Season Ticket', NULL, 5, NULL, NULL, NULL, NULL, 1, NULL, 28, NULL, '', 'Administrador', 'Operadora Onebox', 'all@onebox.es', '987654321', NULL, '2018-10-31 14:58:22.000', '2018-10-31 14:58:46.000', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, NULL, NULL, NULL, NULL, 4, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '2018-10-31 14:58:22.000', '2018-10-31 14:59:19.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, entradaRegalo, elementosComPassbook, nominal, create_date, update_date) VALUES
(103, NULL, 'Deleted Season Ticket', NULL, 5, NULL, NULL, NULL, NULL, 0, NULL, 28, NULL, '', 'Administrador', 'Operadora Onebox', 'all@onebox.es', '987654321', NULL, '2018-10-31 14:58:22.000', '2018-10-31 14:58:46.000', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, NULL, NULL, NULL, NULL, 4, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '2018-10-31 14:58:22.000', '2018-10-31 14:59:19.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, recomendarRecargosPromocionCanal, recargoPromocionMaximo, recargoPromocionMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, entradaRegalo, elementosComPassbook, nominal, create_date, update_date) VALUES
(50, 103, 1, 'Yet another season ticket', NULL, 5, NULL, NULL, NULL, NULL, 1, NULL, 15, 1, '', 'Administrador', 'Operadora Onebox', 'all@onebox.es', '987654321', NULL, '2018-10-31 14:58:22.000', '2018-10-31 14:58:46.000', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, NULL, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 1, 1, 2, 1, 4, 1, NULL, NULL, NULL, NULL, NULL, 96, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, '2018-10-31 14:58:22.000', '2018-10-31 14:59:19.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(51, 103, NULL, 'Still more season tickets', NULL, 5, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 4, 0, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(52, 103, NULL, 'Bad season ticket with 2 sessions', NULL, 5, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 4, 0, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(53, 103, NULL, 'Season ticket with 0 sessions', NULL, 5, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 4, 0, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_season_ticket
(idEvento, isMemberMandatory)
select idEvento, 0 from ob_cpanel.cpanel_evento ce
 where tipoEvento = 5;

INSERT INTO ob_cpanel.cpanel_entidad
(idEntidad, idOperadora, nombre, estado)
VALUES(5894, 1, 'Managed entity by entityAdmin', 1);

INSERT INTO ob_cpanel.cpanel_entidad
(idEntidad, idOperadora, nombre, estado)
VALUES(6707, 1, 'Entity Admin', 1);

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES(6707, 5894);


INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, tipoAbono, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(28439, 5894, NULL, 'Event Season ticket of entityAdmin', NULL, 5, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 4, 0, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, idRelacionEntidadRecinto, nombre, descripcion, fechaInicioSesion, fechaFinSesion, fechaVenta, fechaPublicacion, duracion, estado, estadoGeneracionAforo, estadoReplanchadoAforo, aforo, publicado, enVenta, motivoCancelacionVenta, motivoCancelacionPublicacion, migradoBiPromotor, migradoBiCanal, esAbono, color, fechaInicioReserva, fechaFinReserva, reservasActivas, estadoPurgado, idExterno, idImpuesto, idImpuestoRecargo, fechaNoDefinitiva, fechaModificacionExterna, espacioValidacionAcceso, fechaRealFinSesion, tipoHorarioAccesos, aperturaAccesos, cierreAccesos, dirtyBI, numMaxLocalidadesCompra, captcha, elementoComTicketTaquilla, elementoComTicket, mostrarHorario, vinculoConfigRecinto, tipoVenta, usaAccesosPlantilla, usaLimitesCuposPlantillaEvento, usarDatosFiscalesProductor, idPromotor, razonCancelacionPublicacion, idListaSubscripcion, isExternal, isPreview, hideSessionDates, showDate, showDatetime, create_date, update_date) VALUES (28439, 28439, 251, 'SEASON ADMIN', null, '2018-09-22 18:45:00', '2018-09-22 19:15:00', '2018-09-13 10:00:00', '2018-09-13 10:00:00', null, 2, 1, null, 0, 1, 1, null, null, null, null, 0, null, null, null, 0, null, 15450, 3, 3, null, null, null, null, 1, null, null, true, null, null, null, null, 1, false, 1, false, 0, null, null, null, null, true, false, 0, true, true, '2019-04-12 10:17:39', '2019-04-12 10:17:39');
