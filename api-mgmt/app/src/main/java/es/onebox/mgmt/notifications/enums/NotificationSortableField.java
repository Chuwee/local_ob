package es.onebox.mgmt.notifications.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.FiltrableField;
import java.util.stream.Stream;

public enum NotificationSortableField implements FiltrableField {

    @JsonProperty("internalName")
    INTERNAL_NAME("internalName"),
    @JsonProperty("entity")
    ENTITY("entity"),
    @JsonProperty("operator")
    OPERATOR("operator"),
    @JsonProperty("status")
    STATUS("status");

    private static final long serialVersionUID = 1L;

    String dtoName;

    NotificationSortableField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static NotificationSortableField byName(String name) {
        if(name == null) {
            return null;
        }
        return Stream.of(NotificationSortableField.values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
