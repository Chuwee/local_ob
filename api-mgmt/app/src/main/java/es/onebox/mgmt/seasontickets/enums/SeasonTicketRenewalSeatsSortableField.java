package es.onebox.mgmt.seasontickets.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum SeasonTicketRenewalSeatsSortableField implements FiltrableField {

    @JsonProperty("mapping_status")
    MAPPING_STATUS("mapping_status", "mapping_status"),

    @JsonProperty("name")
    NAME("name", "name"),

    @JsonProperty("surname")
    SURNAME("surname", "surname"),

    @JsonProperty("birthday")
    BIRTHDAY("birthday", "birthday");

    private static final long serialVersionUID = 1L;

    String name;
    String dtoName;

    SeasonTicketRenewalSeatsSortableField(String name, String dtoName) {
        this.name = name;
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static SeasonTicketRenewalSeatsSortableField byName(String name) {
        return Stream.of(SeasonTicketRenewalSeatsSortableField.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}
