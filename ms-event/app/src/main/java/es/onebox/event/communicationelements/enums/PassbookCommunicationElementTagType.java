package es.onebox.event.communicationelements.enums;

public enum PassbookCommunicationElementTagType {
    STRIP(ElementType.IMAGE),
    TITLE(ElementType.TEXT),
    ADDITIONAL_DATA_1(ElementType.TEXT),
    ADDITIONAL_DATA_2(ElementType.TEXT),
    ADDITIONAL_DATA_3(ElementType.TEXT);


    private final ElementType type;

    private PassbookCommunicationElementTagType(ElementType type) {
        this.type = type;
    }
    
    public boolean isText() {
        return ElementType.TEXT.equals(this.type);
    }
    
    public boolean isImage() {
        return ElementType.IMAGE.equals(this.type);
    }
    
    private enum ElementType{
        TEXT,
        IMAGE
    }
    
}
