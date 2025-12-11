CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_evento(
  idEvento int(11) NOT NULL AUTO_INCREMENT,
  idEntidad int(11) NOT NULL,
  idGira int(11) DEFAULT NULL,
  nombre varchar(50) NOT NULL,
  descripcion varchar(200) DEFAULT NULL,
  tipoEvento int(11) DEFAULT '1',
  fechaInicio datetime DEFAULT NULL,
  fechaInicioTZ int(11) DEFAULT NULL,
  fechaFin datetime DEFAULT NULL,
  fechaFinTZ int(11) DEFAULT NULL,
  estado int(11) NOT NULL,
  estadoPublicacion int(11) DEFAULT NULL,
  idTaxonomia int(11) NOT NULL,
  idTaxonomiaPropia int(11) DEFAULT NULL,
  referenciaPromotor varchar(50) DEFAULT NULL,
  nombreResponsable varchar(50) DEFAULT NULL,
  apellidosResponsable varchar(200) DEFAULT NULL,
  emailResponsable varchar(200) DEFAULT NULL,
  telefonoResponsable varchar(25) DEFAULT NULL,
  cargoResponsable varchar(150) DEFAULT NULL,
  fechaAlta datetime NOT NULL,
  fechaModificacion datetime DEFAULT NULL,
  fechaCambioEstado datetime DEFAULT NULL,
  fechaVenta datetime DEFAULT NULL,
  fechaVentaTZ int(11) DEFAULT NULL,
  fechaPublicacion datetime DEFAULT NULL,
  fechaPublicacionTZ int(11) DEFAULT NULL,
  destacado tinyint(1) DEFAULT NULL,
  aforo int(11) DEFAULT NULL,
  elementoComTicket int(11) DEFAULT NULL,
  recomendarRecargosCanal tinyint(1) DEFAULT NULL,
  recargoMaximo double DEFAULT NULL,
  recargoMinimo double DEFAULT NULL,
  idPlantillaTicket int(11) DEFAULT NULL,
  creadoPor int(11) DEFAULT NULL,
  modificadoPor int(11) DEFAULT NULL,
  objetivoSobreEntradas int(11) DEFAULT NULL,
  objetivoSobreVentas double DEFAULT NULL,
  idListaSubscripcion int(11) DEFAULT NULL,
  invitacionUsaPlantillaTicket tinyint(1) DEFAULT NULL,
  idPlantillaTicketInvitacion int(11) DEFAULT NULL,
  elementoComTicketInvitacion int(11) DEFAULT NULL,
  archivado tinyint(1) DEFAULT NULL,
  idPlantillaTicketTaquilla int(11) DEFAULT NULL,
  idPlantillaTicketTaquillaInvitacion int(11) DEFAULT NULL,
  elementoComTicketTaquilla int(11) DEFAULT NULL,
  elementoComTicketTaquillaInvitacion int(11) DEFAULT NULL,
  elementoComEmail int(11) DEFAULT NULL,
  permiteAbonos tinyint(1) DEFAULT NULL,
  permiteReservas tinyint(1) DEFAULT NULL,
  tipoCaducidadReserva tinyint(4) DEFAULT NULL,
  numUnidadesCaducidad int(11) DEFAULT NULL,
  tipoUnidadesCaducidad tinyint(4) DEFAULT NULL ,
  tipoFechaLimiteReserva tinyint(4) DEFAULT NULL,
  numUnidadesLimite int(11) DEFAULT NULL,
  tipoUnidadesLimite tinyint(4) DEFAULT NULL ,
  tipoLimite tinyint(4) DEFAULT NULL,
  fechaLimite datetime DEFAULT NULL,
  fechaInicioReserva datetime DEFAULT NULL,
  fechaInicioReservaTZ int(11) DEFAULT NULL,
  fechaFinReserva datetime DEFAULT NULL,
  fechaFinReservaTZ int(11) DEFAULT NULL,
  idPromotor int(11) DEFAULT NULL,
  usarDatosFiscalesProductor tinyint(1) DEFAULT NULL,
  usaElementosComGira tinyint(1) DEFAULT NULL,
  recomendarRecargosInvCanal tinyint(1) DEFAULT NULL,
  recargoInvMaximo double DEFAULT NULL,
  recargoInvMinimo double DEFAULT NULL,
  usaNombreSesion tinyint(4) DEFAULT NULL,
  permitirInformesRecinto tinyint(1) DEFAULT NULL,
  estadoPurgadoPdfs tinyint(4) DEFAULT NULL,
  idExterno int(11) DEFAULT NULL,
  idConfiguracionDefecto int(11) DEFAULT NULL,
  idCalificacionEdad varchar(2) NOT NULL DEFAULT 'E0',
  dirtyBI int(1) NOT NULL DEFAULT '0',
  numHorasCaducidadReserva int(11) DEFAULT NULL,
  numHorasLimiteReserva int(11) DEFAULT NULL,
  esSupraEvento tinyint(1) DEFAULT NULL,
  permiteGrupos tinyint(1) DEFAULT NULL,
  idPlantillaTicketGrupos int(11) DEFAULT NULL,
  idPlantillaTicketTaquillaGrupos int(11) DEFAULT NULL,
  idPlantillaTicketInvitacionGrupos int(11) DEFAULT NULL,
  idPlantillaTicketTaquillaInvitacionGrupos int(11) DEFAULT NULL,
  precioGrupos int(11) DEFAULT NULL,
  acompanyantesGrupoPagan tinyint(1) DEFAULT NULL,
  elementosComPassbook int(11) DEFAULT NULL,
  entradaRegalo tinyint(1) DEFAULT '0',
  recargoPromocionMaximo double DEFAULT NULL,
  recargoPromocionMinimo double DEFAULT NULL,
  recomendarRecargosPromocionCanal tinyint(1) DEFAULT NULL,
  nominal tinyint(1) NOT NULL DEFAULT '0',
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idEvento)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_sesion (
  idSesion int(11) NOT NULL AUTO_INCREMENT
);


