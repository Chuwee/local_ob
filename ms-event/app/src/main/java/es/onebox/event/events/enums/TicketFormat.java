package es.onebox.event.events.enums;

import java.util.stream.Stream;

public enum TicketFormat {
    PDF(1),
    ZPL(2),
    PASSBOOK(3),
    HARD_TICKET_PDF(4),
    QR(5);

    private final Integer format;

    TicketFormat(Integer format) {
        this.format = format;
    }

    public Integer getFormat() {
        return format;
    }

    public static TicketFormat byId(Integer id) {
        return Stream.of(TicketFormat.values())
                .filter(v -> v.getFormat().equals(id))
                .findFirst()
                .orElse(null);
    }
}
