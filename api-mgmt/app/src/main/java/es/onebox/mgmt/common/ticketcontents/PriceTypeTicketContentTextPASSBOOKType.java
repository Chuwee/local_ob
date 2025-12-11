package es.onebox.mgmt.common.ticketcontents;

public enum PriceTypeTicketContentTextPASSBOOKType implements TicketContentItemType {
    TITLE(ElementType.TEXT),
    ADDITIONAL_DATA_1(ElementType.TEXT),
    ADDITIONAL_DATA_2(ElementType.TEXT),
    ADDITIONAL_DATA_3(ElementType.TEXT);

    private final ElementType type;

    PriceTypeTicketContentTextPASSBOOKType(ElementType type) {
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
