CREATE SCHEMA IF NOT EXISTS ob_cpanel;

-- -----------------------------------------------------
-- Table ob_cpanel.cpanel_sesion_tarifa
-- -----------------------------------------------------

CREATE TABLE ob_cpanel.cpanel_sesion_tarifa (
  idSesion int(11) NOT NULL,
  idTarifa int(11) NOT NULL,
  defecto bit(1) NOT NULL,
  visibilidad bit(1) NOT NULL DEFAULT 1,
  create_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (idSesion,idTarifa),
  KEY fk_tarifa_sesion_sesion (idSesion),
  KEY fk_tarifa_sesion_tarifa (idTarifa)
);

