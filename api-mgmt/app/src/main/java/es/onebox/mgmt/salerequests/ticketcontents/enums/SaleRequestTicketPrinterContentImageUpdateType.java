package es.onebox.mgmt.salerequests.ticketcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.common.ticketcontents.TicketContentItemType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SaleRequestTicketPrinterContentImageUpdateType implements TicketContentItemType, SizeConstrained {

    BANNER_MAIN(456, 112, 153600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    SaleRequestTicketPrinterContentImageUpdateType(Integer width, Integer height, Integer size) {
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
