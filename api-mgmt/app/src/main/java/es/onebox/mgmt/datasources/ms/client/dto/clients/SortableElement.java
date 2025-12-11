package es.onebox.mgmt.datasources.ms.client.dto.clients;

import java.util.Arrays;

public enum SortableElement {
    CREATEDAT("created_at"),
    NAME("name");

    private final String value;

    SortableElement(String value) {
        this.value = value;
    }

    public static SortableElement fromValue(String value) {
        return Arrays.stream(values())
                .filter(elem -> elem.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
