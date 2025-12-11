package es.onebox.mgmt.common.ticketcontents;

import es.onebox.mgmt.common.SizeConstrained;

public enum TicketContentImagePassbookType implements SizeConstrained, TicketContentItemType {

    STRIP(640, 168, 153600);

    private final Integer height;
    private final Integer width;
    private final Integer size;

    TicketContentImagePassbookType(Integer width, Integer height, Integer size) {
        this.height = height;
        this.width = width;
        this.size = size;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    @Override
    public Integer getSize() {
        return this.size;
    }

    @Override
    public String getTag() {
        return name();
    }

}
