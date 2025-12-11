CREATE SCHEMA IF NOT EXISTS ob_cpanel;
CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_promotor (
       idPromotor int(11) NOT NULL AUTO_INCREMENT,
       idEntidad int(11) DEFAULT NULL,
       nombre varchar(200) NOT NULL,
       razonSocial varchar(100) NOT NULL,
       nif varchar(11) NOT NULL,
       estado tinyint(4) NOT NULL,
       predeterminado tinyint(1) DEFAULT NULL,
       direccion varchar(200) DEFAULT NULL,
       pais int(11) DEFAULT NULL,
       provincia int(11) DEFAULT NULL,
       municipio varchar(50) DEFAULT NULL,
       cPostal varchar(10) DEFAULT NULL,
       personaContacto varchar(250) DEFAULT NULL,
       emailContacto varchar(200) DEFAULT NULL,
       telefonoContacto varchar(30) DEFAULT NULL,
       dirtyBI bit(1) NOT NULL DEFAULT 0,
       PRIMARY KEY (idPromotor),
       KEY fk_promotor_entidad (idEntidad),
       KEY fk_promotor_pais (pais),
       KEY fk_promotor_provincia (provincia)
);


INSERT INTO ob_cpanel.cpanel_promotor (idPromotor, idEntidad, nombre, razonSocial, nif, estado, predeterminado,
                                      direccion, pais, provincia, municipio, cPostal, personaContacto, emailContacto,
                                      telefonoContacto)
VALUES (1, 1, 'promotor 1', '2356235672', '326234632', 1, 0, 'c/ castaña 1',
                1, 1, 'gsdfhysd', '08354', 'persona 1', 'correo1@af.com', '324523461'),
       (2, 1, 'promotor 2', '3463467436', '867969245', 1, 1, 'c/ castaña 2',
                1, 1, 'sdhbvewh', '08532', 'persona 2', 'correo2@af.com', '235612364'),
       (3, 1, 'promotor 3', '4357246547', '346867492', 1, 0, 'c/ castaña 3',
                2, 3, 'shdfhsdf', '09567', 'persona 4', 'correo3@af.com', '946781245'),
       (4, 2, 'promotor 21', '736852657', '658356764', 1, 1, 'c/ castaño 1',
                3, 1, 'hre64dfh', '09356', 'persona 6', 'correo4@af.com', '8573457341');
