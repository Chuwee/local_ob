package es.onebox.event.seasontickets.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortableField;

public enum RenewalsSortableField implements SortableField {
    @JsonProperty("mapping_status")
    MAPPING_STATUS(RenewalESProperty.MAPPING_STATUS.getProperty()),

    @JsonProperty("name")
    NAME(RenewalESProperty.NAME.getProperty()),

    @JsonProperty("surname")
    SURNAME(RenewalESProperty.SURNAME.getProperty()),

    @JsonProperty("birthday")
    BIRTHDAY(RenewalESProperty.BIRTHDAY.getProperty());


    private String name;

    RenewalsSortableField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
