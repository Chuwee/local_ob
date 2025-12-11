package es.onebox.mgmt.tickettemplates.enums;

import java.util.stream.Stream;

public enum TicketTemplateFormatPath {

    PDF(1),
    PRINTER(2);


    private final int format;

    TicketTemplateFormatPath(int format) {
        this.format = format;
    }

    public int getFormat() {
        return format;
    }

    public static TicketTemplateFormatPath byId(int id) {
        return Stream.of(TicketTemplateFormatPath.values())
                .filter(v -> v.format == id)
                .findFirst()
                .orElse(null);
    }
}
