drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_zona_precios_config (
  idZona int(11) NOT NULL AUTO_INCREMENT,
  idConfiguracion int(11) NOT NULL,
  codigo varchar(50) NOT NULL,
  zonaReferencia int(11) DEFAULT NULL,
  descripcion varchar(200) DEFAULT NULL,
  color varchar(20) DEFAULT NULL,
  defecto tinyint(1) DEFAULT NULL,
  idExterno int(11) DEFAULT NULL,
  dirtyBI bit(1) NOT NULL DEFAULT '0',
  elementoComDescripcion int(11) DEFAULT NULL,
  create_date timestamp NOT NULL,
  update_date timestamp NOT NULL,
  restrictiveAccess tinyint(1) DEFAULT NULL,
  prioridad int(11) NOT NULL DEFAULT '0'
);

CREATE TABLE ob_cpanel.cpanel_config_recinto (
  idConfiguracion int(11) NOT NULL AUTO_INCREMENT,
  idRecinto int(11) NOT NULL,
  nombreConfiguracion varchar(50) NOT NULL,
  estado int(11) NOT NULL,
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
  dirtyBI bit(1) NOT NULL DEFAULT 0,
  espacioRecinto int(11) DEFAULT NULL,
  thirdPartyIntegration bit(1) NOT NULL DEFAULT '0',
  create_date timestamp NOT NULL,
  update_date timestamp NOT NULL
);

INSERT INTO ob_cpanel.cpanel_config_recinto (idConfiguracion,idRecinto,nombreConfiguracion,estado,descripcion,tipoNumeracion,configuracionAsociada,tipo,aforo,esGrafica,creador,fechaAlta,fechaUltimaModificacion,publicada,idEvento,urlImagenDefecto,idExterno,fechaPropagacionDatosExterna,hayCambiosExternos,tipoPlantilla,estadoProcesoExterno,currentSequence,lastSequence,dirtyBI,espacioRecinto,thirdPartyIntegration,create_date,update_date) VALUES
(2474,39,'Plantilla evento por defecto',0,NULL,NULL,422,3,946,0,1,'2013-11-20 17:20:10.000','2013-11-21 11:12:32.000',0,781,NULL,0,NULL,NULL,1,NULL,NULL,NULL,0,60,0,'2017-05-23 10:25:21.000','2017-05-23 10:25:21.000')
,(2477,39,'Abono Anual (2)',1,NULL,NULL,NULL,3,946,0,1,'2013-11-21 09:35:09.000',NULL,0,781,'31629_1385036384226.jpg',0,NULL,NULL,1,NULL,NULL,NULL,0,60,0,'2017-05-23 10:25:21.000','2017-05-23 10:25:21.000')
,(2478,39,'Evento por defecto',1,NULL,NULL,1563,3,946,1,1,'2013-11-21 10:03:35.000',NULL,0,781,NULL,0,NULL,NULL,1,NULL,NULL,NULL,0,60,0,'2017-05-23 10:25:21.000','2017-05-23 10:25:21.000')
,(2482,39,'Abono Anual',0,NULL,NULL,NULL,3,946,0,1,'2013-11-21 12:10:46.000','2013-11-21 12:11:37.000',0,781,NULL,0,NULL,NULL,1,NULL,NULL,NULL,0,60,0,'2017-05-23 10:25:21.000','2017-05-23 10:25:21.000')
,(2483,39,'Abono Anual',1,NULL,NULL,NULL,3,946,0,1,'2013-11-21 12:11:42.000',NULL,0,781,'722034_1385036367568.jpg',0,NULL,NULL,1,NULL,NULL,NULL,0,60,0,'2017-05-23 10:25:21.000','2017-05-23 10:25:21.000')
;

