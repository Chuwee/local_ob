package es.onebox.event.promotions.converter;

import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionConditionRateRecord;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import es.onebox.event.priceengine.simulation.record.PromotionCommElemRecord;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.dto.PromotionCollective;
import es.onebox.event.promotions.dto.PromotionCommElements;
import es.onebox.event.promotions.dto.PromotionPriceVariation;
import es.onebox.event.promotions.dto.PromotionPriceVariationValue;
import es.onebox.event.promotions.dto.PromotionUsageConditions;
import es.onebox.event.promotions.dto.RatesRelationsCondition;
import es.onebox.event.promotions.dto.restriction.PromotionRestrictions;
import es.onebox.event.promotions.dto.restriction.PromotionValidationPeriod;
import es.onebox.event.promotions.dto.restriction.RestrictionLimit;
import es.onebox.event.promotions.enums.CollectiveSubtype;
import es.onebox.event.promotions.enums.CollectiveType;
import es.onebox.event.promotions.enums.PromotionPriceVariationType;
import es.onebox.event.promotions.enums.PromotionStatus;
import es.onebox.event.promotions.enums.PromotionType;
import es.onebox.event.promotions.enums.PromotionValidationPeriodType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventPromotionConverter {

    private static final String INTERNAL_DELIMITER = "::";
    private static final String GROUP_DELIMITER = "\\|\\|";

    private EventPromotionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate converter class");
    }

    public static List<EventPromotion> convert(List<EventPromotionRecord> promotionRecords, List<EventPromotionConditionRateRecord> usageConditions, List<RateRecord> rates) {
        if (CollectionUtils.isNotEmpty(promotionRecords)) {
            return promotionRecords.stream()
                    .map(record -> EventPromotionConverter.convert(record, usageConditions, rates))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static EventPromotion convert(EventPromotionRecord promotionRecord, List<EventPromotionConditionRateRecord> usageConditions, List<RateRecord> rates) {
        EventPromotion promo = new EventPromotion();
        promo.setEventId(promotionRecord.getEventId().longValue());
        promo.setEventPromotionTemplateId(promotionRecord.getEventPromotionTemplateId().longValue());
        promo.setPromotionTemplateId(promotionRecord.getPromotionTemplateId().longValue());
        promo.setName(promotionRecord.getName());
        promo.setActive(promotionRecord.getActive());
        promo.setStatus(PromotionStatus.from(promotionRecord.getActive(), promotionRecord.getStatus()));
        promo.setType(PromotionType.fromId(promotionRecord.getSubtype()));
        promo.setCommElements(buildCommElements(promotionRecord.getCommElemRecords()));
        promo.setRestrictions(buildRestrictions(promotionRecord));
        promo.setPriceVariation(buildPriceVariation(promotionRecord));
        promo.setApplyChannelSpecificCharges(promotionRecord.getApplyChannelSpecificCharges());
        promo.setApplyPromoterSpecificCharges(promotionRecord.getApplyPromoterSpecificCharges());
        promo.setSelfManaged(promotionRecord.getSelfManaged());
        promo.setRestrictiveAccess(promotionRecord.getRestrictiveAccess());
        promo.setBlockSecondaryMarketSale(promotionRecord.getBlockSecondaryMarketSale());
        promo.setUsageConditions(buildUsageConditions(usageConditions, promotionRecord.getEventPromotionTemplateId(), rates));
        return promo;
    }

    private static PromotionUsageConditions buildUsageConditions(List<EventPromotionConditionRateRecord> usageConditions, Integer promotionTemplateId, List<RateRecord> rates) {
        if (CollectionUtils.isEmpty(usageConditions)) {
            return null;
        }

        List<EventPromotionConditionRateRecord> filteredConditions = usageConditions.stream()
                .filter(condition -> Objects.equals(condition.getIdPromotionEvent(), promotionTemplateId))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filteredConditions)) {
            return null;
        }

        List<RatesRelationsCondition> ratesConditions = filteredConditions.stream()
                .map(record -> {
                    RatesRelationsCondition condition = new RatesRelationsCondition();
                    condition.setId(record.getId());
                    condition.setName(rates.stream()
                            .filter(r -> r.getIdTarifa().intValue() == record.getId())
                            .map(RateRecord::getNombre)
                            .findFirst()
                            .orElse(null));
                    condition.setQuantity(record.getQuantity());
                    return condition;
                })
                .collect(Collectors.toList());

        PromotionUsageConditions conditions = new PromotionUsageConditions();
        conditions.setRatesRelationsConditions(ratesConditions);

        return conditions;
    }

    private static PromotionPriceVariation buildPriceVariation(EventPromotionRecord promotionRecord) {
        PromotionPriceVariation priceVariation = null;
        if (promotionRecord.getDiscountType() != null) {
            priceVariation = new PromotionPriceVariation();
            priceVariation.setType(PromotionPriceVariationType.fromId(promotionRecord.getDiscountType()));
            priceVariation.setValue(buildPromotionPriceVariationList(promotionRecord, priceVariation.getType()));
        }
        return priceVariation;
    }

    private static List<PromotionPriceVariationValue> buildPromotionPriceVariationList(EventPromotionRecord promotionRecord, PromotionPriceVariationType type) {
        List<PromotionPriceVariationValue> ranges = new ArrayList<>();
        switch (type) {
            case FIXED:
                ranges.add(new PromotionPriceVariationValue(NumberUtils.DOUBLE_ZERO, promotionRecord.getFixedDiscountValue()));
                break;
            case PERCENTAGE:
                ranges.add(new PromotionPriceVariationValue(NumberUtils.DOUBLE_ZERO, promotionRecord.getPercentualDiscountValue()));
                break;
            case NEW_BASE_PRICE:
                String[] rangeItems = promotionRecord.getRanges() != null ? promotionRecord.getRanges().split(GROUP_DELIMITER) : null;
                if (rangeItems != null) {
                    List<PromotionPriceVariationValue> collect = Stream.of(rangeItems).map(r -> {
                        String[] data = r.split(INTERNAL_DELIMITER);
                        return new PromotionPriceVariationValue(Double.valueOf(data[0]), Double.valueOf(data[2]));
                    }).collect(Collectors.toList());
                    ranges.addAll(collect);
                }
                break;
            case NO_DISCOUNT:
            default:
                break;
        }

        return ranges;
    }

    private static PromotionRestrictions buildRestrictions(EventPromotionRecord promotionRecord) {
        PromotionRestrictions restrictions = new PromotionRestrictions();
        List<Long> channels = null;
        List<Long> priceZones = null;
        List<Long> rates = null;

        restrictions.setValidationPeriod(new PromotionValidationPeriod());
        restrictions.getValidationPeriod().setType(PromotionValidationPeriodType.fromId(promotionRecord.getValidationPeriodType()));
        restrictions.getValidationPeriod().setFrom(promotionRecord.getDateFrom());
        restrictions.getValidationPeriod().setTo(promotionRecord.getDateTo());

        PromotionCollective collective = buildCollective(promotionRecord);

        if (StringUtils.isNotEmpty(promotionRecord.getChannels())) {
            channels = parseLongList(promotionRecord.getChannels());
        }

        if (StringUtils.isNotEmpty(promotionRecord.getPriceZones())) {
            priceZones = parseLongList(promotionRecord.getPriceZones());
        }

        if (StringUtils.isNotEmpty(promotionRecord.getRates())) {
            rates = parseLongList(promotionRecord.getRates());
        }

        restrictions.setChannels(channels);
        restrictions.setRates(rates);
        restrictions.setPriceZones(priceZones);
        restrictions.setCollective(collective);
        restrictions.setSessions(promotionRecord.getSessions());

        var eventLimit = new RestrictionLimit();
        eventLimit.setEnabled(promotionRecord.getUseLimitByEvent());
        eventLimit.setValue(promotionRecord.getLimitByEvent());
        restrictions.setEventLimit(eventLimit);

        var sessionLimit = new RestrictionLimit();
        sessionLimit.setEnabled(promotionRecord.getUseLimitBySession());
        sessionLimit.setValue(promotionRecord.getLimitBySession());
        restrictions.setSessionLimit(sessionLimit);

        var operationLimit = new RestrictionLimit();
        operationLimit.setEnabled(promotionRecord.getUseLimitByOperation());
        operationLimit.setValue(promotionRecord.getLimitByOperation());
        restrictions.setOperationLimit(operationLimit);

        var packLimit = new RestrictionLimit();
        packLimit.setEnabled(promotionRecord.getUseLimitByPack());
        packLimit.setValue(promotionRecord.getLimitByPack());
        restrictions.setPackLimit(packLimit);

        var minLimit = new RestrictionLimit();
        minLimit.setEnabled(promotionRecord.getUseLimitByMinTickets());
        minLimit.setValue(promotionRecord.getLimitByMinTickets());
        restrictions.setMinLimit(minLimit);

        var eventCollectiveLimit = new RestrictionLimit();
        eventCollectiveLimit.setEnabled(promotionRecord.getUsesEventUserCollectiveLimit());
        eventCollectiveLimit.setValue(promotionRecord.getEventUserCollectiveLimit());
        restrictions.setEventCollectiveLimit(eventCollectiveLimit);

        var sessionCollectiveLimit = new RestrictionLimit();
        sessionCollectiveLimit.setEnabled(promotionRecord.getUsesSessionUserCollectiveLimit());
        sessionCollectiveLimit.setValue(promotionRecord.getSessionUserCollectiveLimit());
        restrictions.setSessionCollectiveLimit(sessionCollectiveLimit);

        restrictions.setNonCummulative(promotionRecord.getNotCumulative());

        return restrictions;
    }

    private static PromotionCollective buildCollective(EventPromotionRecord promotionRecord) {
        PromotionCollective collective = null;
        if (promotionRecord.getCollectiveId() != null) {
            collective = new PromotionCollective();
            collective.setId(promotionRecord.getCollectiveId());
            collective.setExclusiveSale(promotionRecord.getExclusiveSale());
            collective.setType(CollectiveType.fromId(promotionRecord.getCollectiveTypeId()));
            collective.setName(promotionRecord.getCollectiveName());
            CollectiveSubtype collectiveSubtype = CollectiveSubtype.fromId(promotionRecord.getCollectiveSubtypeId());
            if (collectiveSubtype != null) {
                collective.setValidationType(collectiveSubtype.getValidationType());
            }
        }
        return collective;
    }

    private static List<Long> parseLongList(String text) {
        String[] items = text.split(GROUP_DELIMITER);
        return Stream.of(items).map(Long::valueOf).collect(Collectors.toList());
    }

    private static PromotionCommElements buildCommElements(List<PromotionCommElemRecord> commElements) {
        if (CollectionUtils.isEmpty(commElements)) {
            return null;
        }
        Map<String, String> names = commElements.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.isNotBlank(item.getName()))
                .collect(Collectors.toMap(PromotionCommElemRecord::getLanguageCode, PromotionCommElemRecord::getName));
        Map<String, String> descriptions = commElements.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.isNotBlank(item.getDescription()))
                .collect(Collectors.toMap(PromotionCommElemRecord::getLanguageCode, PromotionCommElemRecord::getDescription));

        return new PromotionCommElements(names, descriptions);
    }


}
