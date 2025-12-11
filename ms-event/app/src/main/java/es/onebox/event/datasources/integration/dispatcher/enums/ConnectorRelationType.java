package es.onebox.event.datasources.integration.dispatcher.enums;

public enum ConnectorRelationType {
    EVENTDATE(0),
    CHANNEL(1),
    VENUE(2),
    EVENT(3);

    private final Integer type;

    ConnectorRelationType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
