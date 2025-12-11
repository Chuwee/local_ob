package es.onebox.event.packs.enums;

import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK;

public enum PackField {

    ID("id", CPANEL_PACK.IDPACK),
    NAME("name", CPANEL_PACK.NOMBRE);


    private String name;
    private Field<?> field;

    PackField(String name, TableField field) {
        this.name = name;
        this.field = field;
    }

    public static Field byName(String requestField) {
        return Stream.of(PackField.values())
                .filter(value -> value.name.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);

    }
}
