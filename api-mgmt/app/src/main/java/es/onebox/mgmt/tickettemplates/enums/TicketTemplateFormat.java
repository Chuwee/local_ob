package es.onebox.mgmt.tickettemplates.enums;

import java.util.stream.Stream;

public enum TicketTemplateFormat {

    PDF(1),
    PRINTER(2), 
    PASSBOOK(3),
    HARD_TICKET_PDF(4);


    private final int format;

    TicketTemplateFormat(int format) {
        this.format = format;
    }

    public int getFormat() {
        return format;
    }

    public static TicketTemplateFormat byId(int id) {
        return Stream.of(TicketTemplateFormat.values())
                .filter(v -> v.format == id)
                .findFirst()
                .orElse(null);
    }
}
