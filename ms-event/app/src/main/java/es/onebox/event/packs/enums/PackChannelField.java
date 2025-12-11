package es.onebox.event.packs.enums;

import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL;

public enum PackChannelField {

    NAME("channelname", CPANEL_CANAL.as(Alias.CHANNEL).NOMBRECANAL);

    private String requestField;
    private Field<?> field;

    PackChannelField(String requestField, TableField field) {
        this.requestField = requestField;
        this.field = field;
    }

    public static Field byName(String requestField) {
        return Stream.of(PackChannelField.values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);

    }

    public static class Alias {
        private Alias() {
        }

        public static final String EVENT_CHANNEL = "eventChannel";
        public static final String CHANNEL = "channel";
    }

}
