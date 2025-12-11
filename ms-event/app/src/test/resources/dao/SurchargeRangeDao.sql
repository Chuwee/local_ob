/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  ignasi
 * Created: 16-abr-2019
 */
CREATE SCHEMA IF NOT EXISTS ob_cpanel;
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango (
  `idRango` int(11) NOT NULL AUTO_INCREMENT,
  `nombreRango` varchar(50) NOT NULL,
  `rangoMaximo` double DEFAULT NULL,
  `rangoMinimo` double DEFAULT NULL,
  `valor` double DEFAULT NULL,
  `porcentaje` double DEFAULT NULL,
  `valorMaximo` double DEFAULT NULL,
  `valorMinimo` double DEFAULT NULL,
  `idCurrency` int(10),
  PRIMARY KEY (`idRango`)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento (
  `idEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEvento`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_promocion (
  `idEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEvento`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_inv (
  `idEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEvento`,`idRango`)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal (
  `idCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanal`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal_promocion (
  `idCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanal`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal_inv (
  `idCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanal`,`idRango`)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal_evento (
  `idCanalEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanalEvento`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal_evento_promocion (
  `idCanalEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanalEvento`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_canal_evento_inv (
  `idCanalEvento` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idCanalEvento`,`idRango`)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_canal (
  `idEventoCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEventoCanal`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_canal_promocion (
  `idEventoCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEventoCanal`,`idRango`)
);
CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_canal_inv (
  `idEventoCanal` int(11) NOT NULL,
  `idRango` int(11) NOT NULL,
  PRIMARY KEY (`idEventoCanal`,`idRango`)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_rango_recargo_evento_cambio_localidad (
    `idEvento` int(11) NOT NULL,
    `idRango` int(11) NOT NULL,
    PRIMARY KEY (`idEvento`,`idRango`)
    );

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento VALUES (1,1);
INSERT INTO ob_cpanel.cpanel_rango_recargo_evento VALUES (2,2);
INSERT INTO ob_cpanel.cpanel_rango_recargo_evento VALUES (2,3);
INSERT INTO ob_cpanel.cpanel_rango_recargo_evento VALUES (2,4);

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento_promocion VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento_inv VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal VALUES (1,1);
INSERT INTO ob_cpanel.cpanel_rango_recargo_canal VALUES (2,2);
INSERT INTO ob_cpanel.cpanel_rango_recargo_canal VALUES (2,3);
INSERT INTO ob_cpanel.cpanel_rango_recargo_canal VALUES (2,4);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal_promocion VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal_inv VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento_canal VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento_canal_promocion VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_evento_canal_inv VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal_evento VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal_evento_promocion VALUES (1,1);

INSERT INTO ob_cpanel.cpanel_rango_recargo_canal_evento_inv VALUES (1,1);


-- cpanel rango dummy data: We use the same key for previous tables
INSERT INTO ob_cpanel.cpanel_rango
(idRango,nombreRango,rangoMaximo,rangoMinimo,valor,porcentaje,valorMaximo,valorMinimo)
VALUES
(1,'papiol',0,0,1,10, null, null);

INSERT INTO ob_cpanel.cpanel_rango
(idRango,nombreRango,rangoMaximo,rangoMinimo,valor,porcentaje,valorMaximo,valorMinimo)
VALUES
(2,'papiol',0,2,1,10, null, null);
INSERT INTO ob_cpanel.cpanel_rango
(idRango,nombreRango,rangoMaximo,rangoMinimo,valor,porcentaje,valorMaximo,valorMinimo)
VALUES
(3,'papiol',2,1,1,10, null, null);
INSERT INTO ob_cpanel.cpanel_rango
(idRango,nombreRango,rangoMaximo,rangoMinimo,valor,porcentaje,valorMaximo,valorMinimo)
VALUES
(4,'papiol',1,0,1,10, null, null);


