package es.onebox.mgmt.products.enums.ticketContent;

import java.io.Serializable;

public enum ProductTicketContentImagePassbookType implements Serializable {

    THUMBNAIL(680, 370, 92160);


    private final Integer height;
    private final Integer width;
    private final Integer size;

    ProductTicketContentImagePassbookType(Integer height, Integer width, Integer size) {
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
