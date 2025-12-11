package es.onebox.event.packs.enums;


import es.onebox.event.common.enums.ElementType;

public enum PackTicketContentTagType {
    BODY(ElementType.IMAGEN), // PDF: pathIMAGECuerpo
    BANNER_MAIN(ElementType.IMAGEN), // PDF: pathIMAGEBanner1 - ZPL: pathIMAGEBanner2
    BANNER_SECONDARY(ElementType.IMAGEN), // PDF: pathIMAGEBanner2 - ZPL: pathIMAGEBanner2
    TITLE(ElementType.TEXT), // subtitulo1
    SUBTITLE(ElementType.TEXT), // subtitulo2
    TERMS(ElementType.TEXT), // terminos
    ADDITIONAL_DATA(ElementType.TEXT); // otrosDatos

    private final ElementType elementType;

    private PackTicketContentTagType(ElementType elementType) {
        this.elementType = elementType;
    }

    public ElementType getElementType() {
        return elementType;
    }

}
