package es.onebox.mgmt.vouchers.enums;

import es.onebox.mgmt.common.channelcontents.BaseChannelContentImageType;

import java.util.Arrays;

public enum VoucherGiftCardContentImageType implements BaseChannelContentImageType {

    CARD_DESIGN_1("PURCHASE_CARD_DESIGN_1",793, 500, 92160),
    CARD_DESIGN_2("PURCHASE_CARD_DESIGN_2",793, 500, 92160),
    CARD_DESIGN_3("PURCHASE_CARD_DESIGN_3",793, 500, 92160);

    private static final long serialVersionUID = 1L;

    private final String internalName;
    private final Integer width;
    private final Integer height;
    private final Integer size;

    private VoucherGiftCardContentImageType(String internalName, Integer width, Integer height, Integer size) {
        this.internalName = internalName;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public String getInternalName() {
        return internalName;
    }

    @Override
    public Integer getTagId() {
        return null;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public Integer getSize() {
        return this.size;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    public static VoucherGiftCardContentImageType getByInternalName(String n) {
        return Arrays.stream(VoucherGiftCardContentImageType.values()).filter(v -> v.getInternalName().equals(n)).findFirst().orElse(null);
    }

}
