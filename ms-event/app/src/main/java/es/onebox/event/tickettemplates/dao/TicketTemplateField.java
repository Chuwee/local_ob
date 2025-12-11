package es.onebox.event.tickettemplates.dao;

import es.onebox.jooq.cpanel.tables.CpanelPlantillaTicket;
import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

public enum TicketTemplateField {

    ID("id", CpanelPlantillaTicket.CPANEL_PLANTILLA_TICKET.as("template").IDPLANTILLA),
    NAME("name", CpanelPlantillaTicket.CPANEL_PLANTILLA_TICKET.as("template").NOMBRE);

    private String requestField;
    private Field<?> field;

    TicketTemplateField(String requestField, TableField field) {
        this.requestField = requestField;
        this.field = field;
    }

    public String getField() {
        return requestField;
    }

    public static Field byName(String requestField) {
        return Stream.of(TicketTemplateField.values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);

    }

}
