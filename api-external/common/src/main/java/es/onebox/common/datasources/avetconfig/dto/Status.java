package es.onebox.common.datasources.avetconfig.dto;

public enum Status {
    OK("OK"),KO("KO");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
