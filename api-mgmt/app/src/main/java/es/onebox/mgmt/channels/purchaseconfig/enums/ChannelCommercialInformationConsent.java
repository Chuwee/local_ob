package es.onebox.mgmt.channels.purchaseconfig.enums;

import java.util.Arrays;

public enum ChannelCommercialInformationConsent {
    DO_NOT_REQUEST(Boolean.FALSE, Boolean.FALSE),
    CHECK_TO_ACCEPT(Boolean.TRUE, Boolean.FALSE),
    CHECK_TO_DECLINE(Boolean.TRUE, Boolean.TRUE);

    private final Boolean allowCommercialMailing;
    private final Boolean commercialMailingNegativeAuth;

    ChannelCommercialInformationConsent(Boolean allowCommercialMailing, Boolean commercialMailingNegativeAuth) {
        this.allowCommercialMailing = allowCommercialMailing;
        this.commercialMailingNegativeAuth = commercialMailingNegativeAuth;
    }

    public Boolean isAllowCommercialMailing() {
        return allowCommercialMailing;
    }

    public Boolean isCommercialMailingNegativeAuth() {
        return commercialMailingNegativeAuth;
    }

    public static ChannelCommercialInformationConsent get(Boolean allowCommercialMailing, Boolean commercialMailingNegativeAuth){
        return  Arrays.stream(ChannelCommercialInformationConsent.values())
                .filter(v -> allowCommercialMailing.equals(v.allowCommercialMailing)
                        && commercialMailingNegativeAuth.equals(v.commercialMailingNegativeAuth))
                .findFirst()
                .orElse(null);
    }
}

