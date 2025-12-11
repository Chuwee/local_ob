package es.onebox.mgmt.seasontickets.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum SeasonTicketSessionSortableField implements FiltrableField {

    @JsonProperty("session_starting_date")
    SESSION_DATE("session_starting_date", "session_starting_date"),

    @JsonProperty("event_name")
    EVENT_NAME("event_name", "event_name"),

    @JsonProperty("session_name")
    SESSION_NAME("session_name", "session_name"),

    @JsonProperty("status")
    ASSIGNATION_STATUS("status", "assignation_status");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    SeasonTicketSessionSortableField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static SeasonTicketSessionSortableField byName(String name) {
        return Stream.of(SeasonTicketSessionSortableField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
