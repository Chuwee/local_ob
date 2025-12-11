package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class CalendarDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonProperty("entity_id")
    private Long entityId;
    private String name;
    @JsonProperty("day_types")
    private List<CalendarDayTypeDTO> dayTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CalendarDayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<CalendarDayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
