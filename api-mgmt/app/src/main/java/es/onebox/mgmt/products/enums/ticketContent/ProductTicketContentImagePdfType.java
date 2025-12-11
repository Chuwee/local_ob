package es.onebox.mgmt.products.enums.ticketContent;

import java.io.Serializable;

public enum ProductTicketContentImagePdfType implements Serializable {

    BODY(360, 430, 51200);

    private final Integer height;
    private final Integer width;
    private final Integer size;

    ProductTicketContentImagePdfType(Integer width, Integer height, Integer size) {
        this.height = height;
        this.width = width;
        this.size = size;
    }

    public Integer getHeight() {
        return this.height;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getSize() {
        return this.size;
    }
}
