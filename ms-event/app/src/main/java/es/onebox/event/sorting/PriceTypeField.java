package es.onebox.event.sorting;

import org.jooq.Field;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

public enum PriceTypeField {

    PRICE_TYPE_NAME("price_type_name", CPANEL_ZONA_PRECIOS_CONFIG.as(Alias.ZONA_PRECIOS).DESCRIPCION),
    ENTITY_ID("venue_template_name", CPANEL_CONFIG_RECINTO.as(Alias.CONFIG_RECINTO).NOMBRECONFIGURACION);
   

    private String requestField;
    private Field<? extends Object> field;

    PriceTypeField(String requestField, Field<? extends Object> field) {
        this.requestField = requestField;
        this.field = field;
    }

    public Field<? extends Object> getField() {
        return field;
    }

    public static Field byName(String requestField) {
        return Stream.of(values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);
    }

    public String getRequestField() {
        return requestField;
    }

    public static class Alias {
        public static final String ZONA_PRECIOS = "zonaPreciosConfig";
        public static final String CONFIG_RECINTO = "configRecinto";
    }
}
