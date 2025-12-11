package es.onebox.event.priceengine.simulation.converter;

import es.onebox.event.priceengine.builders.EventPromotionRecordBuilder;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.PromotionPricesRange;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PromotionUtils {

    private static final String PROMO_2E = "Promo 2€";
    private static final String AUTOMATIC_3E = "Automatic 3€";
    private static final String AUTOMATIC_10_PERCENT = "Automatic 10%";
    private static final String DISCOUNT_NEW_PRICE = "Discount New price";
    private static final String AUTOMATIC_NEW_PRICE = "Automatic New price";
    private static final String AUTOMATIC_NBP_NAME = "AUTO NBP";
    private static final String AUTOMATIC_NBP_HIGHER_NAME = "AUTO NBP Higher";
    private static final String AUTOMATIC_FIXED_NAME = "AUTO FIXED";
    private static final String AUTOMATIC_PERCENTAGE_NAME = "AUTO PERCENTAGE";
    private static final String DISCOUNT_NBP_NAME = "PLUS NBP";
    private static final String DISCOUNT_NBP_HIGHER_NAME = "PLUS NBP Higher";
    private static final String DISCOUNT_FIXED_NAME = "PLUS FIXED";
    private static final String DISCOUNT_PERCENTAGE_NAME = "PLUS PERCENTAGE";
    private static final String PROMOTION_FIXED_NAME = "BASIC FIXED";
    private static final String PROMOTION_PERCENTAGE_NAME = "BASIC PERCENTAGE";
    private static final String PROMOTION_NEGATIVE_NAME = "BASIC NEGATIVE";

    private static final Integer TYPE_DISCOUNT= 3;
    private static final Integer TYPE_PROMOTION = 2;
    private static final Integer TYPE_AUTOMATIC = 1;

    private static final Integer FIXED_DISCOUNT = 0;
    private static final Integer PERCENTAGE_DISCOUNT = 1;
    private static final Integer NEW_PRICE_DISCOUNT = 2;


    private static final String INTERNAL_DELIMITER = "::";
    private static final String GROUP_DELIMITER = "\\|\\|";

    private PromotionUtils(){throw new UnsupportedOperationException("Cannot instantiate utility class");}

    public static EventPromotionRecord getDiscount1E() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(1D)
                .withName("Discount 1€")
                .withNotCumulative(false)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(FIXED_DISCOUNT)
                .build();
    }

    public static EventPromotionRecord getPromo2E() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(2D)
                .withName(PROMO_2E)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .build();

    }

    public static EventPromotionRecord getPromo2EDifferentChannel() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(2D)
                .withName(PROMO_2E)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withSelectedChannels(1)
                .withChannels("123")
                .build();
    }

    public static EventPromotionRecord getPromo2EDifferentPriceZone() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(2D)
                .withName(PROMO_2E)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withSelectedPriceZones(1)
                .withPriceZones("123")
                .build();
    }

    public static EventPromotionRecord getPromo2EDifferentRate() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(2D)
                .withName(PROMO_2E)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withSelectedRates(1)
                .withRates("123")
                .build();
    }

    public static EventPromotionRecord getPromo2ESpecificRate() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(2D)
                .withName(PROMO_2E)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withSelectedRates(1)
                .withRates("2")
                .build();
    }

    public static EventPromotionRecord getAuto10Percent() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withPercentualDiscountValue(10.0)
                .withName(AUTOMATIC_10_PERCENT)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(PERCENTAGE_DISCOUNT)
                .withNotCumulative(false)
                .build();
    }

    public static EventPromotionRecord getAuto10PercentNoCumulative() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withFixedDiscountValue(3D)
                .withName(AUTOMATIC_3E)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(true)
                .build();
    }

    public static EventPromotionRecord getAutoNewPrice() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_NEW_PRICE)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::10::4.88||10::0::8.88")
                .build();
    }

    public static EventPromotionRecord getAutoNewPriceBig() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_NEW_PRICE)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::10::50||10::0::88")
                .build();
    }

    public static EventPromotionRecord getAutoNewPrice2() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_NEW_PRICE)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::10::3||10::0::8")
                .build();
    }

    public static EventPromotionRecord getDiscountNewPrice() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(DISCOUNT_NEW_PRICE)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::10::5||10::0::9")
                .build();
    }

    public static EventPromotionRecord getAutomaticNBP() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_NBP_NAME)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::11::5||11::0::9")
                .build();
    }

    public static EventPromotionRecord getAutomaticNBPHigher() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_NBP_HIGHER_NAME)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::11::50||11::0::60")
                .build();
    }

    public static EventPromotionRecord getAutomaticFixed() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_FIXED_NAME)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withFixedDiscountValue(2.0)
                .build();
    }

    public static EventPromotionRecord getAutomaticPercentage() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(AUTOMATIC_PERCENTAGE_NAME)
                .withSubtype(TYPE_AUTOMATIC)
                .withDiscountType(PERCENTAGE_DISCOUNT)
                .withNotCumulative(false)
                .withPercentualDiscountValue(50.0)
                .build();
    }

    public static EventPromotionRecord getDiscountNBP() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(DISCOUNT_NBP_NAME)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::11::4||11::0::8")
                .build();
    }

    public static EventPromotionRecord getDiscountNBPHigher() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(DISCOUNT_NBP_HIGHER_NAME)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(NEW_PRICE_DISCOUNT)
                .withNotCumulative(false)
                .withRanges("0::11::40||11::0::45")
                .build();
    }

    public static EventPromotionRecord getDiscountFixed() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(DISCOUNT_FIXED_NAME)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(FIXED_DISCOUNT)
                .withNotCumulative(false)
                .withFixedDiscountValue(8.0)
                .build();
    }

    public static EventPromotionRecord getDiscountPercentage() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(DISCOUNT_PERCENTAGE_NAME)
                .withSubtype(TYPE_DISCOUNT)
                .withDiscountType(PERCENTAGE_DISCOUNT)
                .withNotCumulative(false)
                .withPercentualDiscountValue(20.0)
                .build();
    }

    public static EventPromotionRecord getPromotionFixed() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(PROMOTION_FIXED_NAME)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withFixedDiscountValue(5.0)
                .withNotCumulative(false)
                .build();
    }

    public static EventPromotionRecord getPromotionPercentage() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(PROMOTION_PERCENTAGE_NAME)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(PERCENTAGE_DISCOUNT)
                .withPercentualDiscountValue(30.0)
                .withNotCumulative(false)
                .build();
    }

    public static EventPromotionRecord getPromotionNegative() {
        return EventPromotionRecordBuilder.buider()
                .withEventPromotionTemplateId(8)
                .withpromotionTemplateId(8)
                .withName(PROMOTION_NEGATIVE_NAME)
                .withSubtype(TYPE_PROMOTION)
                .withDiscountType(FIXED_DISCOUNT)
                .withFixedDiscountValue(-10.0)
                .withNotCumulative(false)
                .build();
    }
    
    public static List<List<Promotion>> getCombinesOfDiscountAndPromo() {
        List<List<Promotion>> listPromotions = new ArrayList<>();
        listPromotions.add(Collections.singletonList(convertToPromotion(getDiscount1E())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getPromo2E())));
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(convertToPromotion(getDiscount1E()));
        promotions.add(convertToPromotion(getPromo2E()));
        listPromotions.add(promotions);
        return listPromotions;
    }

    public static List<List<Promotion>> getCombinesOfAutomaticAndDiscountAndPromo() {
        List<List<Promotion>> listPromotions = new ArrayList<>();
        //Single combine
        listPromotions.add(Collections.singletonList(convertToPromotion(getAuto10Percent())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getDiscount1E())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getPromo2E())));
        //Pair combine
        listPromotions.add(getCombineOf(getAuto10Percent(), getDiscount1E()));
        listPromotions.add(getCombineOf(getAuto10Percent(), getPromo2E()));
        listPromotions.add(getCombineOf(getDiscount1E(), getPromo2E()));
        //Combines of three
        listPromotions.add(getCombineOf(getAuto10Percent(), getDiscount1E(), getPromo2E()));
        return listPromotions;
    }

    public static List<List<Promotion>> getCombinesOfAutomaticAndDiscountAndPromoAndAutoNotCumulative() {
        List<List<Promotion>> listPromotions = new ArrayList<>();
        //Not cumulative
        listPromotions.add(Collections.singletonList(convertToPromotion(getAuto10PercentNoCumulative())));
        //Single combine
        listPromotions.add(Collections.singletonList(convertToPromotion(getAuto10Percent())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getDiscount1E())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getPromo2E())));
        //Pair combine
        listPromotions.add(getCombineOf(getAuto10Percent(), getDiscount1E()));
        listPromotions.add(getCombineOf(getAuto10Percent(), getPromo2E()));
        listPromotions.add(getCombineOf(getDiscount1E(), getPromo2E()));
        //Combines of three
        listPromotions.add(getCombineOf(getAuto10Percent(), getDiscount1E(), getPromo2E()));
        return listPromotions;
    }

    public static List<List<Promotion>> getCombinesOfAutomaticNBPAndDiscountNBP() {
        List<List<Promotion>> listPromotions = new ArrayList<>();
        listPromotions.add(Collections.singletonList(convertToPromotion(getAutoNewPrice2())));
        listPromotions.add(Collections.singletonList(convertToPromotion(getDiscountNewPrice())));
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(convertToPromotion(getAutoNewPrice2()));
        promotions.add(convertToPromotion(getDiscountNewPrice()));
        listPromotions.add(promotions);
        return listPromotions;
    }

    private static List<Promotion> getCombineOf(EventPromotionRecord promoA, EventPromotionRecord promoB) {
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(convertToPromotion(promoA));
        promotions.add(convertToPromotion(promoB));
        return promotions;
    }

    private static List<Promotion> getCombineOf(EventPromotionRecord promoA, EventPromotionRecord promoB, EventPromotionRecord promoC) {
        List<Promotion> promotions = new ArrayList<>();
        promotions.add(convertToPromotion(promoA));
        promotions.add(convertToPromotion(promoB));
        promotions.add(convertToPromotion(promoC));
        return promotions;
    }

    public static List<Promotion> convertToPromotions(List<EventPromotionRecord> promotionsRecord) {
        return promotionsRecord.stream().map(PromotionUtils::convertToPromotion).collect(Collectors.toList());
    }

    private static Promotion convertToPromotion(EventPromotionRecord record) {
        Promotion promo = new Promotion();
        promo.setId(record.getEventPromotionTemplateId());
        promo.setActive(record.getActive());
        promo.setApplyChannelSpecificCharges(record.getApplyChannelSpecificCharges());
        promo.setApplyPromoterSpecificCharges(record.getApplyChannelSpecificCharges());
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
                .collect(Collectors.toList());
    }

    private static List<PromotionPricesRange> getRanges(String ranges) {
        String[] rangeItems = ranges != null ? ranges.split(GROUP_DELIMITER) : null;
        if (rangeItems != null) {
            return Stream.of(rangeItems)
                    .map(r -> {
                        String[] data = r.split(INTERNAL_DELIMITER);
                        return new PromotionPricesRange(Double.valueOf(data[0]), Double.valueOf(data[2]));
                    }).collect(Collectors.toList())
                    .stream()
                    .sorted((v1, v2) -> Double.compare(v2.getFrom(), v1.getFrom()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
