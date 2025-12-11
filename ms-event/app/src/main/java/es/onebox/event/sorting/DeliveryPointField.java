package es.onebox.event.sorting;

import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;
import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DELIVERY_POINT;

public enum DeliveryPointField {
    NAME("name", CPANEL_DELIVERY_POINT.as(Alias.DELIVERY_POINT).NAME);

    private final String requestField;
    private final TableField<CpanelDeliveryPointRecord, String> field;

    DeliveryPointField(String requestField, TableField<CpanelDeliveryPointRecord, String> field) {
        this.requestField = requestField;
        this.field = field;
    }

    public String getField() {
        return requestField;
    }

    public static Field<String> byName(String requestField) {
        return Stream.of(DeliveryPointField.values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);
    }

    public static class Alias {
        private Alias() {}

        public static final String DELIVERY_POINT = "deliveryPoint";
    }
}
