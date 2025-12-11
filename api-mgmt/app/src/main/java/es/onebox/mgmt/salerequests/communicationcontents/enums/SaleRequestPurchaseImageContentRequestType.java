package es.onebox.mgmt.salerequests.communicationcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum SaleRequestPurchaseImageContentRequestType implements SizeConstrained {

    CHANNEL_BANNER(300, 242, 103714),
    CHANNEL_HEADER_BANNER(670, 56, 53600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    SaleRequestPurchaseImageContentRequestType(Integer width, Integer height, Integer size) {
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
