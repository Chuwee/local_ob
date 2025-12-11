package es.onebox.event.priceengine.simulation.converter;

import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.PromotionPricesRange;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.domain.PromotionsEvent;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PromotionsEventConvert {

    private static final String INTERNAL_DELIMITER = "::";
    private static final String GROUP_DELIMITER = "\\|\\|";

    private static final Integer AUTOMATIC_TYPE = 1;
    private static final Integer PROMOTION_TYPE = 2;
    private static final Integer  DISCOUNT_TYPE = 3;

    private PromotionsEventConvert() {throw new UnsupportedOperationException("Cannot instantiate convert class");}

    public static PromotionsEvent convertToPromotionsEvent(List<EventPromotionRecord> promotionsRecord) {
        List<Promotion> listPromotions = promotionsRecord.stream().map(PromotionsEventConvert::convertToPromotion).toList();
        PromotionsEvent promotionsEvent = new PromotionsEvent();
        promotionsEvent.setNotCumulative(getNotCumulativePromotions(listPromotions));
        promotionsEvent.setDiscounts(getPromotionsByType(DISCOUNT_TYPE, listPromotions));
        promotionsEvent.setPromotions(getPromotionsByType(PROMOTION_TYPE, listPromotions));
        promotionsEvent.setAutomatics(getPromotionsByType(AUTOMATIC_TYPE, listPromotions));
        return promotionsEvent;
    }

    private static List<Promotion> getPromotionsByType(Integer type, List<Promotion> listPromotions) {
        return listPromotions.stream()
                .filter(item -> BooleanUtils.isFalse(item.getNotCumulative()) && item.getSubtype().equals(type))
                .toList();
    }

    private static List<Promotion> getNotCumulativePromotions(List<Promotion> listPromotions) {
        return listPromotions.stream()
                .filter(item -> BooleanUtils.isTrue(item.getNotCumulative()))
                .toList();
    }

    private static Promotion convertToPromotion(EventPromotionRecord record) {
        Promotion promo = new Promotion();
        promo.setId(record.getEventPromotionTemplateId());
        promo.setActive(record.getActive());
        promo.setApplyChannelSpecificCharges(record.getApplyChannelSpecificCharges());
        promo.setApplyPromoterSpecificCharges(record.getApplyPromoterSpecificCharges());
        promo.setDiscountType(record.getDiscountType());
        promo.setFixedDiscountValue(record.getFixedDiscountValue());
        promo.setName(record.getName());
        promo.setNotCumulative(record.getNotCumulative());
        promo.setPercentualDiscountValue(record.getPercentualDiscountValue());
        promo.setPriceZones(getListOfIds(record.getPriceZones()));
        promo.setRanges(getRanges(record.getRanges()));
        promo.setSessions(record.getSessions());
        promo.setRates(getListOfIds(record.getRates()));
        promo.setSelectedChannels(record.getSelectedChannels());
        promo.setSelectedPriceZones(record.getSelectedPriceZones());
        promo.setSelectedRates(record.getSelectedRates());
        promo.setSelectedSessions(record.getSelectedSessions());
        promo.setStatus(record.getStatus());
        promo.setSubtype(record.getSubtype());
        return promo;
    }

    private static List<Integer> getListOfIds(String priceZones) {
        return Objects.isNull(priceZones) ? null : Arrays.stream(priceZones.split(GROUP_DELIMITER))
                                                            .map(Integer::valueOf)
                .toList();
    }

    private static List<PromotionPricesRange> getRanges(String ranges) {
        String[] rangeItems = ranges != null ? ranges.split(GROUP_DELIMITER) : null;
        if (rangeItems != null) {
            return Stream.of(rangeItems)
                    .map(r -> {
                                String[] data = r.split(INTERNAL_DELIMITER);
                                return new PromotionPricesRange(Double.valueOf(data[0]), Double.valueOf(data[2]));
                    }).toList()
                    .stream()
                    .sorted((v1, v2) -> Double.compare(v2.getFrom(), v1.getFrom()))
                    .toList();
        }
        return null;
    }
}
