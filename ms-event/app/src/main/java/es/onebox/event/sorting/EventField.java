package es.onebox.event.sorting;

import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIME_ZONE_GROUP;

public enum EventField {

    ID("id", CPANEL_EVENTO.as(Alias.EVENTO).IDEVENTO),
    NAME("name", CPANEL_EVENTO.as(Alias.EVENTO).NOMBRE),
    TYPE("type", CPANEL_EVENTO.as(Alias.EVENTO).TIPOEVENTO),
    STATUS("status", CPANEL_EVENTO.as(Alias.EVENTO).ESTADO),
    START_DATE("date.start", CPANEL_EVENTO.as(Alias.EVENTO).FECHAINICIO),
    START_DATE_TZ("date.start.timezone", CPANEL_TIME_ZONE_GROUP.as("startDateTZ").OLSONID.as("startTZ")),
    END_DATE("date.end", CPANEL_EVENTO.as(Alias.EVENTO).FECHAFIN),
    END_DATE_TZ("date.end.timezone", CPANEL_TIME_ZONE_GROUP.as("endDateTZ").OLSONID.as("endTZ")),
    CURRENCYID("currency_id", CPANEL_EVENTO.as(Alias.EVENTO).IDCURRENCY),
    ENTITYID("entity.id", CPANEL_EVENTO.as(Alias.EVENTO).IDENTIDAD),
    OPERATORID("operator.id", CPANEL_ENTIDAD.as(Alias.ENTIDAD).IDOPERADORA);

    private String requestField;
    private Field<?> field;

    EventField(String requestField, Field<?> field) {
        this.requestField = requestField;
        this.field = field;
    }

    public String getField() {
        return requestField;
    }

    public static Field byName(String requestField) {
        return Stream.of(EventField.values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);

    }

    public static class Alias {
        public static final String EVENTO = "evento";
        public static final String ENTIDAD = "entity";
    }

}
