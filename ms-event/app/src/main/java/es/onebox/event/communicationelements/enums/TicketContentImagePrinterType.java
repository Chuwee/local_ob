package es.onebox.event.communicationelements.enums;

import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;

import java.awt.Dimension;

public enum TicketContentImagePrinterType {
    BODY_200DPI(432, 488),
    BANNER_SECONDARY_200DPI(304,64);

    private final Dimension dimension;

    TicketContentImagePrinterType(Integer width, Integer height) {
        this.dimension = new Dimension(width, height);
    }

    public static Dimension get200dpiVersion(TicketCommunicationElementTagType tag) {
        return switch (tag) {
            case BODY -> BODY_200DPI.dimension;
            case BANNER_SECONDARY -> BANNER_SECONDARY_200DPI.dimension;
            default -> null;
        };
    }

    public static Dimension get200dpiVersion(TicketTemplateTagType tag) {
        return switch (tag) {
            case BODY -> BODY_200DPI.dimension;
            case BANNER_SECONDARY -> BANNER_SECONDARY_200DPI.dimension;
            default -> null;
        };
    }
}