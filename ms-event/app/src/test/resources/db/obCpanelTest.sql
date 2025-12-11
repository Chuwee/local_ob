CREATE SCHEMA ob_cpanel;

CREATE TABLE ob_cpanel.cpanel_evento (
  idEvento int(5) not null primary key,
  estado varchar(20) not null,
  fechaInicio DATETIME null,
  fechaFin DATETIME null,
  archivado TINYINT(1) null
);

CREATE TABLE ob_cpanel.cpanel_sesion (
  idSesion int(11) not null primary key,
  idEvento int(11) not null,
  estado int(11) not null,
  archivado TINYINT(1) not null default 0
);

INSERT INTO ob_cpanel.cpanel_evento (idEvento, estado, fechaInicio, fechaFin) VALUES
  (1, 1, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- PLANIFICADO
  (2, 7, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- FINALIZADO
  (3, 0, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- BORRADO
  (4, 3, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- LISTO
  (5, 3, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- LISTO
  (6, 7, '2014-06-01 08:00:00', '2014-06-01 08:00:00'), -- FINALIZADO
  (7, 7, '2030-06-01 08:00:00', '2014-06-01 08:00:00'), -- FINALIZADO > 2030 fechaInicio
  (8, 7, '2014-06-01 08:00:00', '2032-06-01 08:00:00'), -- FINALIZADO > 2032 fechaFin
  (9, 7, '2009-06-01 08:00:00', '2009-06-01 08:00:00'); -- FINALIZADO - Not all sessions


INSERT INTO ob_cpanel.cpanel_sesion (idSesion, idEvento, estado) VALUES
  (1, 4, 3), -- LISTO
  (2, 2, 7), -- FINALIZADO
  (3, 2, 0), -- BORRADO de Evento FINALIZADO
  (4, 2, 7), -- FINALIZADO
  (5, 3, 0), -- BORRADO
  (6, 6, 7), -- FINALIZADO
  (7, 6, 7), -- FINALIZADO
  (8, 7, 7), -- FINALIZADO - fechaInicio
  (9, 8, 7), -- FINALIZADO - fechaFin
  (10, 9, 3), -- LISTO de Evento FINALIZADO
  (11, 9, 7); -- FINALIZADO