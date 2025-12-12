package es.onebox.eci.promotions.converter;

import es.onebox.eci.promotions.dto.Promotion;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PromotionConverter {
    private PromotionConverter() {
    }

    public static List<Promotion> convert(Stream<es.onebox.common.datasources.catalog.dto.common.Promotion> promotions) {
        if (promotions == null) {
            return Collections.emptyList();
        }
        return promotions.map(PromotionConverter::convert).collect(Collectors.toList());
    }

    public static Promotion convert(es.onebox.common.datasources.catalog.dto.common.Promotion prom) {
        Promotion promotion = new Promotion();
        promotion.setIdentifier(String.valueOf(prom.getId()));
        promotion.setName(prom.getName());
        return promotion;
    }
}
