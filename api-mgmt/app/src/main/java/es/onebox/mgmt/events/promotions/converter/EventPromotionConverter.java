package es.onebox.mgmt.events.promotions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.promotions.converter.PromotionConverter;
import es.onebox.mgmt.common.promotions.dto.CustomerTypesConditionDTO;
import es.onebox.mgmt.common.promotions.dto.PromoRateConditionDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionCollectiveDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionDiscountConfigDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionRangeDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionSurchargesDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionValidityPeriodDTO;
import es.onebox.mgmt.common.promotions.dto.RatesRelationsConditionDTO;
import es.onebox.mgmt.common.promotions.dto.UpdateCustomerTypesConditionDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionDiscountType;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.common.promotions.enums.PromotionType;
import es.onebox.mgmt.common.promotions.enums.PromotionValidityType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.CustomerTypesCondition;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromoRateCondition;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionCollective;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionCollectiveType;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionConditions;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionDiscountConfig;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionLimits;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionPeriod;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionRange;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplate;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.RatesRelationsCondition;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateCustomerTypesCondition;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdatePromotionConditions;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import es.onebox.mgmt.events.promotions.dto.CreateEventPromotionDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionCollectiveTypeDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionValidityDatesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionsDTO;
import es.onebox.mgmt.events.promotions.dto.PromotionConditionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.UpdatePromotionConditionsDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.promotions.converter.PromotionConverter.createPromotionLimit;
import static es.onebox.mgmt.common.promotions.converter.PromotionConverter.createPromotionMaxLimit;
import static es.onebox.mgmt.common.promotions.converter.PromotionConverter.limit;

public class EventPromotionConverter {

    private EventPromotionConverter() {
    }

    public static EventPromotionDTO fromMsPromotion(PromotionTemplate eventPromo) {
        EventPromotionDTO dto = new EventPromotionDTO();
        EventPromotionValidityDatesDTO  validityDatesDTO = new EventPromotionValidityDatesDTO();

        validityDatesDTO.setStart(eventPromo.getValidityDates().getStart());
        validityDatesDTO.setEnd(eventPromo.getValidityDates().getEnd());
        dto.setValidityDates(validityDatesDTO);

        dto.setId(eventPromo.getId().intValue());
        dto.setName(eventPromo.getName());
        dto.setStatus(PromotionStatus.valueOf(eventPromo.getStatus().name()));
        dto.setType(PromotionType.valueOf(eventPromo.getType().name()));
        dto.setPresale(eventPromo.getPresale());
        return dto;
    }

    public static EventPromotionsDTO fromMsPromotions(PromotionTemplates eventPromos) {
        EventPromotionsDTO eventPromotionsDTO = new EventPromotionsDTO();
        eventPromotionsDTO.setData(eventPromos.getData().stream()
                .map(EventPromotionConverter::fromMsPromotion)
                .collect(Collectors.toList()));
        eventPromotionsDTO.setMetadata(eventPromos.getMetadata());
        return eventPromotionsDTO;
    }

    public static CreatePromotion toMsPromotions(CreateEventPromotionDTO createEventPromotionDTO) {
        CreatePromotion createPromotion = new CreatePromotion();
        createPromotion.setName(createEventPromotionDTO.getName());
        createPromotion.setType(es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType
                .valueOf(createEventPromotionDTO.getType().name()));
        return createPromotion;
    }

    public static EventPromotionDetailDTO fromMsPromotion(PromotionDetail inDto, List<Currency> currencies) {

        EventPromotionDetailDTO outDto = new EventPromotionDetailDTO();

        generalInfo(inDto, outDto);

        discount(inDto, outDto, currencies);
        limits(inDto, outDto);
        validityPeriod(inDto, outDto);
        collective(inDto, outDto);
        secondaryMarket(inDto, outDto);
        promotionConditions(inDto, outDto);
        PromotionSurchargesDTO surcharges = new PromotionSurchargesDTO();
        surcharges.setChannelFees(inDto.getIncludeChannelSurcharges());
        surcharges.setPromoter(inDto.getIncludePromoterSurcharges());
        outDto.setSurcharges(surcharges);

        return outDto;
    }

    public static UpdateEventPromotionDetail toUpdatePromotionDetail(UpdateEventPromotionDetailDTO inDto, List<Currency> currencies) {
        UpdateEventPromotionDetail outDto = new UpdateEventPromotionDetail();
        generalInfo(inDto, outDto);
        if (inDto.getSurcharges() != null) {
            outDto.setIncludeChannelSurcharges(inDto.getSurcharges().getChannelFees());
            outDto.setIncludePromoterSurcharges(inDto.getSurcharges().getPromoter());
        }

        validityPeriod(inDto, outDto);
        limits(inDto, outDto);
        discount(inDto, outDto, currencies);
        collective(inDto, outDto);
        secondaryMarket(inDto, outDto);
        promotionConditions(inDto, outDto);

        return outDto;
    }

