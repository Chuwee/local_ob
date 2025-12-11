CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_atributos_evento (
idAtributoEvento int(11) NOT NULL AUTO_INCREMENT,
idEvento int(11) NOT NULL,
idAtributo int(11) NOT NULL,
idValor int(11) DEFAULT NULL,
valor varchar(200) DEFAULT NULL,
PRIMARY KEY (idAtributoEvento)
);

INSERT INTO ob_cpanel.cpanel_atributos_evento (idEvento,idAtributo,idValor,valor) VALUES
(10,1,1,NULL),
(10,1,2,NULL),
(10,1,NULL,'A string value'),
(10,1,NULL,'80');
