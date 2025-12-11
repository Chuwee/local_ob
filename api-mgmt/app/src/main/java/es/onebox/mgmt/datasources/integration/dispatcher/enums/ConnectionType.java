package es.onebox.mgmt.datasources.integration.dispatcher.enums;

public enum ConnectionType {

    TICKETING("ticketing"),
    MEMBERS("members");

    private final String name;

    ConnectionType(String name) {
        this.name = name;
    }
}
