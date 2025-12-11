package es.onebox.mgmt.channels.ticketcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.validation.annotation.ImageContent;

@ImageContent
public enum ChannelTicketPassbookImageContentType implements SizeConstrained {

    LOGO(350,60,10000),
    STRIP(640, 168, 153600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    ChannelTicketPassbookImageContentType(Integer width, Integer height, Integer size) {
        this.width = width;
        this.height = height;
        this.size = size;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public Integer getSize() {
        return size;
    }
}
