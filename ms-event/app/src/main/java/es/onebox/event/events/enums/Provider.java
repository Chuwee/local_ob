package es.onebox.event.events.enums;

public enum Provider {
    
    SEETICKETS("int-seetickets-connector"),
    ITALIAN_COMPLIANCE("int-italy-compliance-connector"),
    IFEMA("int-ifema-connector"),
    SGA("int-sga-connector");
    
    private final String connector;

    private Provider(String connector) {
        this.connector = connector;
    }
    
    public String getConnector() {
        return connector;
    }

}
