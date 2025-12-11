CREATE SCHEMA IF NOT EXISTS ob_cpanel;

-- -----------------------------------------------------
-- Table ob_cpanel.cpanel_plantilla_ticket
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad (
  idEntidad int(11) NOT NULL PRIMARY KEY,
  idOperadora int(11) NOT NULL,
  nombre varchar(50) NOT NULL,
  estado int(11) NOT NULL
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_modelo_ticket (
  `idModelo` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `tipoImpresora` varchar(50) DEFAULT NULL,
  `tipoHoja` varchar(50) DEFAULT NULL,
  `orientacion` varchar(50) DEFAULT NULL,
  `aspectoEvento` varchar(200) DEFAULT NULL,
  `modeloJasper` varchar(100) DEFAULT NULL,
  `formato` tinyint(4) NOT NULL COMMENT '1:Impresion estandar\n2:Impresion ticket',
  `aspectoEventoCanal` varchar(200) DEFAULT NULL,
  `aspectoPlantillaTicket` varchar(200) DEFAULT NULL,
  `tipoModelo` int(8) DEFAULT NULL COMMENT '1: PDF (Jasper)\n2: Ticket ZPL General\n3: Ticket ZPL IVA\n4: Ticket ZPL Zona de Precio\n5: Ticket ZPL Wonderland\n8:Ticket ZPL Zona de Precio sin Canal',
  `widthBanner` int(11) DEFAULT NULL,
  `heightBanner` int(11) DEFAULT NULL,
  `verticalZpl` tinyint(1) NOT NULL DEFAULT '0',
  `gradosRotacionBanner` int(11) NOT NULL DEFAULT '270',
  PRIMARY KEY (`idModelo`)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_elementos_com_ticket (
  `idInstancia` int(11) NOT NULL AUTO_INCREMENT,
  `subtitulo1` int(11) DEFAULT NULL,
  `subtitulo2` int(11) DEFAULT NULL,
  `otrosDatos` int(11) DEFAULT NULL,
  `terminos` int(11) DEFAULT NULL,
  `pathImagenCabecera` int(11) DEFAULT NULL,
  `pathImagenLogo` int(11) DEFAULT NULL,
  `pathImagenCuerpo` int(11) DEFAULT NULL,
  `pathImagenBanner1` int(11) DEFAULT NULL,
  `pathImagenBanner2` int(11) DEFAULT NULL,
  `pathImagenBanner3` int(11) DEFAULT NULL,
  PRIMARY KEY (`idInstancia`)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_admin_entidades (
    idEntidadAdmin int(11) NOT NULL,
    idEntidad int(11) NOT NULL,
    PRIMARY KEY (idEntidadAdmin,idEntidad),
    KEY fk_entidad_entidades (idEntidad),
    CONSTRAINT fk_entidad_admin_entidades FOREIGN KEY (idEntidadAdmin) REFERENCES cpanel_entidad (idEntidad),
    CONSTRAINT fk_entidad_entidades FOREIGN KEY (idEntidad) REFERENCES cpanel_entidad (idEntidad)
);


CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_plantilla_ticket (
  `idPlantilla` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `idEntidad` int(11) NOT NULL,
  `idModelo` int(11) DEFAULT NULL,
  `asignacionAutomatica` tinyint(1) DEFAULT NULL,
  `estado` int(11) DEFAULT NULL,
  `elementoComTicket` int(11) DEFAULT NULL,
  `excludeBarcode` tinyint(1) DEFAULT '0',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idPlantilla`),
  KEY `fk_ticket_modelo` (`idModelo`),
  KEY `fk_ticket_elementosCom` (`elementoComTicket`),
  KEY `fk_ticket_entidad` (`idEntidad`),
  CONSTRAINT `fk_ticket_elementosCom` FOREIGN KEY (`elementoComTicket`) REFERENCES `cpanel_elementos_com_ticket` (`idInstancia`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ticket_entidad` FOREIGN KEY (`idEntidad`) REFERENCES `cpanel_entidad` (`idEntidad`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ticket_modelo` FOREIGN KEY (`idModelo`) REFERENCES `cpanel_modelo_ticket` (`idModelo`) ON DELETE NO ACTION ON UPDATE NO ACTION
);


INSERT INTO ob_cpanel.cpanel_modelo_ticket
(nombre, descripcion, tipoImpresora, tipoHoja, orientacion, aspectoEvento, modeloJasper, formato, aspectoEventoCanal, aspectoPlantillaTicket, tipoModelo, widthBanner, heightBanner, verticalZpl, gradosRotacionBanner)
VALUES('', '', '', '', '', '', '', 0, '', '', 0, 0, 0, 0, 270);

INSERT INTO ob_cpanel.cpanel_elementos_com_ticket
(subtitulo1, subtitulo2, otrosDatos, terminos, pathImagenCabecera, pathImagenLogo, pathImagenCuerpo, pathImagenBanner1, pathImagenBanner2, pathImagenBanner3)
VALUES(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);


INSERT INTO ob_cpanel.cpanel_entidad
(idEntidad, idOperadora, nombre, estado) VALUES
(103, 1, 'Entidad 1', 1),
(6707, 1, 'entityadmin', 1),
(5894, 1, 'Entidad Admin managed', 1),
(324, 1, 'Entidad Admin managed 2', 1);

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES(6707, 5894);

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES(6707, 324);

INSERT INTO ob_cpanel.cpanel_plantilla_ticket
(nombre, idEntidad, idModelo, asignacionAutomatica, estado, elementoComTicket, excludeBarcode, create_date, update_date)
VALUES('', 5894, 1, 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO ob_cpanel.cpanel_plantilla_ticket
(nombre, idEntidad, idModelo, asignacionAutomatica, estado, elementoComTicket, excludeBarcode, create_date, update_date)
VALUES('', 324, 1, 1,1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
