package es.onebox.event.datasources.ms.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProductSortableField {
    @JsonProperty("purchase_date")
    PURCHASE_DATE,
    @JsonProperty("event_name")
    EVENT_NAME,
    @JsonProperty("session_name")
    SESSION_NAME
}
