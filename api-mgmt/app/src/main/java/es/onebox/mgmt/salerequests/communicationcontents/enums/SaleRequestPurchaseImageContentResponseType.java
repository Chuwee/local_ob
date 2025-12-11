package es.onebox.mgmt.salerequests.communicationcontents.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum SaleRequestPurchaseImageContentResponseType implements SizeConstrained {

    PROMOTER_BANNER(300, 242, 153600),
    CHANNEL_BANNER(300, 242, 103714),
    CHANNEL_HEADER_BANNER(670, 56, 53600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    SaleRequestPurchaseImageContentResponseType(Integer width, Integer height, Integer size) {
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
