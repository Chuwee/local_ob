package es.onebox.mgmt.common.ticketcontents;

public enum PriceTypeTicketContentTextPDFType implements TicketContentItemType {
    TITLE(ElementType.TEXT),
    SUBTITLE(ElementType.TEXT),
    TERMS(ElementType.TEXT),
    ADDITIONAL_DATA(ElementType.TEXT),
    BODY(ElementType.IMAGE),
    EVENT_LOGO(ElementType.IMAGE),
    BANNER_MAIN(ElementType.IMAGE),
    BANNER_SECONDARY(ElementType.IMAGE),
    STRIP(ElementType.IMAGE);

    private final ElementType type;

    PriceTypeTicketContentTextPDFType(ElementType type) {
        this.type = type;
    }

    public boolean isText() {
        return ElementType.TEXT.equals(this.type);
    }

    public boolean isImage() {
        return ElementType.IMAGE.equals(this.type);
    }

    public ElementType getType() {
        return type;
    }

    public enum ElementType {
        TEXT,
        IMAGE
    }

    @Override
    public String getTag() {
        return name();
    }
}
