package es.onebox.mgmt.channels.ticketcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.validation.annotation.ImageContent;

@ImageContent
public enum ChannelTicketPDFImageContentType implements SizeConstrained {

    HEADER(1076,56,25000),
    BANNER_SECONDARY(520,420,61440);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    ChannelTicketPDFImageContentType(Integer width, Integer height, Integer size) {
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
