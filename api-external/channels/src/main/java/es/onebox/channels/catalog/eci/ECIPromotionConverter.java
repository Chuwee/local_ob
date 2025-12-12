package es.onebox.channels.catalog.eci;

import es.onebox.channels.catalog.eci.dto.ECIPromotionDTO;
import es.onebox.common.datasources.catalog.dto.common.Promotion;
import es.onebox.common.datasources.catalog.dto.common.PromotionCommunicationElements;
import es.onebox.common.datasources.catalog.dto.common.PromotionValidityPeriodType;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

public class ECIPromotionConverter {

    private ECIPromotionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<ECIPromotionDTO> convert(List<Promotion> promotions, List<String> languages) {
        return ECIConverterUtils.map(promotions, promotion -> ECIPromotionConverter.convert(promotion, languages), Comparator.comparing(ECIPromotionDTO::getId));
    }

    private static ECIPromotionDTO convert(Promotion promotion, List<String> languages) {
        if (promotion == null || !isCurrentPromotion(promotion)) {
            return null;
        }
        ECIPromotionDTO eciPromotion = new ECIPromotionDTO();
        eciPromotion.setId(promotion.getId());
        eciPromotion.setName(ECILanguageUtils.getText(promotion.getTexts(), PromotionCommunicationElements::getName, languages));
        eciPromotion.setMultilingualDescription(ECILanguageUtils.getI18nTexts(promotion.getTexts(), PromotionCommunicationElements::getDescription, languages));
        return eciPromotion;
    }

    private static boolean isCurrentPromotion(Promotion promotion) {
        var validityPeriod = promotion.getValidityPeriod();
        if (validityPeriod == null) {
            return true;
        }
        if (validityPeriod.getType() == PromotionValidityPeriodType.DATE_RANGE) {
            return !isPastPeriod(validityPeriod.getTo()) && !isFuturePeriod(validityPeriod.getFrom());
        }
        return true;
    }

    private static boolean isPastPeriod(ZonedDateTime to) {
        return to != null && to.isBefore(ZonedDateTime.now());
    }

    private static boolean isFuturePeriod(ZonedDateTime from) {
        return from != null && from.isAfter(ZonedDateTime.now());
    }
}