    public static ClonePromotion toClonePromotion(CreateEventPromotionDTO createEventPromotionDTO) {
        ClonePromotion msDto = new ClonePromotion();
        msDto.setName(createEventPromotionDTO.getName());
        msDto.setEntityPromotionTemplateId(createEventPromotionDTO.getFromEntityTemplateId());
        return msDto;
    }

    public static ClonePromotion toClonePromotion(Long eventPromotionId) {
        ClonePromotion msDto = new ClonePromotion();
        msDto.setEventPromotionTemplateId(eventPromotionId);
        return msDto;
    }

    private static void generalInfo(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        outDto.setAccesControlRestricted(inDto.getAccesControlRestricted());
        outDto.setCombinable(inDto.getCombinable());
        outDto.setShowTicketDiscountName(inDto.getShowDiscountNameticket());
        outDto.setShowTicketPriceWithouDiscount(inDto.getShowTicketPriceWithoutDiscount());
        outDto.setName(inDto.getName());
        outDto.setPresale(inDto.getPresale());
        if(inDto.getStatus() != null) {
            outDto.setStatus(PromotionActivationStatus.fromId(inDto.getStatus().getId()));
        }
    }

    private static void generalInfo(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        outDto.setId(inDto.getId());
        outDto.setName(inDto.getName());
        outDto.setStatus(PromotionStatus.valueOf(inDto.getStatus().toString()));
        outDto.setType(PromotionType.valueOf(inDto.getType().toString()));
        outDto.setCombinable(inDto.getCombinable());
        outDto.setShowTicketPriceWithoutDiscount(inDto.getShowTicketPriceWithoutDiscount());
        outDto.setShowDiscountNameticket(inDto.getShowTicketDiscountName());
        outDto.setAccesControlRestricted(inDto.getAccesControlRestricted());
        outDto.setPresale(inDto.getPresale());
    }

