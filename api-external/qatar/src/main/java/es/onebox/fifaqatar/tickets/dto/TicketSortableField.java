package es.onebox.fifaqatar.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TicketSortableField {
    @JsonProperty("updated_date")
    UPDATED_DATE("ticket.updatedDate");

    private final String field;

    TicketSortableField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
