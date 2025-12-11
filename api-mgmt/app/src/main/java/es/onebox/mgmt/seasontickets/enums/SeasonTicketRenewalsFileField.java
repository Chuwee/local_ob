package es.onebox.mgmt.seasontickets.enums;

import java.util.stream.Stream;

public enum SeasonTicketRenewalsFileField {

    ID("id"),
    USER_ID("user_id"),
    MEMBER_ID("member_id"),
    PRODUCT_CLIENT_ID("product_client_id"),
    EMAIL("email"),
    NAME("name"),
    SURNAME("surname"),
    BIRTHDAY("birthday"),
    PHONE_NUMBER("phone_number"),
    SEASON_TICKET_ID("season_ticket_id"),
    SEASON_TICKET_NAME("season_ticket_name"),
    POSTAL_CODE("postal_code"),
    GENDER("gender"),
    LANGUAGE("language"),
    COUNTRY("country"),
    COUNTRY_SUBDIVISION("country_subdivision"),
    CITY("city"),
    ID_CARD("id_card"),
    SIGN_UP_DATE("sign_up_date"),
    ADDRESS("address"),
    ENTITY_ID("entity_id"),

    HISTORIC_SEAT_TYPE("historic_seat.seat_type"),
    HISTORIC_SEAT_NOT_NUMBERED_ZONE_ID("historic_seat.not_numbered_zone_id"),
    HISTORIC_SEAT_SECTOR_ID("historic_seat.sector_id"),
    HISTORIC_SEAT_ROW_ID("historic_seat.row_id"),
    HISTORIC_SEAT_SEAT_ID("historic_seat.seat_id"),
    HISTORIC_SEAT_SECTOR("historic_seat.sector"),
    HISTORIC_SEAT_ROW("historic_seat.row"),
    HISTORIC_SEAT_SEAT("historic_seat.seat"),
    HISTORIC_SEAT_PRICE_ZONE("historic_seat.price_zone"),
    HISTORIC_SEAT_NOT_NUMBERED_ZONE("historic_seat.not_numbered_zone"),

    HISTORIC_RATE("historic_rate"),
    HISTORIC_RATE_ID("historic_rate_id"),

    ACTUAL_SEAT_TYPE("actual_seat.seat_type"),
    ACTUAL_SEAT_NOT_NUMBERED_ZONE_ID("actual_seat.not_numbered_zone_id"),
    ACTUAL_SEAT_SECTOR_ID("actual_seat.sector_id"),
    ACTUAL_SEAT_ROW_ID("actual_seat.row_id"),
    ACTUAL_SEAT_SEAT_ID("actual_seat.seat_id"),
    ACTUAL_SEAT_SECTOR("actual_seat.sector"),
    ACTUAL_SEAT_ROW("actual_seat.row"),
    ACTUAL_SEAT_SEAT("actual_seat.seat"),
    ACTUAL_SEAT_PRICE_ZONE("actual_seat.price_zone"),
    ACTUAL_SEAT_NOT_NUMBERED_ZONE("actual_seat.not_numbered_zone"),

    ACTUAL_RATE("actual_rate"),
    ACTUAL_RATE_ID("actual_rate_id"),

    MAPPING_STATUS("mapping_status"),
    RENEWAL_STATUS("renewal_status"),
    RENEWALS_SETTINGS_ENABLE("renewal_settings.enable"),
    RENEWALS_SETTINGS_START_DATE("renewal_settings.start_date"),
    RENEWALS_SETTINGS_END_DATE("renewal_settings.end_date"),
    RENEWALS_SETTINGS_IN_PROCESS("renewal_settings.in_process"),
    BALANCE("balance"),
    AUTO_RENEWAL("auto_renewal");

    private final String code;

    SeasonTicketRenewalsFileField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static SeasonTicketRenewalsFileField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
