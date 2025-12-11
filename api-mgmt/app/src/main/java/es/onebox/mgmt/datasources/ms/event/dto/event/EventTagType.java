package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.Arrays;

public enum EventTagType {
    TEXT_TITLE_WEB(1, ElementType.TEXT, 1),
    TEXT_SUBTITLE_WEB(2, ElementType.TEXT, 2),
    TEXT_LENGTH_WEB(3, ElementType.TEXT, 3),
    TEXT_SUMMARY_WEB(4, ElementType.TEXT, 4),
    TEXT_BODY_WEB(5, ElementType.TEXT, 5),
    TEXT_LOCATION_WEB(6, ElementType.TEXT, 6),
    LOGO_WEB(7, ElementType.LOGO, 1),
    IMG_BODY_WEB(8, ElementType.LOGO, 2),
    IMG_BANNER_WEB(9, ElementType.LOGO, 3),
    IMG_CARD_WEB(10, ElementType.LOGO, 4),
    IMG_SQUARE_BANNER_WEB(12, ElementType.LOGO, 5);

    private final Integer id;
    private final ElementType elementType;
    private final Integer order;

    private EventTagType(Integer id, ElementType elementType, Integer order) {
        this.id = id;
        this.elementType = elementType;
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public Integer getOrder() {
        return order;
    }

    public static EventTagType getTagTypeById(final Integer id) {
        return Arrays.stream(values()).filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean isImage() {
        return this.elementType.equals(ElementType.LOGO);
    }

    public boolean isText() {
        return this.elementType.equals(ElementType.TEXT);
    }
}
