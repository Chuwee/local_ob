package es.onebox.event.catalog.dto.promotion;

import java.util.Arrays;

public enum CatalogPromotionPriceVariationType {
    FIXED,
    PERCENTAGE,
    NEW_BASE_PRICE;


    public static CatalogPromotionPriceVariationType fromName(String name) {
        if (name == null) return null;
        return Arrays.stream(CatalogPromotionPriceVariationType.values()).filter(v -> v.name().equals(name)).findFirst().orElse(null);
    }
}
