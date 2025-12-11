CREATE SCHEMA IF NOT EXISTS ob_cpanel;

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad (
    idEntidad int(11) NOT NULL PRIMARY KEY,
    idOperadora int(11) NOT NULL,
    nombre varchar(50) NOT NULL,
    estado int(11) NOT NULL
    );

CREATE TABLE IF NOT EXISTS ob_cpanel.cpanel_entidad_admin_entidades (
    idEntidadAdmin int(11) NOT NULL,
    idEntidad int(11) NOT NULL,
    PRIMARY KEY (idEntidadAdmin,idEntidad),
    KEY fk_entidad_entidades (idEntidad),
    CONSTRAINT fk_entidad_admin_entidades FOREIGN KEY (idEntidadAdmin) REFERENCES cpanel_entidad (idEntidad),
    CONSTRAINT fk_entidad_entidades FOREIGN KEY (idEntidad) REFERENCES cpanel_entidad (idEntidad)
    );

INSERT INTO ob_cpanel.cpanel_entidad
(idEntidad, idOperadora, nombre, estado) VALUES
                                             (1, 1, 'Entidad 1', 1),
                                             (2, 1, 'entityadmin', 1),
                                             (3, 1, 'Entidad Admin managed', 1),
                                             (4, 1, 'Entidad Admin managed 2', 1);

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES(1, 2);

INSERT INTO ob_cpanel.cpanel_entidad_admin_entidades
(idEntidadAdmin, idEntidad)
VALUES(1, 3);

create table if not exists ob_cpanel.cpanel_product_ticket_model
(
    modelId         int primary key not null auto_increment,
    name            varchar(50) not null,
    description     varchar(200),
    modelType       int(1) not null,
    targetType		int(1) not null,
    fileName        varchar(100) not null,
    create_date     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


create table if not exists ob_cpanel.cpanel_product_ticket_template
(
    templateId  int(20) primary key auto_increment,
    name        varchar(50) not null,
    entityId    int(20),
    modelId     int(20),
    status      tinyint(1) default 1,
    create_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    foreign key (entityId) references ob_cpanel.cpanel_entidad(idEntidad),
    foreign key (modelId) references ob_cpanel.cpanel_product_ticket_model(modelId)
);

-- Insert product ticket templates with explicit IDs
insert into ob_cpanel.cpanel_product_ticket_model(modelId, name, description, modelType, targetType, fileName)
values
    (1,'Default product PDF ticket', 'The default PDF model for all products', 1, 1, 'ProductTicket.jasper'),
    (2,'Default product PDF hard ticket', 'The default PDF/hard-ticket model (just for printers) for all products', 1, 2, 'ProductHardTicket.jasper'),
    (3,'PDF without delivery point', 'PDF model for products without delivery point information', 1, 1, 'ProductTicketNoDeliveryPoint.jasper');

INSERT INTO `ob_cpanel`.`cpanel_product_ticket_template`
(`templateId`, `name`, `entityId`, `modelId`, `status`) VALUES
                                                            (100, 'Basic Web Hosting Support', 1, 1, 1),
                                                            (101, 'Premium Web Hosting Support', 2, 1, 1),
                                                            (102, 'Domain Registration Help', 1, 2, 1),
                                                            (103, 'Domain Transfer Assistance', 3, 2, 1),
                                                            (104, 'SSL Certificate Installation', 1, 3, 1),
                                                            (105, 'Wildcard SSL Setup', 2, 3, 1),
                                                            (106, 'Email Account Setup', 1, 2, 1),
                                                            (107, 'Business Email Configuration', 2, 1, 1),
                                                            (108, 'MySQL Database Support', 1, 3, 1),
                                                            (109, 'PostgreSQL Database Help', 3, 2, 1),
                                                            (110, 'FTP Account Management', 1, 1, 1),
                                                            (111, 'SFTP Configuration', 2, 2, 1),
                                                            (112, 'Website Backup Service', 1, 1, 1),
                                                            (113, 'Database Backup Restore', 2, 1, 1),
                                                            (114, 'Server Migration Basic', 1, 3, 1),
                                                            (115, 'Server Migration Premium', 2, 2, 1),
                                                            (116, 'Website Speed Optimization', 1, 1, 1),
                                                            (117, 'Performance Monitoring', 3, 2, 1),
                                                            (118, 'Security Scan Basic', 1, 3, 1),
                                                            (119, 'Advanced Security Audit', 2, 3, 1),
                                                            (120, 'Billing Questions', 1, 1, 1),
                                                            (121, 'Payment Issues', 3, 2, 1),
                                                            (122, 'General Technical Help', 1, 3, 1),
                                                            (123, 'Priority Technical Support', 2, 1, 1),
                                                            (124, 'Account Settings Update', 1, 2, 1),
                                                            (125, 'User Management', 2, 1, 1),
                                                            (126, 'CDN Basic Setup', 1, 3, 2),
                                                            (127, 'CDN Advanced Configuration', 2, 1, 1),
                                                            (128, 'Basic Monitoring', 1, 2, 1),
                                                            (129, 'Advanced Monitoring Suite', 2, 1, 1);

commit;
