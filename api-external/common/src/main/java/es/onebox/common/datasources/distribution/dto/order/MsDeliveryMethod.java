package es.onebox.common.datasources.distribution.dto.order;

public enum MsDeliveryMethod {
    PRINT_AT_HOME(false),
    TAQ_PICKUP(false),
    PRINT_EXPRESS(false),
    PHONE(false),
    EXTERNAL_CHANNEL(true),
    NATIONAL_POST_DELIVERY(false),
    INTERNATIONAL_POST_DELIVERY(false),
    WHATSAPP(false);

    private Boolean exclude;

    MsDeliveryMethod(Boolean exclude) {
        this.exclude = exclude;
    }

    public Boolean getExclude() {
        return exclude;
    }

}
