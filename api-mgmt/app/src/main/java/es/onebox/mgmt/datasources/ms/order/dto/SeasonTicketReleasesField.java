package es.onebox.mgmt.datasources.ms.order.dto;

import java.util.stream.Stream;

public enum SeasonTicketReleasesField {

    NAME("name"),
    SURNAME("surname"),
    EMAIL("email"),
    GENDER("gender"),
    BIRTHDAY("birthday"),
    ID_CARD("id_card"),
    PHONE_NUMBER("phone_number"),
    ADDRESS("address"),
    CITY("city"),
    POSTAL_CODE("postal_code"),
    COUNTRY_SUBDIVISION("country_subdivision"),
    COUNTRY("country"),
    LANGUAGE("language"),
    SIGN_UP_DATE("sign_up_date"),

    MEMBER_ID("member_id"),
    PRODUCT_CLIENT_ID("product_client_id"),
    MANAGER_EMAIL("manager_email"),

    RELEASE_STATUS("release.status"),
    RELEASE_PRICE("release.price"),
    RELEASE_PERCENTAGE("release.percentage"),
    RELEASE_EARNINGS("release.earnings"),

    ORDER_CODE("order.code"),
    ORDER_DATE("order.date"),
    CHANNEL("channel"),

    EVENT_NAME("event_name"),
    SESSION_NAME("session.name"),
    SESSION_DATE("session.date"),
    VENUE("venue"),
    PROMOTER("promoter");

    private final String code;

    SeasonTicketReleasesField(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static SeasonTicketReleasesField getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
