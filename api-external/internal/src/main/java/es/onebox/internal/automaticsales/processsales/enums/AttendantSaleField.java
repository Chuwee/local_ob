package es.onebox.internal.automaticsales.processsales.enums;

import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public enum AttendantSaleField {

    NAME("firstName", "ATTENDANT_NAME", List.of(SaleDTO::getName)),
    SURNAME("lastName", "ATTENDANT_SURNAME", List.of(SaleDTO::getFirstSurname)),
    DNI("identification", "ATTENDANT_ID_NUMBER", List.of(SaleDTO::getDni)),
    PHONE_NUMBER("telephone", "ATTENDANT_CELLPHONE", List.of(SaleDTO::getPhone)),
    EMAIL("email", "ATTENDANT_MAIL", List.of(SaleDTO::getEmail));

    private final String channelField;
    private final String eventField;
    private final List<Function<SaleDTO, String>> extractors;

    AttendantSaleField(String channelField, String eventField, List<Function<SaleDTO, String>> extractors) {
        this.channelField = channelField;
        this.eventField = eventField;
        this.extractors = extractors;
    }

    public static AttendantSaleField fromChannelFieldName(String channelFieldName) {
        return Stream.of(values())
                .filter(field -> field.getChannelField().equals(channelFieldName))
                .findFirst()
                .orElse(null);
    }

    public static AttendantSaleField fromEventFieldName(String eventFieldName) {
        return Stream.of(values())
                .filter(field -> field.getEventField().equals(eventFieldName))
                .findFirst()
                .orElse(null);
    }

    public String getChannelField() {
        return channelField;
    }

    public String getEventField() {
        return eventField;
    }

    public List<Function<SaleDTO, String>> getExtractors() {
        return extractors;
    }
}
