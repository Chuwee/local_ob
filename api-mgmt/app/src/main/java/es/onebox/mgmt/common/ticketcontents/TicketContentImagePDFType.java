package es.onebox.mgmt.common.ticketcontents;

import es.onebox.mgmt.common.SizeConstrained;

public enum TicketContentImagePDFType implements SizeConstrained, TicketContentItemType {
    BODY(360, 430, 51200),
    BANNER_MAIN(520, 856, 87040),
    BANNER_SECONDARY(520, 420, 61440);

    private final Integer height;
    private final Integer width;
    private final Integer size;

    TicketContentImagePDFType(Integer width, Integer height, Integer size) {
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
