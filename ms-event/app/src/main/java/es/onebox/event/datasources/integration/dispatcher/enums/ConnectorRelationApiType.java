package es.onebox.event.datasources.integration.dispatcher.enums;

public enum ConnectorRelationApiType {
    TICKETING(1),
    PARTNERS(2);
    
    private final Integer id;

    ConnectorRelationApiType(Integer id) {
        this.id  = id;
    }

    public Integer getId() {
        return id;
    }
}
