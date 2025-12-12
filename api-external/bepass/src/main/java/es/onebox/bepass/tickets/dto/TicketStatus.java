package es.onebox.bepass.tickets.dto;

public enum TicketStatus {
    ACTIVE("active"),
    CANCELED("canceled");

    private String value;

    TicketStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
