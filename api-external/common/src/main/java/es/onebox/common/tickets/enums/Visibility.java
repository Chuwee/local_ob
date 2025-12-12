package es.onebox.common.tickets.enums;

public enum Visibility {
    NORMAL("BUENA"),
    REDUCED("REDUCIDA"),
    NULL("NULA"),
    SIDE("LATERAL");

    private String type;

    Visibility(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
