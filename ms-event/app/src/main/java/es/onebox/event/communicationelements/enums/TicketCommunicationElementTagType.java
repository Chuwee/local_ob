package es.onebox.event.communicationelements.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TicketCommunicationElementTagType {
    HEADER(ElementType.IMAGE), // PDF: pathIMAGECabecera
    BODY(ElementType.IMAGE), // PDF: pathIMAGECuerpo
    EVENT_LOGO(ElementType.IMAGE), // PDF: pathIMAGELogo
    BANNER_MAIN(ElementType.IMAGE), // PDF: pathIMAGEBanner1 - ZPL: pathIMAGEBanner2
    BANNER_SECONDARY(ElementType.IMAGE), // PDF: pathIMAGEBanner2 - ZPL: pathIMAGEBanner2
    BANNER_CHANNEL_LOGO(ElementType.IMAGE), // PDF: pathIMAGEBanner3
    TITLE(ElementType.TEXT), // subtitulo1
    SUBTITLE(ElementType.TEXT), // subtitulo2
    TERMS(ElementType.TEXT), // terminos
    ADDITIONAL_DATA(ElementType.TEXT);// otrosDatos
    
    
    private final ElementType type;

    private TicketCommunicationElementTagType(ElementType type) {
        this.type = type;
    }
    
    public boolean isText() {
        return ElementType.TEXT.equals(this.type);
    }
    
    public boolean isImage() {
        return ElementType.IMAGE.equals(this.type);
    }

    public static Set<TicketCommunicationElementTagType> getImageTags() {
        return Stream.of(TicketCommunicationElementTagType.values())
                .filter(TicketCommunicationElementTagType::isImage).collect(Collectors.toSet());
    }

    public static Set<TicketCommunicationElementTagType> getTextTags() {
        return Stream.of(TicketCommunicationElementTagType.values())
                .filter(TicketCommunicationElementTagType::isText).collect(Collectors.toSet());
    }
    
    private enum ElementType{
        TEXT,
        IMAGE
    }
    
}
