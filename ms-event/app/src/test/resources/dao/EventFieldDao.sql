drop schema if exists ob_cpanel;
CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_field (
    id INT(11) NOT NULL AUTO_INCREMENT,
    sid VARCHAR(100) NOT NULL,
    maxLength INT(10) NOT NULL,
    fieldType VARCHAR(100) NOT NULL,
    fieldGroup VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_event_field (
	eventFieldId INT(11) NOT NULL AUTO_INCREMENT,
	fieldId INT(11) NOT NULL,
	eventId INT(11) NOT NULL,
	minLength INT(11) NOT NULL,
	maxLength INT(11) NOT NULL,
	fieldOrder TINYINT(2) NOT NULL,
	mandatory TINYINT(1) NOT NULL,
	PRIMARY KEY (eventFieldId)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_event_field_validator (
  eventFieldValidatorId int NOT NULL AUTO_INCREMENT,
  eventFieldId int NOT NULL,
  validatorId int NOT NULL,
  PRIMARY KEY (eventFieldValidatorId)
);

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_tipo_validacion (
  idTipoValidacion int NOT NULL AUTO_INCREMENT,
  tipoValidacion varchar(128) NOT NULL,
  regexpText varchar(1024) DEFAULT NULL,
  javaClass varchar(1024) DEFAULT NULL,
  PRIMARY KEY (idTipoValidacion)
);

INSERT INTO ob_cpanel.cpanel_field VALUES (1, 'ATTENDANT_NAME', 250, 'STRING', 'EVENT_ATTENDANT');
INSERT INTO ob_cpanel.cpanel_field VALUES (2, 'ATTENDANT_SURNAME', 250, 'STRING', 'EVENT_ATTENDANT');
INSERT INTO ob_cpanel.cpanel_field VALUES (3, 'ATTENDANT_MAIL', 250, 'STRING', 'EVENT_ATTENDANT');
INSERT INTO ob_cpanel.cpanel_field VALUES (4, 'ATTENDANT_TOWN', 250, 'STRING', 'EVENT_ATTENDANT');

INSERT INTO ob_cpanel.cpanel_event_field VALUES(1, 1, 285, 1, 250, 1, 1);
INSERT INTO ob_cpanel.cpanel_event_field VALUES(2, 2, 285, 1, 200, 2, 0);
INSERT INTO ob_cpanel.cpanel_event_field VALUES(3, 3, 285, 1, 250, 3, 1);

INSERT INTO ob_cpanel.cpanel_event_field_validator
(eventFieldValidatorId, eventFieldId, validatorId)
VALUES(1, 57300, 4);

INSERT INTO ob_cpanel.cpanel_tipo_validacion
(idTipoValidacion, tipoValidacion, regexpText, javaClass)
VALUES(1, 'INTEGER', '[0-9]*', NULL);
