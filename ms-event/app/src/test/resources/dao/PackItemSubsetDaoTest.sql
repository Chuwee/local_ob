CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_pack (
                                                     idPack int NOT NULL AUTO_INCREMENT,
                                                     nombre varchar(50) NOT NULL,
    estado int NOT NULL DEFAULT '2' COMMENT '0:Borrado\n1:Activo\n2:Inactivo',
    tipo int DEFAULT NULL COMMENT '0: PROMOTOR\n1: CHANNEL',
    subtipo int NOT NULL DEFAULT '0' COMMENT ' 0: MANUAL\n1: AUTOMATICO ',
    idCanal int DEFAULT NULL,
    idEntidad int DEFAULT NULL,
    idPromocion int DEFAULT NULL,
    tipoRangoPack int DEFAULT '1' COMMENT '1:AUTOMATIC\n2:CUSTOM',
    fechaInicioPack timestamp NULL DEFAULT NULL,
    fechaFinPack timestamp NULL DEFAULT NULL,
    elementoComTicket int DEFAULT NULL,
    elementoComTicketTaquilla int DEFAULT NULL,
    idTipoPricing int DEFAULT '1',
    incremementoPrecio double DEFAULT '0' COMMENT '1: COMBINED, 2: INCREMENTAL, 3: NEW_PRICE',
    showMainVenue tinyint(1) NOT NULL DEFAULT '0',
    showMainDate tinyint(1) NOT NULL DEFAULT '0',
    showDateTime tinyint(1) NOT NULL DEFAULT '1',
    unifiedPrice tinyint(1) NOT NULL DEFAULT '1',
    showDate tinyint(1) NOT NULL DEFAULT '1',
    taxonomyId int DEFAULT NULL,
    taxId int DEFAULT NULL,
    customTaxonomyId int DEFAULT NULL,
    create_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (idPack)
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_pack_item (
                                                          idPackItem int NOT NULL AUTO_INCREMENT,
                                                          idPack int NOT NULL,
                                                          idItem int NOT NULL,
                                                          tipoItem int NOT NULL,
                                                          principal tinyint(1) DEFAULT '0',
    idConfiguracion int DEFAULT NULL,
    idZonaPrecio int DEFAULT NULL,
    zonaPrecioMapping tinyint(1) DEFAULT '0',
    zonaPrecioTipoSeleccion int DEFAULT NULL,
    idVariante int DEFAULT NULL,
    idPuntoEntrega int DEFAULT NULL,
    codigoDeBarrasCompartido tinyint(1) DEFAULT '0',
    mostrarItemEnChannels tinyint(1) DEFAULT '0',
    precioInformativo double DEFAULT NULL,
    PRIMARY KEY (idPackItem)
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_pack_item_subset (
                                                                 idPackItemSubitem INT NOT NULL AUTO_INCREMENT,
                                                                 idPackItem INT NOT NULL,
                                                                 idSubitem INT NOT NULL,
                                                                 type TINYINT(1) NOT NULL COMMENT '1:SESION',
    PRIMARY KEY(idPackItemSubitem)
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad (
                                                        idEntidad int(11) NOT NULL PRIMARY KEY,
    idOperadora int(11) NOT NULL,
    nombre varchar(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_taxonomia_base (
                                                               idTaxonomia int(11) NOT NULL AUTO_INCREMENT,
    idTaxonomiaSuperior int(11) DEFAULT NULL,
    codigo varchar(50) NOT NULL,
    descripcion varchar(300) DEFAULT NULL,
    orden int(11) DEFAULT NULL,
    tipo tinyint(4) NOT NULL DEFAULT '1' COMMENT '1: Taxonomia de eventos\n2: Taxonomia de productos no ticketing',
    PRIMARY KEY (idTaxonomia),
    UNIQUE KEY uk_taxonomia_codigo (codigo)
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_taxonomia_propia (
                                                                 idTaxonomia int(11) NOT NULL AUTO_INCREMENT,
    idTaxonomiaSuperior int(11) DEFAULT NULL,
    idEntidad int(11) NOT NULL,
    referencia varchar(50) DEFAULT NULL,
    descripcion varchar(300) DEFAULT NULL,
    orden int(11) DEFAULT NULL,
    tipo tinyint(4) NOT NULL DEFAULT '1' COMMENT '1: Taxonomia de eventos\n2: Taxonomia de productos no ticketing',
    PRIMARY KEY (idTaxonomia)
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

INSERT INTO ob_cpanel.cpanel_entidad (idEntidad, idOperadora, nombre)
VALUES (1, 1, 'Test Entity');

INSERT INTO ob_cpanel.cpanel_impuesto (idImpuesto, idOperadora, nombre, valor)
VALUES (1, 1, 'Test Tax', 0.0);

INSERT INTO ob_cpanel.cpanel_pack (idPack, nombre, estado, tipo, subtipo, idEntidad)
VALUES 
    (1, 'Test Pack', 1, 0, 1, 1),
    (2, 'Test Pack', 2, 0, 1, 1);

INSERT INTO ob_cpanel.cpanel_pack_item (idPackItem, idPack, idItem, tipoItem, principal, zonaPrecioTipoSeleccion, idConfiguracion)
VALUES 
    (10, 1, 1, 1, 1, NULL, NULL),
    (20, 1, 1, 3, 0, NULL, 10),
    (200, 2, 1, 1, 0, NULL, NULL),
    (201, 1, 1, 2, 0, 1, NULL),
    (202, 1, 1, 1, 0, 2, NULL),
    (203, 1, 1, 1, 0, 0, NULL),
    (204, 1, 1, 1, 0, 1, 10),
    (205, 1, 1, 1, 0, NULL, 10);

INSERT INTO ob_cpanel.cpanel_pack_item_subset (idPackItem, idSubitem, type)
VALUES (10, 100, 1), (10, 101, 1), (10, 102, 1),
       (20, 100, 1), (20, 101, 1), (20, 102, 1);