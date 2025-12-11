package es.onebox.event.sessions.dao;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import es.onebox.jooq.cpanel.tables.CpanelConfigRecintoMultimediaContent;
import es.onebox.jooq.cpanel.tables.CpanelMultimediaContentPluginExterno;
import es.onebox.jooq.cpanel.tables.CpanelPluginExterno;
import es.onebox.jooq.cpanel.tables.CpanelPoliticasShard;
import es.onebox.jooq.cpanel.tables.CpanelSesionesAbono;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelEspacio;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelImpuesto;
import es.onebox.jooq.cpanel.tables.CpanelRecinto;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTimeZoneGroup;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEspacioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import es.onebox.jooq.dao.DaoImpl;

public abstract class SessionDaoSupport extends DaoImpl<CpanelSesionRecord, Integer> {

    protected static final String OLSON_UTC = "UTC";
    protected static final String SEPARATOR = "' , '";
    protected static final String DEFAULT_SESSION_SHARD = "SHARD_1";
    protected static final String PLUGINS = "plugins";

    protected static final CpanelSesion sesion = CPANEL_SESION.as("sesion");
    protected static final CpanelEvento evento = Tables.CPANEL_EVENTO.as("evento");
    protected static final CpanelEntidad entidad = Tables.CPANEL_ENTIDAD.as("entidad");
    protected static final CpanelEntidadRecintoConfig entidadRecintoConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG.as("entidadRecintoConfig");
    protected static final CpanelConfigRecinto configRecinto = Tables.CPANEL_CONFIG_RECINTO.as("configRecinto");
    protected static final CpanelConfigRecintoMultimediaContent configRecintoMultimediaContent = Tables.CPANEL_CONFIG_RECINTO_MULTIMEDIA_CONTENT.as("configRecintoMultimediaContent");
    protected static final CpanelMultimediaContentPluginExterno multimediaContentPluginExterno = Tables.CPANEL_MULTIMEDIA_CONTENT_PLUGIN_EXTERNO.as("multimediaContentPluginExterno");
    protected static final CpanelPluginExterno pluginExterno = Tables.CPANEL_PLUGIN_EXTERNO.as("pluginExterno");
    protected static final CpanelEspacio configRecintoEspacio = Tables.CPANEL_ESPACIO.as("configRecintoEspacio");
    protected static final CpanelRecinto recinto = Tables.CPANEL_RECINTO.as("recinto");
    protected static final CpanelTimeZoneGroup recintoTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("recintoTZ");
    protected static final CpanelImpuesto ticketTax = Tables.CPANEL_IMPUESTO.as("ticketTax");
    protected static final CpanelImpuesto chargesTax = Tables.CPANEL_IMPUESTO.as("chargesTax");
    protected static final CpanelEspacio espacio = Tables.CPANEL_ESPACIO.as("espacio");
    protected static final CpanelPoliticasShard politicasShard = Tables.CPANEL_POLITICAS_SHARD.as("shard");
    protected static final CpanelSesionesAbono sesionesAbono = Tables.CPANEL_SESIONES_ABONO.as("sesionesAbono");

    protected static final TableField<CpanelSesionRecord, Integer> ID_EVENTO = sesion.IDEVENTO;
    protected static final TableField<CpanelSesionRecord, Integer> ID_SESION = sesion.IDSESION;
    protected static final TableField<CpanelEventoRecord, String> JOIN_EVENT_NAME = evento.NOMBRE;
    protected static final TableField<CpanelEventoRecord, Byte> JOIN_EVENT_PACK_TYPE = evento.TIPOABONO;
    protected static final TableField<CpanelEventoRecord, Integer> JOIN_EVENT_TYPE = evento.TIPOEVENTO;
    protected static final TableField<CpanelEventoRecord, Integer> JOIN_EVENT_STATUS = evento.ESTADO;
    protected static final TableField<CpanelEntidadRecord, Integer> JOIN_ENTITY_ID = entidad.IDENTIDAD;
    protected static final TableField<CpanelEntidadRecord, String> JOIN_ENTITY_NAME = entidad.NOMBRE;
    protected static final TableField<CpanelEntidadRecord, Integer> JOIN_OPERATOR_ID = entidad.IDOPERADORA;
    protected static final TableField<CpanelConfigRecintoRecord, Integer> JOIN_VENUETEMPLATE_ID = configRecinto.IDCONFIGURACION;
    protected static final TableField<CpanelConfigRecintoRecord, String> JOIN_VENUETEMPLATE_NAME = configRecinto.NOMBRECONFIGURACION;
    protected static final TableField<CpanelConfigRecintoRecord, Integer> JOIN_VENUETEMPLATE_TYPE = configRecinto.TIPOPLANTILLA;
    protected static final TableField<CpanelConfigRecintoRecord, Byte> JOIN_VENUETEMPLATE_GRAPHIC = configRecinto.ESGRAFICA;
    protected static final TableField<CpanelEspacioRecord, Integer> JOIN_VENUETEMPLATE_SPACE_ID = configRecintoEspacio.IDESPACIO;
    protected static final TableField<CpanelEspacioRecord, String> JOIN_VENUETEMPLATE_SPACE_NAME = configRecintoEspacio.NOMBRE;
    protected static final TableField<CpanelRecintoRecord, Integer> JOIN_VENUE_ID = recinto.IDRECINTO;
    protected static final TableField<CpanelRecintoRecord, String> JOIN_VENUE_NAME = recinto.NOMBRE;
    protected static final TableField<CpanelRecintoRecord, String> JOIN_VENUE_CITY = recinto.MUNICIPIO;
    protected static final TableField<CpanelRecintoRecord, Integer> JOIN_VENUE_COUNTRY = recinto.PAIS;
    protected static final TableField<CpanelTimeZoneGroupRecord, String> JOIN_VENUE_TZ_OLSON = recintoTZ.OLSONID;
    protected static final TableField<CpanelTimeZoneGroupRecord, String> JOIN_VENUE_TZ_NAME = recintoTZ.DISPLAYNAME;
    protected static final TableField<CpanelTimeZoneGroupRecord, Integer> JOIN_VENUE_TZ_OFFSET = recintoTZ.RAWOFFSETMINS;
    protected static final TableField<CpanelEspacioRecord, String> JOIN_SPACE_NAME = espacio.NOMBRE;
    protected static final TableField<CpanelImpuestoRecord, String> JOIN_TAX_TICKET_NAME = ticketTax.NOMBRE;
    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_TAX_TICKET_ID = ticketTax.IDIMPUESTO;
    protected static final TableField<CpanelImpuestoRecord, Double> JOIN_TAX_TICKET_VALUE = ticketTax.VALOR;
    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_TAX_CHARGES_ID = chargesTax.IDIMPUESTO;
    protected static final TableField<CpanelImpuestoRecord, String> JOIN_TAX_CHARGES_NAME = chargesTax.NOMBRE;
    protected static final TableField<CpanelImpuestoRecord, Double> JOIN_TAX_CHARGES_VALUE = chargesTax.VALOR;