CREATE TABLE IF not EXISTS ob_cpanel.cpanel_idioma (
                                                       idIdioma int(11) NOT NULL AUTO_INCREMENT,
                                                       codigo varchar(15) DEFAULT NULL,
                                                       descripcion varchar(200) DEFAULT NULL,
                                                       idiomaPlataforma tinyint(1) DEFAULT NULL,
                                                       PRIMARY KEY (idIdioma),
                                                       UNIQUE KEY codigo_UNIQUE (codigo)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_item_desc_sequence (
                                             idItem int(11) NOT NULL AUTO_INCREMENT,
                                             descripcion varchar(50) DEFAULT NULL,
                                             PRIMARY KEY (idItem)
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_desc_por_idioma (
                  idItem int(11) NOT NULL,
                  idIdioma int(11) NOT NULL,
                  descripcion longtext NOT NULL,
                  PRIMARY KEY (idItem,idIdioma),
                  KEY fk_item_desc (idItem),
                  KEY fk_item_desc_idioma (idIdioma),
                  CONSTRAINT fk_item_desc FOREIGN KEY (idItem) REFERENCES ob_cpanel.cpanel_item_desc_sequence (idItem) ON DELETE NO ACTION ON UPDATE NO ACTION,
                  CONSTRAINT fk_item_desc_idioma FOREIGN KEY (idIdioma) REFERENCES ob_cpanel.cpanel_idioma (idIdioma) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_tarifa (
  idTarifa int(11) NOT NULL AUTO_INCREMENT,
  idEvento int(11) DEFAULT NULL,
  nombre varchar(50) NOT NULL,
  descripcion varchar(200) DEFAULT NULL,
  defecto tinyint(1) DEFAULT NULL,
  accesoRestrictivo tinyint(1) DEFAULT NULL,
  elementoComDescripcion int(11) DEFAULT NULL,
  idGrupoTarifa int(11) DEFAULT NULL,
  externalRateTypeId int(11) DEFAULT NULL,
  position int(1) DEFAULT NULL,
  PRIMARY KEY (idTarifa),
  KEY fk_tarifa_e (idEvento),
  CONSTRAINT fk_tarifa_e FOREIGN KEY (idEvento) REFERENCES ob_cpanel.cpanel_evento (idEvento) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_tarifa_elemento_com_desc FOREIGN KEY (elementoComDescripcion) REFERENCES ob_cpanel.cpanel_item_desc_sequence (idItem) ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF not EXISTS ob_cpanel.cpanel_external_rate_type (
  id int(11) NOT NULL AUTO_INCREMENT,
  provider varchar(50) NOT NULL,
  code varchar(50) NOT NULL,
  name varchar(200) NOT NULL,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_sesion_tarifa (
  idSesion int(11) NOT NULL,
  idTarifa int(11) NOT NULL,
  defecto bit(1) NOT NULL,
  visibilidad bit(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (idSesion,idTarifa),
  KEY fk_tarifa_sesion_sesion (idSesion),
  KEY fk_tarifa_sesion_tarifa (idTarifa),
  CONSTRAINT fk_sesion_tarifa_sesion FOREIGN KEY (idSesion) REFERENCES ob_cpanel.cpanel_sesion (idSesion) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_sesion_tarifa_tarifa FOREIGN KEY (idTarifa) REFERENCES ob_cpanel.cpanel_tarifa (idTarifa) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IF not EXISTS ob_cpanel.cpanel_grupo_tarifa (
   idGrupoTarifa int(11) NOT NULL AUTO_INCREMENT,
   idEvento int(11) NOT NULL,
   nombre varchar(50) NOT NULL,
   elementoComDescripcion int(11) DEFAULT NULL,
   defecto tinyint(1) DEFAULT NULL,
   descripcionExterna varchar(50) DEFAULT NULL,
   create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (idGrupoTarifa),
   KEY fk_grupo_tarifa_elemento_com_desc (elementoComDescripcion),
   CONSTRAINT fk_grupo_tarifa_elemento_com_desc FOREIGN KEY (elementoComDescripcion) REFERENCES cpanel_item_desc_sequence (idItem) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO ob_cpanel.cpanel_grupo_tarifa (idGrupoTarifa, idEvento, nombre, elementoComDescripcion, defecto, descripcionExterna)
VALUES
(1, 1, 'grupoTarifa', null, 1, null);

INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, permiteAbonos, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(11, 103, NULL, 'A night at the Dorsia with Patrick Bateman', NULL, 1, '2016-06-30 20:00:00.000', 37, '2016-07-01 20:00:00.000', 37, 5, NULL, 15, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '5+965969', NULL, '2016-04-20 14:13:43.000', '2017-03-09 11:40:06.000', '2017-03-09 11:40:06.000', '2016-04-20 08:00:00.000', 37, '2016-04-20 08:00:00.000', 37, NULL, 0, 13, 0, NULL, NULL, 1, 2, 2, 0, 0, NULL, 1, 1, 11, 0, 6, 6, 14, 12, 22, 1, 1, 1, 1, 1, 1, 1, 4, 1, NULL, '2016-04-20 08:00:00.000', 37, '2016-07-01 20:00:00.000', 37, 96, 1, 0, 0, NULL, NULL, NULL, 1, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 4, 0, NULL, NULL, 0, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');
INSERT INTO ob_cpanel.cpanel_evento
(idEvento, idEntidad, idGira, nombre, descripcion, tipoEvento, fechaInicio, fechaInicioTZ, fechaFin, fechaFinTZ, estado, estadoPublicacion, idTaxonomia, idTaxonomiaPropia, referenciaPromotor, nombreResponsable, apellidosResponsable, emailResponsable, telefonoResponsable, cargoResponsable, fechaAlta, fechaModificacion, fechaCambioEstado, fechaVenta, fechaVentaTZ, fechaPublicacion, fechaPublicacionTZ, destacado, aforo, elementoComTicket, recomendarRecargosCanal, recargoMaximo, recargoMinimo, idPlantillaTicket, creadoPor, modificadoPor, objetivoSobreEntradas, objetivoSobreVentas, idListaSubscripcion, invitacionUsaPlantillaTicket, idPlantillaTicketInvitacion, elementoComTicketInvitacion, archivado, idPlantillaTicketTaquilla, idPlantillaTicketTaquillaInvitacion, elementoComTicketTaquilla, elementoComTicketTaquillaInvitacion, elementoComEmail, permiteAbonos, permiteReservas, tipoCaducidadReserva, numUnidadesCaducidad, tipoUnidadesCaducidad, tipoFechaLimiteReserva, numUnidadesLimite, tipoUnidadesLimite, tipoLimite, fechaLimite, fechaInicioReserva, fechaInicioReservaTZ, fechaFinReserva, fechaFinReservaTZ, idPromotor, usarDatosFiscalesProductor, usaElementosComGira, recomendarRecargosInvCanal, recargoInvMaximo, recargoInvMinimo, usaNombreSesion, permitirInformesRecinto, estadoPurgadoPdfs, idExterno, idConfiguracionDefecto, idCalificacionEdad, dirtyBI, numHorasCaducidadReserva, numHorasLimiteReserva, esSupraEvento, permiteGrupos, idPlantillaTicketGrupos, idPlantillaTicketTaquillaGrupos, idPlantillaTicketInvitacionGrupos, idPlantillaTicketTaquillaInvitacionGrupos, precioGrupos, acompanyantesGrupoPagan, elementosComPassbook, entradaRegalo, recargoPromocionMaximo, recargoPromocionMinimo, recomendarRecargosPromocionCanal, nominal, create_date, update_date)
VALUES(22, 399, NULL, 'Summer Festival Sound', NULL, 1, '2017-02-14 09:00:00.000', 1, '2026-08-20 08:30:00.000', 1, 5, NULL, 24, NULL, '', 'Mr Admin', 'Operadora', 'operadmin@onebox.es', '9000001', NULL, '2017-02-14 08:02:27.000', '2017-05-29 14:04:03.000', '2017-05-29 14:04:03.000', '2017-02-01 09:00:00.000', 37, '2017-02-01 09:00:00.000', 37, NULL, 200000, NULL, NULL, NULL, NULL, NULL, 2, 2, 0, 0, NULL, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 1, 1, 1, 1, 1, 1, 1, 4, 1, NULL, '2017-02-01 09:00:00.000', 37, '2020-02-15 09:00:00.000', 37, 461, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 'E0', 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, '2017-11-03 12:38:35.000', '2017-11-03 12:38:35.000');

INSERT INTO ob_cpanel.cpanel_idioma (idIdioma, codigo, descripcion, idiomaPlataforma)
VALUES
('1', 'es_ES', 'Castellano', '1'),
('2', 'ca_ES', 'Català', '1'),
('3', 'en_US', 'English', '1');

INSERT INTO ob_cpanel.cpanel_item_desc_sequence (idItem, descripcion)
VALUES
('1', 'unrequired1'),
('2', 'unrequired2');

INSERT INTO ob_cpanel.cpanel_desc_por_idioma (idItem, idIdioma, descripcion)
VALUES
('1', '1', 'General es'),
('1', '3', 'General en'),
('2', '1', 'Festivo es'),
('2', '2', 'Festiu en'),
('2', '3', 'Festive en');

INSERT INTO ob_cpanel.cpanel_tarifa
(idTarifa, idEvento, nombre, descripcion, defecto, accesoRestrictivo, elementoComDescripcion)
VALUES
       (1, 22, 'General', 'General', 1, 1, 1),
       (2, 22, 'Festivo', 'Festivo', 0, 1, 2),
       (3, 22, 'Vip', 'Vip', 0, 1, null),
       (4, 11, 'General', 'General', 1, 0, null);

INSERT INTO ob_cpanel.cpanel_sesion
(idSesion)
VALUES(33);
INSERT INTO ob_cpanel.cpanel_sesion
(idSesion)
VALUES(44);

INSERT INTO ob_cpanel.cpanel_sesion_tarifa
(idSesion, idTarifa, defecto)
VALUES(33, 1, 1);
INSERT INTO ob_cpanel.cpanel_sesion_tarifa
(idSesion, idTarifa, defecto)
VALUES(44, 2, 1);


