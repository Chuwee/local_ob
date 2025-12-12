package es.onebox.ms.notification.webhooks.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.ms.notification.common.utils.FiltrableField;

public enum NotificationSortableField implements FiltrableField {

    @JsonProperty("internalName")
    INTERNALNAME("LOWER(internalName)"),
    @JsonProperty("entity")
    ENTITYID("entityId"),
    @JsonProperty("operator")
    OPERATORID("operatorId"),
    @JsonProperty("status")
    STATUS("status");

    private static final long serialVersionUID = 1L;

    String dtoName;

    NotificationSortableField(String dtoName) {
        this.dtoName = dtoName;
    }

    @Override
    public String getDtoName() {
        return dtoName;
    }
}
