package es.onebox.mgmt.tickettemplates.enums;

public enum DesignType {
    PDF(1),
    ZPL_GENERAL(2),
    ZPL_IVA(3),
    ZPL_PRICE_ZONE(4),
    ZPL_WONDERLAND(5),
    ZPL_PRICE_ZONE_NO_CHANNEL(8),
    PRODUCT(13);

    private static final long serialVersionUID = 1L;

    private final Integer value;

    DesignType(Integer value) {
        this.value = value;
    }


    public Integer getValue() {
        return value;
    }
}