INSERT INTO ob_cpanel.cpanel_zona_precios_config (idConfiguracion,codigo,zonaReferencia,descripcion,color,defecto,idExterno,dirtyBI,elementoComDescripcion,create_date,update_date,restrictiveAccess,prioridad) VALUES
(2474,'ZP1',NULL,'Patio Butacas Premium','39168.0',1,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Palcos 1ª Planta',NULL,'Palcos 1ª Planta','39321.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Patio de Butacas',NULL,'Patio de Butacas','1.6576033E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'1º Planta F 1-4',NULL,'1º Planta F 1-4','8427230.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'1ª Planta F 5-9',NULL,'1ª Planta F 5-9','1.6751052E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Palcos Entreplanta',NULL,'Palcos Entreplanta','1.6737792E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Palcos Segunda Plant',NULL,'Palcos Segunda Planta','9225791.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'2º Planta F 3-8',NULL,'2º Planta F 3-8','1.675095E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Segunda Planta Filas',NULL,'Segunda Planta Filas 1-2','1.6764006E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2474,'Palcos Patio de Buta',NULL,'Palcos Patio de Butacas','1.03808E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
;
INSERT INTO ob_cpanel.cpanel_zona_precios_config (idConfiguracion,codigo,zonaReferencia,descripcion,color,defecto,idExterno,dirtyBI,elementoComDescripcion,create_date,update_date,restrictiveAccess,prioridad) VALUES
(2477,'ZP1',NULL,'Patio Butacas Premium','39168.0',1,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Palcos 1ª Planta',NULL,'Palcos 1ª Planta','39321.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Patio de Butacas',NULL,'Patio de Butacas','1.6576033E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'1º Planta F 1-4',NULL,'1º Planta F 1-4','8427230.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'1ª Planta F 5-9',NULL,'1ª Planta F 5-9','1.6751052E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Palcos Entreplanta',NULL,'Palcos Entreplanta','1.6737792E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Palcos Segunda Plant',NULL,'Palcos Segunda Planta','9225791.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'2º Planta F 3-8',NULL,'2º Planta F 3-8','1.675095E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Segunda Planta Filas',NULL,'Segunda Planta Filas 1-2','1.6764006E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2477,'Palcos Patio de Buta',NULL,'Palcos Patio de Butacas','1.03808E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
;
INSERT INTO ob_cpanel.cpanel_zona_precios_config (idConfiguracion,codigo,zonaReferencia,descripcion,color,defecto,idExterno,dirtyBI,elementoComDescripcion,create_date,update_date,restrictiveAccess,prioridad) VALUES
(2478,'Mesas Premium',NULL,'Patio Butacas Premium','13260.0',1,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Palcos 1ª Planta',NULL,'Palcos 1ª Planta','52377.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Patio Butacas',NULL,'Patio Butacas','1.6576033E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'1º Planta F 1-4',NULL,'1º Planta F 1-4','8427230.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'1ª Planta F 5-9',NULL,'1ª Planta F 5-9','1.6751052E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Palcos Entreplanta',NULL,'Palcos Entreplanta','1.6737792E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Palcos Segunda Plant',NULL,'Palcos Segunda Planta','9225791.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'2º Planta F 3-8',NULL,'2º Planta F 3-8','1.675095E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Segunda Planta Filas',NULL,'Segunda Planta Filas 1-2','1.6764006E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2478,'Palcos Patio de Buta',NULL,'Palcos Patio de Butacas','1.03808E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
;
INSERT INTO ob_cpanel.cpanel_zona_precios_config (idConfiguracion,codigo,zonaReferencia,descripcion,color,defecto,idExterno,dirtyBI,elementoComDescripcion,create_date,update_date,restrictiveAccess,prioridad) VALUES
(2482,'ZP1',NULL,'Patio Butacas Premium','39168.0',1,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Palcos 1ª Planta',NULL,'Palcos 1ª Planta','39321.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Patio de Butacas',NULL,'Patio de Butacas','1.6576033E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'1º Planta F 1-4',NULL,'1º Planta F 1-4','8427230.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'1ª Planta F 5-9',NULL,'1ª Planta F 5-9','1.6751052E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Palcos Entreplanta',NULL,'Palcos Entreplanta','1.6737792E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Palcos Segunda Plant',NULL,'Palcos Segunda Planta','9225791.0',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'2º Planta F 3-8',NULL,'2º Planta F 3-8','1.675095E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Segunda Planta Filas',NULL,'Segunda Planta Filas 1-2','1.6764006E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
,(2482,'Palcos Patio de Buta',NULL,'Palcos Patio de Butacas','1.03808E7',0,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
;
INSERT INTO ob_cpanel.cpanel_zona_precios_config (idConfiguracion,codigo,zonaReferencia,descripcion,color,defecto,idExterno,dirtyBI,elementoComDescripcion,create_date,update_date,restrictiveAccess,prioridad) VALUES
(2483,'Abono Anual',NULL,'Abono Anual','39168.0',1,NULL,0,NULL,'2017-05-25 09:09:00.000','2017-05-25 09:09:00.000',NULL,0)
;
