package es.onebox.event.sorting;

import org.jooq.Field;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIME_ZONE_GROUP;
import static es.onebox.jooq.cpanel.tables.CpanelSesion.CPANEL_SESION;

public enum SessionField {

    ID("id", CPANEL_SESION.as(Alias.SESSION).IDSESION),
    NAME("name", CPANEL_SESION.as(Alias.SESSION).NOMBRE),
    TYPE("type", CPANEL_SESION.as(Alias.SESSION).ESABONO),
    STATUS("status", CPANEL_SESION.as(Alias.SESSION).ESTADO),
    IS_PREVIEW("isPreview", CPANEL_SESION.as(Alias.SESSION).ISPREVIEW),
    SALE("sale", CPANEL_SESION.as(Alias.SESSION).ENVENTA),
    STATUS_FLAGS("status.flags", null),//KEEP NULL!!
    GENERATION_STATUS("generationStatus", CPANEL_SESION.as(Alias.SESSION).ESTADOGENERACIONAFORO),
    PUBLICATION_CANCELLED_REASON("publicationCancelledReason", CPANEL_SESION.as(Alias.SESSION).RAZONCANCELACIONPUBLICACION),
    RELEASE_ENABLED("releaseEnabled", CPANEL_SESION.as(Alias.SESSION).PUBLICADO),
    EVENT_ID("eventId", CPANEL_SESION.as(Alias.SESSION).IDEVENTO),
    EVENT_NAME("eventName", CPANEL_EVENTO.as(Alias.EVENTO).NOMBRE.as("evento.nombre")),
    EVENT_STATUS("event.status", CPANEL_EVENTO.as(Alias.EVENTO).ESTADO.as("evento.estado")),
    EVENT_ENTITY_ID("event.entity.id", CPANEL_EVENTO.as(Alias.EVENTO).IDENTIDAD),
    START_DATE("date.start", CPANEL_SESION.as(Alias.SESSION).FECHAINICIOSESION),
    END_DATE("date.end", CPANEL_SESION.as(Alias.SESSION).FECHAFINSESION),
    REAL_END_DATE("date.end", CPANEL_SESION.as(Alias.SESSION).FECHAREALFINSESION),
    RELEASE_DATE("date.release", CPANEL_SESION.as(Alias.SESSION).FECHAPUBLICACION),
    SALE_DATE("date.salesStart", CPANEL_SESION.as(Alias.SESSION).FECHAVENTA),
    SALE_END_DATE("date.salesEnd", CPANEL_SESION.as(Alias.SESSION).FECHAFINSESION),
    ADMISSION_START("date.admissionStart", CPANEL_SESION.as(Alias.SESSION).APERTURAACCESOS),
    ADMISSION_END("date.admissionEnd", CPANEL_SESION.as(Alias.SESSION).CIERREACCESOS),
    ADMISSION("admission", CPANEL_SESION.as(Alias.SESSION).TIPOHORARIOACCESOS),
    CAPACITY("capacity", CPANEL_SESION.as(Alias.SESSION).AFORO),
    USE_TEMPLATE_ACCESS("useTemplateAccess", CPANEL_SESION.as(Alias.SESSION).USAACCESOSPLANTILLA),
    ENTITY_ID("entity.id", CPANEL_ENTIDAD.as(Alias.ENTITY).IDENTIDAD),
    OPERATOR_ID("operator.id", CPANEL_ENTIDAD.as(Alias.ENTITY).IDOPERADORA),
    SMART_BOOKING_SETTING_RELATED_SESSION("smartBooking.relatedSession", CPANEL_SESION.as(Alias.SESSION).SBSESIONRELACIONADA),
    VENUE_TEMPLATE_ID("venueTemplate.id", CPANEL_CONFIG_RECINTO.as(Alias.VENUE_CONFIG).IDCONFIGURACION),
    VENUE_TEMPLATE_NAME("venueTemplate.name", CPANEL_CONFIG_RECINTO.as(Alias.VENUE_CONFIG).NOMBRECONFIGURACION.as("configRecinto.nombreConfiguracion")),
    VENUE_TEMPLATE_NAME_TEMPLATE_TYPE("venueTemplate.templateType", CPANEL_CONFIG_RECINTO.as(Alias.VENUE_CONFIG).TIPOPLANTILLA),
    VENUE_TEMPLATE_VENUE_ID("venueTemplate.venue.id", CPANEL_RECINTO.as(Alias.VENUE).IDRECINTO),
    VENUE_TEMPLATE_VENUE_NAME("venueTemplate.venue.name", CPANEL_RECINTO.as(Alias.VENUE).NOMBRE.as("recinto.nombre")),
    VENUE_TEMPLATE_VENUE_TZ("venueTemplate.venue.timezone", CPANEL_TIME_ZONE_GROUP.as(Alias.VENUE_TZ).OLSONID),
    TICKET_COMMUNICATION_ELEMENT_PDF("ticket.element.pdf", CPANEL_SESION.as(Alias.SESSION).ELEMENTOCOMTICKET),
    TICKET_COMMUNICATION_ELEMENT_TICKET_OFFICE("ticket.element.ticket_office",
            CPANEL_SESION.as(Alias.SESSION).ELEMENTOCOMTICKETTAQUILLA),
    SETTINGS_ENABLE_ORPHAN_SEATS("settings.enableOrphanSeats", CPANEL_SESION.as(Alias.SESSION).CHECKORPHANSEATS);

    private final String requestField;
    private final Field<?> field;

    SessionField(String requestField, Field<?> field) {
        this.requestField = requestField;
        this.field = field;
    }

    public Field<?> getField() {
        return field;
    }

    public static Field<?> byName(String requestField) {
        return Stream.of(values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);
    }

    public static SessionField getByRequestField(String requestField) {
        return Stream.of(values())
                .filter(value -> value.requestField.equals(requestField))
                .findFirst()
                .orElse(null);
    }

    public String getRequestField() {
        return requestField;
    }

    private static class Alias {
        private static final String SESSION = "sesion";
        private static final String EVENTO = "evento";
        private static final String ENTITY = "entidad";
        private static final String VENUE_CONFIG = "configRecinto";
        private static final String VENUE = "recinto";
        private static final String VENUE_TZ = "recintoTZ";
    }
}
