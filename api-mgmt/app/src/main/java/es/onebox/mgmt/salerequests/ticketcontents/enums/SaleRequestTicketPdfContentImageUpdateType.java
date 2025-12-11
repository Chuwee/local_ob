package es.onebox.mgmt.salerequests.ticketcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.common.ticketcontents.TicketContentItemType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SaleRequestTicketPdfContentImageUpdateType implements TicketContentItemType, SizeConstrained {

    HEADER(1076, 56, 25600),
    BANNER_SECONDARY(520, 420, 61440);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    SaleRequestTicketPdfContentImageUpdateType(Integer width, Integer height, Integer size) {
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

    public static List<String> getNamesList() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public String getTag() {
        return name();
    }
}
