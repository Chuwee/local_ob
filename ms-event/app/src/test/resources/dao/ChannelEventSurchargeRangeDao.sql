CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_rango_recargo_canal_evento (
    idCanalEvento int(11) NOT NULL,
    idRango int(11) NOT NULL,
    create_date datetime,
    update_date datetime,
    PRIMARY KEY (idCanalEvento,idRango)
);

CREATE TABLE ob_cpanel.cpanel_rango_recargo_evento (
    idEvento int(11) NOT NULL,
    idRango int(11) NOT NULL,
    PRIMARY KEY (idEvento,idRango)
);

CREATE TABLE ob_cpanel.cpanel_rango (
  idRango int(11) NOT NULL AUTO_INCREMENT,
  nombreRango varchar(50) NOT NULL,
  rangoMaximo double DEFAULT NULL,
  rangoMinimo double DEFAULT NULL,
  valor double DEFAULT NULL,
  porcentaje double DEFAULT NULL,
  valorMaximo double DEFAULT NULL,
  valorMinimo double DEFAULT NULL,
  idCurrency int DEFAULT NULL,
  PRIMARY KEY (idRango)
);

INSERT INTO ob_cpanel.cpanel_rango(idRango,nombreRango,rangoMaximo,rangoMinimo,valor,porcentaje,valorMaximo,valorMinimo, idCurrency) VALUES
(100,'0.0-19.0-ev-1',18.95,0,51.56,62.91,621.6,71.42,1),
(200,'19.0-0.0-ev-1',0,18.95,51.56,62.91,621.6,71.42,1),
(300,'19.0-0.0-ev-1',0,18.95,51.56,62.91,621.6,71.42,1);


insert into ob_cpanel.cpanel_rango_recargo_evento (idEvento, idRango) values
(1, 100),
(1, 200),
(1, 300);

