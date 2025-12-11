package es.onebox.event.catalog.dto.promotion;

import java.util.Arrays;

public enum CatalogPromotionCollectiveValidationMethod {

    USER,
    USER_PASSWORD,
    PROMOTIONAL_CODE,
    GIFT_TICKET;

    public static CatalogPromotionCollectiveValidationMethod fromString(String in) {
        if (in == null) return null;
        return Arrays.stream(CatalogPromotionCollectiveValidationMethod.values()).filter(s -> s.name().equals(in)).findFirst().orElse(null);
    }
}
