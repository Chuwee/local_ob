CREATE SCHEMA IF NOT EXISTS ob_cpanel;

USE ob_cpanel;

CREATE TABLE IF NOT EXISTS `cpanel_cupos_config` (
  `idCupo` int(11) NOT NULL AUTO_INCREMENT,
  `idConfiguracion` int(11) NOT NULL,
  `codigo` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `color` varchar(20) DEFAULT NULL,
  `defecto` tinyint(1) DEFAULT NULL,
  `capacidad` int(11) DEFAULT NULL,
  `create_date` timestamp NOT NULL,
  `update_date` timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS `cpanel_config_recinto` (
  `idConfiguracion` int(11) NOT NULL AUTO_INCREMENT,
  `idRecinto` int(11) NOT NULL,
  `nombreConfiguracion` varchar(50) NOT NULL,
  `estado` int(11) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `tipoNumeracion` int(11) DEFAULT '0',
  `configuracionAsociada` int(11) DEFAULT NULL,
  `tipo` int(11) NOT NULL,
  `aforo` int(11) DEFAULT NULL,
  `esGrafica` tinyint(1) NOT NULL DEFAULT '0' ,
  `creador` int(11) DEFAULT NULL,
  `fechaAlta` datetime NOT NULL,
  `fechaUltimaModificacion` datetime DEFAULT NULL,
  `publicada` tinyint(1) DEFAULT NULL,
  `idEvento` int(11) DEFAULT NULL,
  `urlImagenDefecto` varchar(500) DEFAULT NULL,
  `idExterno` int(11) DEFAULT NULL,
  `fechaPropagacionDatosExterna` datetime DEFAULT NULL,
  `hayCambiosExternos` tinyint(1) DEFAULT NULL,
  `tipoPlantilla` int(11) DEFAULT '1',
  `estadoProcesoExterno` int(11) DEFAULT '1',
  `currentSequence` bigint(20) DEFAULT NULL,
  `lastSequence` bigint(20) DEFAULT NULL,
  `dirtyBI` bit(1) NOT NULL DEFAULT '0',
  `espacioRecinto` int(11) DEFAULT NULL,
  `thirdPartyIntegration` bit(1) NOT NULL DEFAULT '0',
  `create_date` timestamp NOT NULL,
  `update_date` timestamp NOT NULL
);
-- Event id: 1
INSERT INTO `cpanel_config_recinto`
(`idConfiguracion`,`idRecinto`,`nombreConfiguracion`,`estado`,`descripcion`,`tipoNumeracion`,`configuracionAsociada`,`tipo`,`aforo`,`esGrafica`,`creador`,`fechaAlta`,`fechaUltimaModificacion`,`publicada`,`idEvento`,`urlImagenDefecto`,`idExterno`,`fechaPropagacionDatosExterna`,`hayCambiosExternos`,`tipoPlantilla`,`estadoProcesoExterno`,`currentSequence`,`lastSequence`,`dirtyBI`,`espacioRecinto`,`thirdPartyIntegration`,`create_date`,`update_date`)
VALUES (1,1,'Plantilla no gráfica',1,NULL,NULL,NULL,3,50,0,1,'2018-08-31 09:27:38',NULL,0,1,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1',1,'0','2018-08-31 09:27:38','2018-08-31 09:27:48');
-- Event id: 1
INSERT INTO `cpanel_config_recinto`
(`idConfiguracion`,`idRecinto`,`nombreConfiguracion`,`estado`,`descripcion`,`tipoNumeracion`,`configuracionAsociada`,`tipo`,`aforo`,`esGrafica`,`creador`,`fechaAlta`,`fechaUltimaModificacion`,`publicada`,`idEvento`,`urlImagenDefecto`,`idExterno`,`fechaPropagacionDatosExterna`,`hayCambiosExternos`,`tipoPlantilla`,`estadoProcesoExterno`,`currentSequence`,`lastSequence`,`dirtyBI`,`espacioRecinto`,`thirdPartyIntegration`,`create_date`,`update_date`)
VALUES (2,1,'Plantilla gráfica',1,NULL,NULL,NULL,3,25,1,1,'2018-08-31 09:39:49',NULL,0,1,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1',1,'0','2018-08-31 09:39:49','2020-01-30 09:23:06');
-- Event id: 1
INSERT INTO `cpanel_config_recinto`
(`idConfiguracion`,`idRecinto`,`nombreConfiguracion`,`estado`,`descripcion`,`tipoNumeracion`,`configuracionAsociada`,`tipo`,`aforo`,`esGrafica`,`creador`,`fechaAlta`,`fechaUltimaModificacion`,`publicada`,`idEvento`,`urlImagenDefecto`,`idExterno`,`fechaPropagacionDatosExterna`,`hayCambiosExternos`,`tipoPlantilla`,`estadoProcesoExterno`,`currentSequence`,`lastSequence`,`dirtyBI`,`espacioRecinto`,`thirdPartyIntegration`,`create_date`,`update_date`)
VALUES (3,2,'Feria Snapbox Gráfico',1,NULL,NULL,NULL,1,6618,1,1,'2018-10-09 10:40:13',NULL,0,1,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'0',2,'0','2018-10-09 10:40:13','2018-10-24 08:00:14');
-- Event id: 2
INSERT INTO `cpanel_config_recinto`
(`idConfiguracion`,`idRecinto`,`nombreConfiguracion`,`estado`,`descripcion`,`tipoNumeracion`,`configuracionAsociada`,`tipo`,`aforo`,`esGrafica`,`creador`,`fechaAlta`,`fechaUltimaModificacion`,`publicada`,`idEvento`,`urlImagenDefecto`,`idExterno`,`fechaPropagacionDatosExterna`,`hayCambiosExternos`,`tipoPlantilla`,`estadoProcesoExterno`,`currentSequence`,`lastSequence`,`dirtyBI`,`espacioRecinto`,`thirdPartyIntegration`,`create_date`,`update_date`)
VALUES (4,2,'ELIMINAR',0,NULL,NULL,NULL,1,320,0,1,'2018-10-10 09:47:51',NULL,0,2,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1',2,'0','2018-10-10 09:47:51','2018-11-15 15:09:04');
-- Event id: 2
INSERT INTO `cpanel_config_recinto`
(`idConfiguracion`,`idRecinto`,`nombreConfiguracion`,`estado`,`descripcion`,`tipoNumeracion`,`configuracionAsociada`,`tipo`,`aforo`,`esGrafica`,`creador`,`fechaAlta`,`fechaUltimaModificacion`,`publicada`,`idEvento`,`urlImagenDefecto`,`idExterno`,`fechaPropagacionDatosExterna`,`hayCambiosExternos`,`tipoPlantilla`,`estadoProcesoExterno`,`currentSequence`,`lastSequence`,`dirtyBI`,`espacioRecinto`,`thirdPartyIntegration`,`create_date`,`update_date`)
VALUES (5,2,'Feria Protractor Gráfico',1,NULL,NULL,3,2,6618,1,1,'2018-10-22 14:54:55',NULL,0,2,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,'1',2,'0','2018-10-22 14:54:55','2018-10-22 14:55:05');



INSERT INTO `cpanel_cupos_config`
(`idCupo`,`idConfiguracion`,`codigo`,`descripcion`,`color`,`defecto`,`capacidad`,`create_date`,`update_date`)
VALUES (1,1,'GV1','Grupo base','16777215',1,NULL,'2018-08-31 09:27:38','2018-08-31 09:27:38');

INSERT INTO `cpanel_cupos_config`
(`idCupo`,`idConfiguracion`,`codigo`,`descripcion`,`color`,`defecto`,`capacidad`,`create_date`,`update_date`)
VALUES (2,2,'GV1','Grupo base','16777215',1,NULL,'2018-08-31 09:39:49','2018-08-31 09:39:49');

INSERT INTO `cpanel_cupos_config`
(`idCupo`,`idConfiguracion`,`codigo`,`descripcion`,`color`,`defecto`,`capacidad`,`create_date`,`update_date`)
VALUES (3,3,'GV1','Grupo base','16777215',1,NULL,'2018-10-09 10:40:13','2018-10-09 10:40:13');

INSERT INTO `cpanel_cupos_config`
(`idCupo`,`idConfiguracion`,`codigo`,`descripcion`,`color`,`defecto`,`capacidad`,`create_date`,`update_date`)
VALUES (4,4,'GV1','Grupo base','16777215',1,NULL,'2018-10-10 09:47:52','2018-10-10 09:47:52');

INSERT INTO `cpanel_cupos_config`
(`idCupo`,`idConfiguracion`,`codigo`,`descripcion`,`color`,`defecto`,`capacidad`,`create_date`,`update_date`)
VALUES (5,5,'GV1','Grupo base','16777215',1,NULL,'2018-10-22 14:54:56','2018-10-22 14:54:56');


