package es.onebox.event.sorting;

import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.jooq.Field;
import org.jooq.TableField;

import java.util.stream.Stream;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;

public enum ProductField {
    NAME("name", CPANEL_PRODUCT.as(Alias.PRODUCT).NAME);

    private final String requestField;
    private final TableField<CpanelProductRecord, String> field;

    ProductField(String requestField, TableField<CpanelProductRecord, String> field) {
        this.requestField = requestField;
        this.field = field;
    }

    public String getField() {
        return requestField;
    }

    public static Field<String> byName(String requestField) {
        return Stream.of(ProductField.values())
                .filter(value -> value.requestField.equals(requestField))
                .map(value -> value.field)
                .findFirst()
                .orElse(null);
    }

    public static class Alias {
        private Alias() {}

        public static final String PRODUCT = "product";
    }
}
