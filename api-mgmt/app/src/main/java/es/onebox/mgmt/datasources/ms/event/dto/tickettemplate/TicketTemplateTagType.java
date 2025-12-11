package es.onebox.mgmt.datasources.ms.event.dto.tickettemplate;

import es.onebox.mgmt.datasources.ms.event.dto.event.ElementType;

public enum TicketTemplateTagType {

    HEADER(ElementType.IMAGEN), // PDF: pathImagenCabecera
    BODY(ElementType.IMAGEN), // PDF-ZPL: pathImagenCuerpo
    EVENT_LOGO(ElementType.IMAGEN), // PDF: pathImagenLogo
    BANNER_MAIN(ElementType.IMAGEN), // PDF: pathImagenBanner1
    BANNER_SECONDARY(ElementType.IMAGEN), // PDF: pathImagenBanner2 - ZPL: pathImagenBanner2
    BANNER_CHANNEL_LOGO(ElementType.IMAGEN), // PDF: pathImagenBanner3
    TERMS_AND_CONDITIONS(ElementType.TEXT);

    private ElementType elementType;

    TicketTemplateTagType(ElementType elementType) {
        this.elementType = elementType;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public boolean isImage() {
        return this.elementType.equals(ElementType.IMAGEN);
    }

    public boolean isText() {
        return this.elementType.equals(ElementType.TEXT);
    }

}
