package es.onebox.event.products.enums;

public enum TicketContentImageType {

    BODY(360, 430, 51200),
    THUMBNAIL(322, 320, 92160);

    private final Integer height;
    private final Integer width;
    private final Integer size;

    TicketContentImageType(Integer height, Integer width, Integer size) {
        this.height = height;
        this.width = width;
        this.size = size;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getSize() {
        return size;
    }
}
