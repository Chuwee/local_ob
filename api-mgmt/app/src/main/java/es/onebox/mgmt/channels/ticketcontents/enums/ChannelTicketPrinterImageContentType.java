package es.onebox.mgmt.channels.ticketcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum ChannelTicketPrinterImageContentType implements SizeConstrained {

    BANNER_MAIN(456,112,153600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    ChannelTicketPrinterImageContentType(Integer width, Integer height, Integer size) {
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
