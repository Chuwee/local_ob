CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_item_desc_sequence (
  idItem int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(50) DEFAULT NULL,
  PRIMARY KEY (idItem)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_idioma (
  idIdioma int(11) NOT NULL AUTO_INCREMENT,
  codigo varchar(15) DEFAULT NULL,
  descripcion varchar(200) DEFAULT NULL,
  idiomaPlataforma tinyint(1) DEFAULT NULL,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idIdioma),
  UNIQUE KEY codigo_UNIQUE (codigo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_desc_por_idioma (
  idItem int(11) NOT NULL,
  idIdioma int(11) NOT NULL,
  descripcion longtext NOT NULL,
  PRIMARY KEY (idItem,idIdioma),
  KEY fk_item_desc (idItem),
  KEY fk_item_desc_idioma (idIdioma),
  CONSTRAINT fk_item_desc FOREIGN KEY (idItem) REFERENCES ob_cpanel.cpanel_item_desc_sequence (idItem),
  CONSTRAINT fk_item_desc_idioma FOREIGN KEY (idIdioma) REFERENCES ob_cpanel.cpanel_idioma (idIdioma)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_tipo_colectivo (
  idTipoColectivo int(11) NOT NULL,
  nombre varchar(200) NOT NULL,
  PRIMARY KEY (idTipoColectivo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_subtipo_colectivo (
  idSubtipoColectivo int(11) NOT NULL AUTO_INCREMENT,
  idTipoColectivo int(11) DEFAULT NULL,
  nombre varchar(50) DEFAULT NULL,
  PRIMARY KEY (idSubtipoColectivo),
  KEY fk_subtipo_tipo_colectivo (idTipoColectivo),
  CONSTRAINT fk_subtipo_tipo_colectivo FOREIGN KEY (idTipoColectivo) REFERENCES ob_cpanel.cpanel_tipo_colectivo (idTipoColectivo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_colectivo (
  idColectivo int(11) NOT NULL AUTO_INCREMENT,
  idTipoColectivo int(11) NOT NULL,
  idSubtipoColectivo int(11) DEFAULT NULL,
  nombre varchar(50) NOT NULL,
  descripcion varchar(250) DEFAULT NULL,
  estado int(11) DEFAULT NULL,
  fechaAlta date NOT NULL,
  fechaModificacion datetime DEFAULT NULL,
  idEntidad int(11) DEFAULT NULL,
  descripcionLarga varchar(2000) DEFAULT NULL,
  usaAutenticacionFacebook tinyint(1) DEFAULT NULL,
  idAplicacionFacebook varchar(50) DEFAULT NULL,
  dirtyBI bit(1) NOT NULL DEFAULT 0,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idColectivo),
  KEY fk_colectivo_tipo_colectivo (idTipoColectivo),
  KEY fk_colectivo_subtipo_colectivo (idSubtipoColectivo),
  KEY idx_dirtybi_colectivo (dirtyBI),
  CONSTRAINT fk_colectivo_subtipo_colectivo FOREIGN KEY (idSubtipoColectivo) REFERENCES ob_cpanel.cpanel_subtipo_colectivo (idSubtipoColectivo),
  CONSTRAINT fk_colectivo_tipo_colectivo FOREIGN KEY (idTipoColectivo) REFERENCES ob_cpanel.cpanel_tipo_colectivo (idTipoColectivo)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_plantilla_promocion (
  idPlantillaPromocion int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(50) NOT NULL,
  nombreDescriptivo int(11) DEFAULT NULL,
  descripcion int(11) DEFAULT NULL,
  estado int(11) DEFAULT NULL,
  activada tinyint(1) DEFAULT NULL,
  subtipo int(11) DEFAULT NULL,
  tipoClientePromocion int(11) DEFAULT NULL,
  idColectivo int(11) DEFAULT NULL,
  aplicacionDescuento int(11) DEFAULT NULL,
  mostrarEnTicket tinyint(1) DEFAULT NULL,
  mostrarEnResumen tinyint(1) DEFAULT NULL,
  tipoPeriodoValidez int(11) DEFAULT NULL,
  periodoDesde datetime DEFAULT NULL,
  periodoHasta datetime DEFAULT NULL,
  diasDesde int(11) DEFAULT NULL,
  diasHasta int(11) DEFAULT NULL,
  usaLimitePackEntradas tinyint(1) DEFAULT NULL,
  limitePackEntradas int(11) DEFAULT NULL,
  usaLimiteMinEntradas tinyint(1) DEFAULT NULL,
  limiteMinEntradas int(11) DEFAULT NULL,
  usaLimiteOperacion tinyint(1) DEFAULT NULL,
  limiteOperacion int(11) DEFAULT NULL,
  usaLimiteEvento tinyint(1) DEFAULT NULL,
  limiteEvento int(11) DEFAULT NULL,
  tipoIncentivo int(11) DEFAULT NULL,
  tipoDescuento int(11) DEFAULT NULL,
  valorDescuentoFijo double DEFAULT NULL,
  valorDescuentoPorcentual double DEFAULT NULL,
  limiteSesion int(11) DEFAULT NULL,
  usaLimiteSesion tinyint(1) DEFAULT NULL,
  esPromocionPrivada tinyint(1) DEFAULT NULL,
  esNoGestionable tinyint(1) DEFAULT NULL,
  validacionColectivoOpcional tinyint(1) DEFAULT NULL,
  dirtyBI bit(1) NOT NULL DEFAULT 0,
  noDescontarImporteEnResumen tinyint(1) DEFAULT NULL,
  aplicarCostesRecargosCanalEspecificos tinyint(1) DEFAULT NULL,
  aplicarCostesRecargosPromotorEspecificos tinyint(1) NOT NULL DEFAULT 0,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  accesoRestrictivo tinyint(1) DEFAULT NULL,
  noAcumulable tinyint(1) NOT NULL DEFAULT 0,
  usesEventUserCollectiveLimit tinyint(1) DEFAULT NULL,
  usesSessionUserCollectiveLimit tinyint(1) DEFAULT NULL,
  eventUserCollectiveLimit int(11) DEFAULT NULL,
  sessionUserCollectiveLimit int(11) DEFAULT NULL,
  blockSecondaryMarketSale tinyint(1) DEFAULT NULL,
  PRIMARY KEY (idPlantillaPromocion),
  KEY fk_plantilla_promocion_desc_secuence (descripcion),
  KEY fk_plantilla_promocion_desc_secuence2 (nombreDescriptivo),
  KEY fk_plantilla_promocion_colectivo (idColectivo),
  KEY idx_dirtybi (dirtyBI),
  CONSTRAINT fk_plantilla_promocion_colectivo FOREIGN KEY (idColectivo) REFERENCES ob_cpanel.cpanel_colectivo (idColectivo),
  CONSTRAINT fk_plantilla_promocion_desc_secuence FOREIGN KEY (descripcion) REFERENCES ob_cpanel.cpanel_item_desc_sequence (idItem),
  CONSTRAINT fk_plantilla_promocion_desc_secuence2 FOREIGN KEY (nombreDescriptivo) REFERENCES ob_cpanel.cpanel_item_desc_sequence (idItem)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_rango (
  idRango int(11) NOT NULL AUTO_INCREMENT,
  nombreRango varchar(50) NOT NULL,
  rangoMaximo double DEFAULT NULL,
  rangoMinimo double DEFAULT NULL,
  valor double DEFAULT NULL,
  porcentaje double DEFAULT NULL,
  valorMaximo double DEFAULT NULL,
  valorMinimo double DEFAULT NULL,
  PRIMARY KEY (idRango)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_rango_plantilla_promocion (
  idPlantillaPromocion int(11) NOT NULL,
  idRango int(11) NOT NULL,
  PRIMARY KEY (idPlantillaPromocion,idRango),
  KEY fk_rango_plantilla_promocion_plantilla (idPlantillaPromocion),
  KEY fk_rango_plantilla_promocion_rango (idRango),
  CONSTRAINT fk_rango_plantilla_promocion_plantilla FOREIGN KEY (idPlantillaPromocion) REFERENCES ob_cpanel.cpanel_plantilla_promocion (idPlantillaPromocion),
  CONSTRAINT fk_rango_plantilla_promocion_rango FOREIGN KEY (idRango) REFERENCES ob_cpanel.cpanel_rango (idRango)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_plantilla_promocion_evento (
  idPromocionEvento int(11) NOT NULL AUTO_INCREMENT,
  idPlantillaPromocion int(11) NOT NULL,
  idEvento int(11) NOT NULL,
  tipoCanalCompra int(11) DEFAULT NULL,
  tipoSesiones int(11) DEFAULT NULL,
  tipoLocalidades int(11) DEFAULT NULL,
  usaSeleccionTarifas int(11),
  usaPacksEntidad tinyint(1) DEFAULT '0',
  PRIMARY KEY (idPromocionEvento),
  KEY fk_promocion_evento_plantilla (idPlantillaPromocion),
  CONSTRAINT fk_promocion_evento_plantilla FOREIGN KEY (idPlantillaPromocion) REFERENCES ob_cpanel.cpanel_plantilla_promocion (idPlantillaPromocion)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promocion_evento_sesion (
  idPromocionEvento int(11) NOT NULL,
  idSesion int(11) NOT NULL,
  PRIMARY KEY (idPromocionEvento,idSesion),
  KEY fk_promocion_evento_sesion_promo (idPromocionEvento),
  CONSTRAINT fk_promocion_evento_sesion_promo FOREIGN KEY (idPromocionEvento) REFERENCES ob_cpanel.cpanel_plantilla_promocion_evento (idPromocionEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promocion_evento_canal (
  idPromocionEvento int(11) NOT NULL,
  idCanal int(11) NOT NULL,
  PRIMARY KEY (idPromocionEvento,idCanal),
  KEY fk_promocion_evento_canal_promo (idPromocionEvento),
  CONSTRAINT fk_promocion_evento_canal_promo FOREIGN KEY (idPromocionEvento) REFERENCES ob_cpanel.cpanel_plantilla_promocion_evento (idPromocionEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promocion_evento_tarifa (
  idPromocionEvento int(11) NOT NULL,
  idTarifa int(11) NOT NULL,
  PRIMARY KEY (idPromocionEvento,idTarifa),
  KEY fk_promcion_evento_dia_promocion (idPromocionEvento),
  CONSTRAINT fk_promcion_evento_tarifa_promocion FOREIGN KEY (idPromocionEvento) REFERENCES ob_cpanel.cpanel_plantilla_promocion_evento (idPromocionEvento)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promocion_evento_zona_precio (
  idPromocionEvento int(11) NOT NULL,
  idZona int(11) NOT NULL,
  PRIMARY KEY (idPromocionEvento,idZona),
  KEY fk_promocion_evento_zona_promo (idPromocionEvento),
  CONSTRAINT fk_promocion_evento_zona_promo FOREIGN KEY (idPromocionEvento) REFERENCES ob_cpanel.cpanel_plantilla_promocion_evento (idPromocionEvento)
);



INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(381, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(386, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(388, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(390, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(392, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(394, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(396, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(421, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(430, NULL);


INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(377, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(382, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(387, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(389, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(391, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(393, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(395, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(397, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(422, NULL);
INSERT INTO ob_cpanel.cpanel_item_desc_sequence
(idItem, descripcion)
VALUES(423, NULL);


INSERT INTO ob_cpanel.cpanel_idioma
(idIdioma, codigo, descripcion, idiomaPlataforma, create_date, update_date)
VALUES(1, 'es_ES', 'Castellano', 1, '2017-11-03 12:38:34.000', '2017-11-03 12:38:34.000');
INSERT INTO ob_cpanel.cpanel_idioma
(idIdioma, codigo, descripcion, idiomaPlataforma, create_date, update_date)
VALUES(2, 'ca_ES', 'Català', 1, '2017-11-03 12:38:34.000', '2017-11-03 12:38:34.000');
INSERT INTO ob_cpanel.cpanel_idioma
(idIdioma, codigo, descripcion, idiomaPlataforma, create_date, update_date)
VALUES(3, 'en_US', 'English', 1, '2017-11-03 12:38:34.000', '2017-11-03 12:38:34.000');


INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(381, 1, 'Descuento 3€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(381, 2, 'Descompte 3€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(381, 3, 'Descuento EN');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(386, 1, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(386, 2, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(386, 3, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(388, 1, '(Desc. ESP) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(388, 2, '(Desc. CAT) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(388, 3, '(Desc. ENG) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(390, 1, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(390, 2, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(390, 3, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(392, 1, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(392, 2, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(392, 3, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(394, 1, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(394, 2, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(394, 3, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(396, 1, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(396, 2, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(396, 3, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(421, 1, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(421, 2, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(421, 3, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(430, 1, '');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(430, 2, '');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(430, 3, '');


INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(377, 1, 'Early Bird');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(377, 2, 'Early Bird');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(377, 3, 'Early Bird');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(382, 1, 'Descuento 3€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(382, 2, 'Descompte 3€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(382, 3, 'Descuento EN');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(387, 1, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(387, 2, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(387, 3, 'Promo Código 4€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(389, 1, '(DescName. ESP) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(389, 2, '(DescName. CAT) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(389, 3, '(DescName. ENG) Auto -1€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(391, 1, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(391, 2, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(391, 3, 'Desc Código Promocional  10%');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(393, 1, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(393, 2, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(393, 3, 'Promo -2€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(395, 1, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(395, 2, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(395, 3, 'Promo NPB 10€');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(397, 1, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(397, 2, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(397, 3, 'Desc NPB 5€ Códido promocional');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(422, 1, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(422, 2, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(422, 3, 'Promo Neg +10');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(423, 1, 'Promo sin descripcion');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(423, 2, 'Promo sense descripcio');
INSERT INTO ob_cpanel.cpanel_desc_por_idioma
(idItem, idIdioma, descripcion)
VALUES(423, 3, 'Promo without description');


INSERT INTO ob_cpanel.cpanel_tipo_colectivo
(idTipoColectivo, nombre)
VALUES(4, 'Validación (interna)');


INSERT INTO ob_cpanel.cpanel_subtipo_colectivo
(idSubtipoColectivo, idTipoColectivo, nombre)
VALUES(1, 4, 'Codigo Promocional');


INSERT INTO ob_cpanel.cpanel_colectivo
(idColectivo, idTipoColectivo, idSubtipoColectivo, nombre, descripcion, estado, fechaAlta, fechaModificacion, idEntidad, descripcionLarga, usaAutenticacionFacebook, idAplicacionFacebook, dirtyBI, create_date, update_date)
VALUES(53, 4, 1, 'Colectivo Ddorado', NULL, 1, '2019-03-07', NULL, 412, NULL, NULL, NULL, 1, '2019-03-07 10:43:16.000', '2019-03-07 10:43:16.000');


INSERT INTO ob_cpanel.cpanel_rango
(idRango, nombreRango, rangoMaximo, rangoMinimo, valor, porcentaje, valorMaximo, valorMinimo)
VALUES(898, '0.0-0.0', 0, 0, 5, NULL, NULL, NULL);
INSERT INTO ob_cpanel.cpanel_rango
(idRango, nombreRango, rangoMaximo, rangoMinimo, valor, porcentaje, valorMaximo, valorMinimo)
VALUES(899, '5.0-10.0', 10.5, 5, 25, NULL, NULL, NULL);


INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(58, 'Early Bird', 377, 430, 1, 1, 3, 0, NULL, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, 25, NULL, NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2018-11-12 11:13:25.000', '2019-04-15 13:32:02.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(59, 'Descuento 3€', 382, 381, 1, 1, 3, 0, NULL, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, 3, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0, '2018-11-30 11:40:50.000', '2019-03-15 08:00:57.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(60, 'Promo Código 4€', 387, 386, 1, 1, 2, 1, 53, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, 4, NULL, NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2019-03-05 10:38:49.000', '2019-03-15 08:02:35.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(61, 'Auto -1€', 389, 388, 1, 1, 1, 0, 53, 0, 0, 0, 0, '2012-05-31 22:00:00.000', '2012-07-31 22:00:00.000', NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, 1, 10.0, NULL, 0, 0, 1, 0, 0, 0, 0, 0, '2019-03-07 10:45:41.000', '2019-03-28 09:41:21.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(62, 'Desc Código Promocional  10%', 391, 390, 1, 1, 3, 1, 53, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 1, NULL, 10, NULL, 0, 0, 0, 0, 0, 0, 0, 0, '2019-03-07 10:48:14.000', '2019-03-07 10:56:05.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(63, 'Promo  -2€', 393, 392, 1, 1, 2, 0, NULL, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, 2, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0, '2019-03-07 10:58:12.000', '2019-03-15 08:00:15.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(64, 'Promo NPB 10€', 395, 394, 0, 0, 2, NULL, NULL, 0, 1, 1, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 1, NULL, NULL, 0, '2019-03-07 11:00:21.000', '2019-03-07 11:01:11.000', NULL, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(65, 'Desc NPB 10€ Códido promocional', 397, 396, 1, 1, 3, 1, 53, 0, 0, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 2, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0, '2019-03-07 11:01:32.000', '2019-03-15 07:48:39.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(72, 'Promo Neg +10', 422, 421, 1, 1, 3, 1, 53, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, -10, NULL, NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2019-03-15 08:03:44.000', '2019-03-15 08:05:17.000', 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion
(idPlantillaPromocion, nombre, nombreDescriptivo, descripcion, estado, activada, subtipo, tipoClientePromocion, idColectivo, aplicacionDescuento, mostrarEnTicket, mostrarEnResumen, tipoPeriodoValidez, periodoDesde, periodoHasta, diasDesde, diasHasta, usaLimitePackEntradas, limitePackEntradas, usaLimiteMinEntradas, limiteMinEntradas, usaLimiteOperacion, limiteOperacion, usaLimiteEvento, limiteEvento, tipoIncentivo, tipoDescuento, valorDescuentoFijo, valorDescuentoPorcentual, limiteSesion, usaLimiteSesion, esPromocionPrivada, esNoGestionable, validacionColectivoOpcional, dirtyBI, noDescontarImporteEnResumen, aplicarCostesRecargosCanalEspecificos, aplicarCostesRecargosPromotorEspecificos, create_date, update_date, accesoRestrictivo, noAcumulable)
VALUES(73, 'Promo Without Description', 423, null, 1, 1, 3, 1, 53, 0, 1, 1, 0, NULL, NULL, NULL, NULL, 0, NULL, 0, NULL, 0, NULL, 0, NULL, 0, 0, -10, NULL, NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2019-03-15 08:03:44.000', '2019-03-15 08:05:17.000', 0, 0);


INSERT INTO ob_cpanel.cpanel_rango_plantilla_promocion
(idPlantillaPromocion, idRango)
VALUES(65, 898);
INSERT INTO ob_cpanel.cpanel_rango_plantilla_promocion
(idPlantillaPromocion, idRango)
VALUES(61, 898);
INSERT INTO ob_cpanel.cpanel_rango_plantilla_promocion
(idPlantillaPromocion, idRango)
VALUES(61, 899);


INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(57, 58, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(58, 59, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(59, 60, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(60, 61, 68, 1, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(61, 62, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(62, 63, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(63, 64, 68, NULL, NULL, NULL);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(64, 65, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(71, 72, 68, 0, 0, 0);
INSERT INTO ob_cpanel.cpanel_plantilla_promocion_evento
(idPromocionEvento, idPlantillaPromocion, idEvento, tipoCanalCompra, tipoSesiones, tipoLocalidades)
VALUES(74, 73, 68, 0, 0, 0);


INSERT INTO ob_cpanel.cpanel_promocion_evento_canal
(idPromocionEvento, idCanal)
VALUES(60, 93);
INSERT INTO ob_cpanel.cpanel_promocion_evento_canal
(idPromocionEvento, idCanal)
VALUES(60, 94);


INSERT INTO ob_cpanel.cpanel_promocion_evento_sesion
(idPromocionEvento, idSesion)
VALUES(60, 1);
INSERT INTO ob_cpanel.cpanel_promocion_evento_sesion
(idPromocionEvento, idSesion)
VALUES(60, 2);
INSERT INTO ob_cpanel.cpanel_promocion_evento_sesion
(idPromocionEvento, idSesion)
VALUES(60, 4);
INSERT INTO ob_cpanel.cpanel_promocion_evento_sesion
(idPromocionEvento, idSesion)
VALUES(60, 5);


INSERT INTO ob_cpanel.cpanel_promocion_evento_zona_precio
(idPromocionEvento, idZona)
VALUES(60, 11);
INSERT INTO ob_cpanel.cpanel_promocion_evento_zona_precio
(idPromocionEvento, idZona)
VALUES(60, 12);


INSERT INTO ob_cpanel.cpanel_promocion_evento_tarifa
(idPromocionEvento, idTarifa)
VALUES(60, 21);
INSERT INTO ob_cpanel.cpanel_promocion_evento_tarifa
(idPromocionEvento, idTarifa)
VALUES(60, 22);
