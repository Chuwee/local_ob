package es.onebox.internal.automaticsales.report.enums;

import es.onebox.core.file.exporter.generator.model.FileCode;

import java.util.stream.Stream;

public enum AutomaticSalesFields implements FileCode {
    GROUP("group"),
    NUM("num"),
    NAME("name"),
    FIRST_SURNAME("first_surname"),
    SECOND_SURNAME("second_surname"),
    DNI("dni"),
    PHONE("phone"),
    EMAIL("email"),
    SECTOR("sector"),
    PRICE_ZONE("price_zone"),
    OWNER("owner"),
    SEAT_ID("seat_id"),
    ORIGINAL_LOCATOR("original_locator"),
    LANGUAGE("language"),
    PROCESSED("processed"),
    ERROR_CODE("error_code"),
    ERROR_DESCRIPTION("error_description"),
    ORDER_ID("order_id"),
    TRACE_ID("trace_id"),
    EXTRA_FIELD("extra_field");

    private final String code;

    AutomaticSalesFields(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static AutomaticSalesFields getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
