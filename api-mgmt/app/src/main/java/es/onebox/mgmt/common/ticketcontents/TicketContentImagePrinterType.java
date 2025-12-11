package es.onebox.mgmt.common.ticketcontents;

import es.onebox.mgmt.common.SizeConstrained;

import java.util.stream.Stream;

public enum TicketContentImagePrinterType implements SizeConstrained, TicketContentItemType {
    BODY(624, 696, 153600, "BODY"),
    BANNER_MAIN(456, 112, 153600, "BANNER_SECONDARY");

    private final Integer height;
    private final Integer width;
    private final Integer size;
    private final String tag;

    TicketContentImagePrinterType(Integer width, Integer height, Integer size, String tag) {
        this.height = height;
        this.width = width;
        this.size = size;
        this.tag = tag;
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
        return tag;
    }

    public static TicketContentImagePrinterType getByTag(String tag) {
        return Stream.of(values()).filter(it -> it.tag.equals(tag)).findFirst().orElse(null);
    }
}