    private static void discount(PromotionDetail inDto, EventPromotionDetailDTO outDto, List<Currency> currencies) {
        if (inDto.getDiscount() != null) {
            PromotionDiscountConfig inDiscountConfig = inDto.getDiscount();
            PromotionDiscountConfigDTO targetDiscountConfig = new PromotionDiscountConfigDTO();

            if (inDiscountConfig.getCurrencyId() != null) {
                targetDiscountConfig.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, inDiscountConfig.getCurrencyId()));
            }
            if (inDiscountConfig.getRanges() != null) {
                targetDiscountConfig.setRanges(inDiscountConfig.getRanges().stream()
                        .map(EventPromotionConverter::createRangeDTO).collect(Collectors.toList()));
            }
            if (inDiscountConfig.getType() != null) {
                targetDiscountConfig.setType(PromotionDiscountType.fromId(inDiscountConfig.getType().getId()));
            }
            targetDiscountConfig.setValue(inDiscountConfig.getValue());
            outDto.setDiscount(targetDiscountConfig);
        }
    }

    private static void limits(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        if (inDto.getLimits() != null) {
            PromotionLimitsDTO outLimits = new PromotionLimitsDTO();
            PromotionLimits inLimits = inDto.getLimits();
            outLimits.setPromotionMaxLimit(createPromotionMaxLimit(inLimits.getPromotionMaxLimit()));
            outLimits.setSessionMaxLimit(createPromotionLimit(inLimits.getSessionMaxLimit()));
            outLimits.setSessionUserCollectiveMaxLimit(createPromotionLimit(inLimits.getSessionUserCollectiveMaxLimit()));
            outLimits.setEventUserCollectiveMaxLimit(createPromotionLimit(inLimits.getEventUserCollectiveMaxLimit()));
            if (!PromotionType.AUTOMATIC.equals(outDto.getType())) {
                outLimits.setPacks(createPromotionLimit(inLimits.getPacks()));
                outLimits.setPurchaseMaxLimit(createPromotionLimit(inLimits.getPurchaseMaxLimit()));
                outLimits.setPurchaseMinLimit(createPromotionLimit(inLimits.getPurchaseMinLimit()));
            }
            outDto.setLimits(outLimits);
        }
    }

    private static void collective(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        if (inDto.getCollective() != null) {
            PromotionCollective inCollective = inDto.getCollective();
            PromotionCollectiveDTO outCollective = new PromotionCollectiveDTO();
            outCollective.setId(inCollective.getId());
            EventPromotionCollectiveTypeDTO type = inCollective.getType() != null
                    ? EventPromotionCollectiveTypeDTO.valueOf(inCollective.getType().name())
                    : null;
            outCollective.setType(type);
            outCollective.setBoxOfficeValidation(inCollective.getBoxOfficeValidation());
            outCollective.setRestrictiveSale(inCollective.getRestrictiveSale());
            outCollective.setSelfManaged(inCollective.getSelfManaged());
            outDto.setCollective(outCollective);
        }
    }

    private static void validityPeriod(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        if (inDto.getValidityPeriod() != null) {
            PromotionPeriod inPeriod = inDto.getValidityPeriod();
            PromotionValidityPeriodDTO outPeriod = new PromotionValidityPeriodDTO();
            outPeriod.setEndDate(inPeriod.getEndDate());
            outPeriod.setStartDate(inPeriod.getStartDate());
            outPeriod.setType(PromotionValidityType.fromId(inPeriod.getType().getId()));
            outDto.setValidityPeriod(outPeriod);
        }
    }

    private static void validityPeriod(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getValidityPeriod() != null) {
            PromotionValidityPeriodDTO validityPeriod = inDto.getValidityPeriod();
            PromotionPeriod period = new PromotionPeriod();
            period.setEndDate(validityPeriod.getEndDate());
            period.setStartDate(validityPeriod.getStartDate());
            if (validityPeriod.getType() != null) {
                period.setType(es.onebox.mgmt.datasources.ms.promotion.enums.PromotionValidityType
                        .fromId(validityPeriod.getType().getId()));
            }
            outDto.setValidityPeriod(period);
        }
    }

    private static void collective(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getCollective() != null) {
            PromotionCollectiveDTO inCollective = inDto.getCollective();
            PromotionCollective outCollective = new PromotionCollective();
            outCollective.setId(inCollective.getId());
            PromotionCollectiveType type = inCollective.getType() != null
                    ? PromotionCollectiveType.valueOf(inCollective.getType().name())
                    : null;
            outCollective.setType(type);
            outCollective.setBoxOfficeValidation(inCollective.getBoxOfficeValidation());
            outCollective.setRestrictiveSale(inCollective.getRestrictiveSale());
            outCollective.setSelfManaged(inCollective.getSelfManaged());
            outDto.setCollective(outCollective);
        }
    }

    private static void discount(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto, List<Currency> currencies) {
        if (inDto.getDiscount() != null) {
            PromotionDiscountConfigDTO discount = inDto.getDiscount();
            PromotionDiscountConfig outDiscount = new PromotionDiscountConfig();
            outDiscount.setValue(discount.getValue());
            es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType type = discount.getType() != null
                    ? es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType.valueOf(discount.getType().name())
                    : null;
            outDiscount.setType(type);
            if (discount.getCurrencyCode() != null) {
                outDiscount.setCurrencyId(currencies.stream().filter(currency -> currency.getCode().equals(discount.getCurrencyCode())).map(Currency::getId)
                        .findFirst().orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND)));
            }
            if (es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType.BASE_PRICE.equals(type) && CollectionUtils.isNotEmpty(discount.getRanges())) {
                outDiscount.setRanges(discount.getRanges().stream()
                        .map(el -> new PromotionRange(el.getFrom(), el.getTo(), el.getValue()))
                        .collect(Collectors.toList()));
            }
            outDto.setDiscount(outDiscount);
        }
    }

    private static void limits(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getLimits() != null) {
            PromotionLimitsDTO limits = inDto.getLimits();
            PromotionLimits outLimits = new PromotionLimits();
            if (limits.getPacks() != null) {
                outLimits.setPacks(limit(limits.getPacks()));
            }
            if (limits.getPromotionMaxLimit() != null) {
                outLimits.setPromotionMaxLimit(PromotionConverter.maxLimit(limits.getPromotionMaxLimit()));
            }
            if (limits.getPurchaseMaxLimit() != null) {
                outLimits.setPurchaseMaxLimit(limit(limits.getPurchaseMaxLimit()));
            }
            if (limits.getPurchaseMinLimit() != null) {
                outLimits.setPurchaseMinLimit(limit(limits.getPurchaseMinLimit()));
            }
            if (limits.getSessionMaxLimit() != null) {
                outLimits.setSessionMaxLimit(limit(limits.getSessionMaxLimit()));
            }
            if (limits.getSessionUserCollectiveMaxLimit() != null) {
                outLimits.setSessionUserCollectiveMaxLimit(limit(limits.getSessionUserCollectiveMaxLimit()));
            }
            if (limits.getEventUserCollectiveMaxLimit() != null) {
                outLimits.setEventUserCollectiveMaxLimit(limit(limits.getEventUserCollectiveMaxLimit()));
            }
            outDto.setLimits(outLimits);
        }
    }

    private static PromotionRangeDTO createRangeDTO(PromotionRange range) {
        return new PromotionRangeDTO(
                range.getFrom(),
                range.getTo(),
                range.getValue());
    }

    private static void secondaryMarket(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        outDto.setBlockSecondaryMarketSale(inDto.getBlockSecondaryMarketSale());
    }

    private static void secondaryMarket(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        outDto.setBlockSecondaryMarketSale(inDto.getBlockSecondaryMarketSale());
    }

    private static RatesRelationsCondition createAdditionalApplicationConditions(RatesRelationsConditionDTO dto) {
        if (dto == null) {
            return null;
        }

        RatesRelationsCondition entity = new RatesRelationsCondition();
        entity.setEnabled(dto.getEnabled());

        if (dto.getRates() != null) {
            List<PromoRateCondition> promoRateConditions = dto.getRates()
                    .stream()
                    .map(EventPromotionConverter::convertPromoRateCondition)
                    .collect(Collectors.toList());
            entity.setRates(promoRateConditions);
        }

        return entity;
    }

    private static PromoRateCondition convertPromoRateCondition(PromoRateConditionDTO dto) {
        if (dto == null) {
            return null;
        }

        PromoRateCondition entity = new PromoRateCondition();
        entity.setLimit(dto.getLimit());
        entity.setRate(dto.getRate());
        return entity;
    }


    private static void promotionConditions(UpdateEventPromotionDetailDTO inDto, UpdateEventPromotionDetail outDto) {
        UpdatePromotionConditionsDTO promotionConditions = inDto.getConditions();
        if (promotionConditions == null) {
            return;
        }

        UpdatePromotionConditions out = new UpdatePromotionConditions();

        UpdateCustomerTypesConditionDTO customerTypesConditionDTO = promotionConditions.getCustomerTypesCondition();
        if (customerTypesConditionDTO != null) {
            out.setCustomerTypesCondition(toMs(customerTypesConditionDTO));
        }

        RatesRelationsConditionDTO ratesRelationsConditionDTO = promotionConditions.getRatesRelationsCondition();
        if (ratesRelationsConditionDTO != null) {
            out.setRatesRelationsCondition(createAdditionalApplicationConditions(ratesRelationsConditionDTO));
        }

        outDto.setConditions(out);
    }

    private static void promotionConditions(PromotionDetail inDto, EventPromotionDetailDTO outDto) {
        PromotionConditions promotionConditions = inDto.getConditions();
        if (promotionConditions == null) {
            return;
        }

        PromotionConditionsDTO outPromotionConditions = new PromotionConditionsDTO();

        CustomerTypesCondition customerTypesCondition = promotionConditions.getCustomerTypesCondition();
        if (customerTypesCondition != null) {
            outPromotionConditions.setCustomerTypesCondition(toDTO(customerTypesCondition));
        }

        RatesRelationsCondition ratesRelationsCondition = promotionConditions.getRatesRelationsCondition();
        if (ratesRelationsCondition != null) {
            outPromotionConditions.setRatesRelationsCondition(createAdditionalApplicationConditions(ratesRelationsCondition));
        }

        outDto.setConditions(outPromotionConditions);
    }

    private static UpdateCustomerTypesCondition toMs(UpdateCustomerTypesConditionDTO customerTypesConditionDTO) {
        UpdateCustomerTypesCondition customerTypes = new UpdateCustomerTypesCondition();
        customerTypes.setType(customerTypesConditionDTO.getType());
        customerTypes.setCustomerTypeIds(customerTypesConditionDTO.getCustomerTypeIds());
        return customerTypes;
    }

    private static CustomerTypesConditionDTO toDTO(CustomerTypesCondition customerTypesCondition) {
        CustomerTypesConditionDTO customerTypes = new CustomerTypesConditionDTO();
        customerTypes.setType(customerTypesCondition.getType());
        customerTypes.setCustomerTypes(customerTypesCondition.getCustomerTypes());
        return customerTypes;
    }

    private static RatesRelationsConditionDTO createAdditionalApplicationConditions(RatesRelationsCondition ratesRelationsCondition) {
        if (ratesRelationsCondition == null) {
            return null;
        }

        RatesRelationsConditionDTO dto = new RatesRelationsConditionDTO();
        dto.setEnabled(ratesRelationsCondition.getEnabled());

        if (ratesRelationsCondition.getRates() != null) {
            List<PromoRateConditionDTO> promoRateConditionDTOS = ratesRelationsCondition.getRates()
                    .stream()
                    .map(EventPromotionConverter::convertPromoRateCondition)
                    .collect(Collectors.toList());
            dto.setRates(promoRateConditionDTOS);
        }

        return dto;
    }

    private static PromoRateConditionDTO convertPromoRateCondition(PromoRateCondition source) {
        if (source == null) {
            return null;
        }

        PromoRateConditionDTO dto = new PromoRateConditionDTO();
        dto.setLimit(source.getLimit());
        dto.setRate(source.getRate());
        return dto;
    }

}