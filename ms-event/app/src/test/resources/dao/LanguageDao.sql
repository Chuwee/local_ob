CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_idioma (idIdioma int(11) NOT NULL AUTO_INCREMENT,
                                                    codigo varchar(15) DEFAULT NULL,
                                                    descripcion varchar(200)  DEFAULT NULL,
                                                    idiomaPlataforma tinyint(1) DEFAULT NULL,
                                                    create_date datetime DEFAULT NULL,
                                                    update_date datetime DEFAULT NULL,
                                                    PRIMARY KEY (`idIdioma`),
                                                    UNIQUE KEY `codigo_UNIQUE` (`codigo`));

INSERT INTO ob_cpanel.cpanel_idioma
(`idIdioma`, `codigo`, `descripcion`, `idiomaPlataforma`) VALUES
('1', 'es_ES', 'Castellano', '1'),
('2', 'ca_ES', 'Català', '1'),
('3', 'en_US', 'English', '1'),
('4', 'ca_ES_valencia', 'Valencià', '0'),
('5', 'it_IT', 'Italiano', '0'),
('6', 'pt_PT', 'Português', '0'),
('7', 'fr_FR', 'Français', '0'),
('8', 'de_DE', 'Deutsch', '0');
