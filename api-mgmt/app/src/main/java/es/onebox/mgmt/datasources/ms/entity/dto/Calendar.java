package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.List;

public class Calendar implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long entityId;
    private String name;
    private List<CalendarDayType> dayTypes;

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

    public List<CalendarDayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<CalendarDayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
