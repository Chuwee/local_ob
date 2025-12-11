package es.onebox.mgmt.datasources.integration.dispatcher.enums;

public enum Status {
    CONNECTED("CONNECTED"),
    NOT_CONNECTED("NOT_CONNECTED"),
    DISABLED("DISABLED"),
    REFRESHING("REFRESHING"),
    ERROR("ERROR");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
