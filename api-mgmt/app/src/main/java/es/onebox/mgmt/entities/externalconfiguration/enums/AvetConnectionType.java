package es.onebox.mgmt.entities.externalconfiguration.enums;

public enum AvetConnectionType {
    SOCKET("SOCKET"),
    WEBSERVICES("WEBSERVICES"),
    APIM("APIM");

    private final String value;

    AvetConnectionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
