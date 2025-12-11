package es.onebox.event.catalog.converter;

import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionCollective;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionCollectiveValidationMethod;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionCommunicationElementsDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionRestrictionsDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionValidationPeriodDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionValidationPeriodType;
import es.onebox.event.catalog.dto.promotion.PromotionUsageConditionsDTO;
import es.onebox.event.catalog.dto.promotion.RatesRelationsConditionDTO;
import es.onebox.event.catalog.dto.promotion.RestrictionLimitDTO;
import es.onebox.event.catalog.elasticsearch.dto.PriceZonePrice;
import es.onebox.event.catalog.elasticsearch.dto.Promotion;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceZonePrices;
import es.onebox.event.catalog.elasticsearch.pricematrix.RatePrices;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.promotions.dao.couch.EventPromotionDocument;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.dto.PromotionCollective;
import es.onebox.event.promotions.dto.PromotionCommElements;
import es.onebox.event.promotions.dto.PromotionUsageConditions;
import es.onebox.event.promotions.dto.restriction.PromotionRestrictions;
import es.onebox.event.promotions.dto.restriction.PromotionValidationPeriod;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CatalogPromotionConverter {

    private CatalogPromotionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<CatalogPromotionDTO> convert(List<Promotion> promotions,
                                                    List<Long> channelPromotions,
                                                    VenueTemplatePrice venueTemplatePrices,
                                                    List<PriceZonePrices> channelPrices,
                                                    EventPromotionCouchDao eventPromotionCouchDao) {
        if (CollectionUtils.isEmpty(promotions) || CollectionUtils.isEmpty(channelPromotions)) {
            return Collections.emptyList();
        }
        var applicablePromotions = promotions.stream()
                .filter(promotion -> channelPromotions.contains(promotion.getEventPromotionTemplateId())).collect(Collectors.toList());
        var promotionIds = applicablePromotions.stream()
                .map(Promotion::getEventPromotionTemplateId)
                .map(id -> new Key(new String[]{id.toString()})).collect(Collectors.toList());
        var eventPromotions = eventPromotionCouchDao.
                bulkGet(promotionIds)
                .stream()
                .collect(Collectors.toMap(EventPromotionDocument::getEventPromotionTemplateId, Function.identity()));
        return applicablePromotions.stream()
                .map(promotion -> {
                    EventPromotion promoDetails = eventPromotions.get(promotion.getEventPromotionTemplateId());
                    return convert(promotion, venueTemplatePrices, channelPrices, promoDetails);
                })
                .collect(Collectors.toList());
    }

    private static CatalogPromotionDTO convert(Promotion promotion, VenueTemplatePrice venueTemplatePrices, List<PriceZonePrices> prices, EventPromotion promoDetails) {
        if (promotion == null) {
            return null;
        }
        CatalogPromotionDTO catalogPromotion = new CatalogPromotionDTO();
        catalogPromotion.setId(promotion.getEventPromotionTemplateId());
        catalogPromotion.setName(promotion.getName());
        catalogPromotion.setActive(promotion.getActive());
        catalogPromotion.setStatus(promotion.getStatus());
        catalogPromotion.setType(promotion.getType());
        catalogPromotion.setCommunicationElements(convert(promotion.getCommunicationElements()));
        catalogPromotion.setSelfManaged(promotion.getSelfManaged());
        catalogPromotion.setUsageConditions(convert(promoDetails.getUsageConditions()));
        if (promoDetails != null) {
            catalogPromotion.setRestrictions(convert(promoDetails.getRestrictions(), venueTemplatePrices, prices));
            if (promoDetails.getRestrictions() != null) {
                catalogPromotion.setCollective(convert(promoDetails.getRestrictions().getCollective()));
            }
        }

        return catalogPromotion;
    }

    private static CatalogPromotionCollective convert(PromotionCollective in) {
        if (in == null) {
            return null;
        }
        CatalogPromotionCollective out = new CatalogPromotionCollective();
        out.setId(in.getId().longValue());
        out.setName(in.getName());
        if (in.getValidationType() != null) {
            out.setValidationMethod(CatalogPromotionCollectiveValidationMethod.fromString(in.getValidationType().name()));
        }
        return out;
    }

    private static CatalogPromotionCommunicationElementsDTO convert(PromotionCommElements communicationElements) {
        if (communicationElements == null) {
            return null;
        }
        CatalogPromotionCommunicationElementsDTO catalogCommunicationElements = new CatalogPromotionCommunicationElementsDTO();
        catalogCommunicationElements.setName(communicationElements.getName());
        catalogCommunicationElements.setDescription(communicationElements.getDescription());
        return catalogCommunicationElements;
    }

    private static CatalogPromotionRestrictionsDTO convert(PromotionRestrictions promotionRestrictions, VenueTemplatePrice venueTemplatePrice, List<PriceZonePrices> prices) {
        if (promotionRestrictions == null) {
            return null;
        }
        CatalogPromotionRestrictionsDTO restrictions = new CatalogPromotionRestrictionsDTO();
        restrictions.setValidationPeriod(convert(promotionRestrictions.getValidationPeriod()));

        restrictions.setNonCummulative(promotionRestrictions.getNonCummulative());

        if (promotionRestrictions.getOperationLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getOperationLimit().getEnabled());
            limit.setValue(promotionRestrictions.getOperationLimit().getValue());

            restrictions.setOperationLimit(limit);
        }
        if (promotionRestrictions.getMinLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getMinLimit().getEnabled());
            limit.setValue(promotionRestrictions.getMinLimit().getValue());

            restrictions.setMinLimit(limit);
        }
        if (promotionRestrictions.getEventLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getEventLimit().getEnabled());
            limit.setValue(promotionRestrictions.getEventLimit().getValue());

            restrictions.setEventLimit(limit);
        }
        if (promotionRestrictions.getSessionLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getSessionLimit().getEnabled());
            limit.setValue(promotionRestrictions.getSessionLimit().getValue());

            restrictions.setSessionLimit(limit);
        }
        if (promotionRestrictions.getPackLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getPackLimit().getEnabled());
            limit.setValue(promotionRestrictions.getPackLimit().getValue());

            restrictions.setPackLimit(limit);
        }
        if (promotionRestrictions.getEventCollectiveLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getEventCollectiveLimit().getEnabled());
            limit.setValue(promotionRestrictions.getEventCollectiveLimit().getValue());

            restrictions.setEventCollectiveLimit(limit);
        }
        if (promotionRestrictions.getSessionCollectiveLimit() != null) {
            var limit = new RestrictionLimitDTO();
            limit.setEnabled(promotionRestrictions.getSessionCollectiveLimit().getEnabled());
            limit.setValue(promotionRestrictions.getSessionCollectiveLimit().getValue());

            restrictions.setSessionCollectiveLimit(limit);
        }
        if (promotionRestrictions.getPriceZones() != null) {
            if (Objects.nonNull(venueTemplatePrice) && CollectionUtils.isNotEmpty(venueTemplatePrice.getPriceZones())) {
                List<Long> priceZones = venueTemplatePrice.getPriceZones().stream()
                        .map(PriceZonePrice::getId).map(Integer::longValue).toList();
                restrictions.setPriceZones(promotionRestrictions.getPriceZones().stream().filter(priceZones::contains).toList());
            } else {
                List<Long> priceZones = prices.stream().map(PriceZonePrices::getPriceZoneId).toList();
                restrictions.setPriceZones(promotionRestrictions.getPriceZones().stream().filter(priceZones::contains).toList());
            }
        }

        if (promotionRestrictions.getRates() != null) {
            List<Long> rates = new ArrayList<>();
            prices.forEach(priceZonePrices -> rates.addAll(priceZonePrices.getRates().stream().map(RatePrices::getId).toList()));
            restrictions.setRates(promotionRestrictions.getRates().stream().filter(rates::contains).toList());
        }

        return restrictions;
    }

    private static CatalogPromotionValidationPeriodDTO convert(PromotionValidationPeriod validationPeriod) {
        if (validationPeriod == null) {
            return null;
        }
        CatalogPromotionValidationPeriodDTO validationPeriodDTO = new CatalogPromotionValidationPeriodDTO();
        validationPeriodDTO.setType(CatalogPromotionValidationPeriodType.valueOf(validationPeriod.getType().name()));
        validationPeriodDTO.setFrom(CatalogUtils.toZonedDateTime(validationPeriod.getFrom()));
        validationPeriodDTO.setTo(CatalogUtils.toZonedDateTime(validationPeriod.getTo()));
        return validationPeriodDTO;
    }

    private static PromotionUsageConditionsDTO convert(PromotionUsageConditions usageConditions) {
        if (usageConditions == null) {
            return null;
        }

        PromotionUsageConditionsDTO catalogUsageConditions = new PromotionUsageConditionsDTO();

        List<RatesRelationsConditionDTO> conditions = usageConditions.getRatesRelationsConditions()
                .stream()
                .map(condition -> {
                    RatesRelationsConditionDTO dto = new RatesRelationsConditionDTO();
                    dto.setId(condition.getId());
                    dto.setName(condition.getName());
                    dto.setQuantity(condition.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());

        catalogUsageConditions.setRatesRelationsConditions(conditions);

        return catalogUsageConditions;
    }
}
