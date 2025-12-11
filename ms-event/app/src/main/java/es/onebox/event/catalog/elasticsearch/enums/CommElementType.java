package es.onebox.event.catalog.elasticsearch.enums;

public enum CommElementType {

    PROMOTER_BANNER(true),
    CHANNEL_BANNER(true),
    CHANNEL_BANNER_LINK(false),
    HEADER_BANNER(true),
    IMG_SQUARE_BANNER_WEB(true);

    private final boolean image;

    CommElementType(boolean image) {
        this.image = image;
    }

    public boolean isImage() {
        return image;
    }

    public static boolean isImage(CommElementType type) {
        return type.isImage();
    }
}
