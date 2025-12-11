CREATE SCHEMA IF NOT EXISTS ob_cpanel;
USE ob_cpanel;

CREATE TABLE if not exists cpanel_config_recinto (
  idConfiguracion int(11) NOT NULL,
  estado int(11) NOT NULL
);

insert into cpanel_config_recinto values (1, 0);
insert into cpanel_config_recinto values (2, 1);
insert into cpanel_config_recinto values (3, 2);
insert into cpanel_config_recinto values (4, 3);
