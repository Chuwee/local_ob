package es.onebox.event.tickettemplates.dto;

import es.onebox.event.common.enums.ElementType;

public enum TicketTemplateTagType {

    HEADER(ElementType.IMAGEN), // PDF: pathImagenCabecera
    BODY(ElementType.IMAGEN), // PDF: pathImagenCuerpo
    EVENT_LOGO(ElementType.IMAGEN), // PDF: pathImagenLogo
    BANNER_MAIN(ElementType.IMAGEN), // PDF: pathImagenBanner1 - ZPL: pathImagenBanner2
    BANNER_SECONDARY(ElementType.IMAGEN), // PDF: pathImagenBanner2 - ZPL: pathIMAGEBanner2
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
        return ElementType.IMAGEN.equals(this.elementType);
    }

}
