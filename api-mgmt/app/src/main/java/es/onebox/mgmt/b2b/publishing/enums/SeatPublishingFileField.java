package es.onebox.mgmt.b2b.publishing.enums;

import java.util.stream.Stream;

public enum SeatPublishingFileField {

    EVENT_NAME("event.name"),
    EVENT_ID("event.id"),

    SESSION_NAME("session.name"),
    SESSION_ID("session.id"),
    SESSION_DATE("session.date"),

    CHANNEL_NAME("channel.name"),
    CHANNEL_ID("channel.id"),

    VENUE_NAME("venue.name"),

    SECTOR_NAME("sector.name"),

    ROW_NAME("row.name"),

    SEAT_NAME("seat.name"),

    TRANSACTION_TYPE("transaction.type"),
    TRANSACTION_DATE("transaction.date"),

    CLIENT_NAME("client.name"),
    CLIENT_ID("client.id"),
    CLIENT_ENTITY_ID("client.entityId"),

    USERNAME("user.name"),
    USER_ID("user.id"),
    PUBLISHER_TYPE("user.publisherType"),

    BASE_PRICE("price.base"),
    FINAL_PRICE("price.final"),

    PROMO_IS_AUTOMATIC("promo.isAutomatic"),
    PROMO_PROMOTION("promo.promotion"),
    PROMO_DISCOUNT("promo.discount"),
    PROMO_CHANNEL_AUTOMATIC("promo.channel.isAutomatic"),
    PROMO_CHANNEL_COLLECTIVE("promo.channel.collective"),

    CHARGE_CHANNEL("charge.channel"),
    CHARGE_PROMOTER("charge.promoter"),
    CHARGE_PROMOTER_CHANNEL("charge.promoterChannel"),
    CHARGE_REALLOCATION("charge.reallocation"),

    FEE_IDS("fee.ids"),
    FEE_NAMES("fee.names"),
    FEE_IS_UNITARY("fee.isUnitary"),
    FEE_VALUES("fee.values"),

    COMMISSION_IDS("commission.ids"),
    COMMISSION_NAMES("commission.names"),
    COMMISSION_VALUES("commission.values"),

    ORDER_CODE("order.code"),
    ORDER_DATE("order.date");

    private final String code;

    SeatPublishingFileField(String code) { this.code = code; }

    public String getCode() {
        return this.code;
    }

    public static SeatPublishingFileField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}