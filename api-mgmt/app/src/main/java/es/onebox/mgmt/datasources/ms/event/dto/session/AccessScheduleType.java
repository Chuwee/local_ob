package es.onebox.mgmt.datasources.ms.event.dto.session;

public enum AccessScheduleType {
    DEFAULT(1),
    SPECIFIC(2);

    private Integer type;

    AccessScheduleType(Integer type)  {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
