package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

public enum UpdateRenewalErrorReason {
    USER_HAS_NOT_RENEWALS,
    USER_HAS_NOT_RENEWALS_FOR_THIS_SEASON_TICKET,
    RENEWAL_PRODUCT_NOT_FOUND,
    RENEWAL_ALREADY_RENEWED,
    INVALID_SEAT,
    INVALID_RATE
}