    protected static final SelectFieldOrAsterisk[] JOIN_CATALOG_FIELDS = {
            JOIN_TAX_TICKET_NAME,
            JOIN_TAX_TICKET_ID,
            JOIN_TAX_TICKET_VALUE,
            JOIN_TAX_CHARGES_ID,
            JOIN_TAX_CHARGES_NAME,
            JOIN_TAX_CHARGES_VALUE
    };


    protected static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_EVENT_NAME.as("evento.nombre"),
            JOIN_EVENT_TYPE,
            JOIN_EVENT_PACK_TYPE,
            JOIN_EVENT_STATUS.as("evento.estado"),
            JOIN_ENTITY_ID,
            JOIN_ENTITY_NAME.as("entidad.nombre"),
            JOIN_OPERATOR_ID,
            JOIN_VENUETEMPLATE_ID,
            JOIN_VENUETEMPLATE_NAME,
            JOIN_VENUETEMPLATE_TYPE,
            JOIN_VENUETEMPLATE_GRAPHIC,
            JOIN_VENUETEMPLATE_SPACE_ID,
            JOIN_VENUETEMPLATE_SPACE_NAME.as("configRecintoEspacio.nombre"),
            JOIN_VENUE_ID,
            JOIN_VENUE_NAME.as("recinto.nombre"),
            JOIN_VENUE_CITY,
            JOIN_VENUE_COUNTRY,
            JOIN_VENUE_TZ_OLSON,
            JOIN_VENUE_TZ_NAME,
            JOIN_VENUE_TZ_OFFSET,
            JOIN_SPACE_NAME.as("espacio.nombre"),
            JOIN_TAX_TICKET_NAME.as("ticketTax.nombre"),
            JOIN_TAX_CHARGES_NAME.as("chargesTax.nombre")
    };

    public SessionDaoSupport(Table table) {
        super(table);
    }

    public SessionDaoSupport(Table table, DSLContext dsl, Configuration configuration) {
        super(table, dsl, configuration);
    }

    protected Timestamp today() {
        return Timestamp.from(ZonedDateTime.now().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    protected static final SelectFieldOrAsterisk[] EVENT_ID_FIELDS = {
            ID_EVENTO,
            ID_SESION
    };

    public static class CaseStatement {

        private CaseStatement() {
        }

        public static final Field<Object> ADMISSION_START_NULLABLE = DSL
                .when(sesion.APERTURAACCESOS.isNull().or(sesion.TIPOHORARIOACCESOS.ne((byte)2)), defaultStartAccessControlDate())
                .otherwise(sesion.APERTURAACCESOS);

        public static final Field<Object> ADMISSION_END_NULLABLE = DSL
                .when(sesion.TIPOHORARIOACCESOS.ne((byte) 2).and(sesion.FECHAREALFINSESION.isNotNull()), defaultRealEndAccessControlDate())
                .when(sesion.TIPOHORARIOACCESOS.eq((byte) 2), sesion.CIERREACCESOS)
                .otherwise(defaultEndAccessControlDate());

        /**
         * Be aware: Just for MySQL
         * @return Field
         */
        private static Field<Object> defaultStartAccessControlDate() {
            return DSL.field(new StringBuilder("STR_TO_DATE(CONCAT(DATE(").append(sesion.FECHAINICIOSESION.getName())
                    .append("), ' ', '00:00:00'), '%Y-%m-%d %H:%i:%s')").toString());
        }

        /**
         * Be aware: Just for MySQL
         * @return Field
         */
        private static Field<Object> defaultEndAccessControlDate() {
            return DSL.field(new StringBuilder("STR_TO_DATE(CONCAT(DATE(").append(sesion.FECHAFINSESION.getName())
                    .append("), ' ', '23:59:59'), '%Y-%m-%d %H:%i:%s')").toString());
        }

        /**
         * Be aware: Just for MySQL
         * @return Field
         */
        private static Field<Object> defaultRealEndAccessControlDate() {
            return DSL.field(new StringBuilder("STR_TO_DATE(CONCAT(DATE(").append(sesion.FECHAREALFINSESION.getName())
                    .append("), ' ', '23:59:59'), '%Y-%m-%d %H:%i:%s')").toString());
        }
    }

}
