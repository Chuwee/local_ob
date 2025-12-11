package es.onebox.mgmt.events.promotiontemplates.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.promotions.dto.CreatePromotionDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionDiscountConfigDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionMaxLimitDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionRangeDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionSurchargesDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionTemplateDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionValidityPeriodDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionDiscountType;
import es.onebox.mgmt.common.promotions.enums.PromotionType;
import es.onebox.mgmt.common.promotions.enums.PromotionValidityType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionTemplate;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionCollectiveType;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionDiscountConfig;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionLimit;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionLimits;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionMaxLimit;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionPeriod;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionRange;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateCollective;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.events.promotions.dto.EventPromotionCollectiveTypeDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateDetailDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplatesDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.PromotionTemplateCollectiveDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.UpdateEventPromotionTemplateDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventPromotionTemplateConverter {

    private EventPromotionTemplateConverter() {
    }

    public static EventPromotionTemplatesDTO from(List<EventPromotionTemplate> list, List<Currency> currencies) {
        EventPromotionTemplatesDTO out = new EventPromotionTemplatesDTO();
        out.setData(list.stream()
                .map(template -> EventPromotionTemplateConverter.from(template, currencies))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return out;
    }

    public static EventPromotionTemplateDetailDTO from(PromotionTemplateDetail in, List<Currency> currencies) {
        if (in == null) {
            return null;
        }

        EventPromotionTemplateDetailDTO out = new EventPromotionTemplateDetailDTO();
        generalInfo(in, out);
        discount(in, out, currencies);
        limits(in, out);
        validityPeriod(in, out);
        surcharges(in, out);
        collective(in, out);
        entity(in, out);
        return out;
    }

    public static CreatePromotion toMsPromotions(CreatePromotionDTO createPromotionDTO) {
        CreatePromotion createPromotion = new CreatePromotion();
        createPromotion.setName(createPromotionDTO.getName());
        createPromotion.setType(es.onebox.mgmt.datasources.ms.promotion.enums.PromotionType
                .valueOf(createPromotionDTO.getType().name()));
        return createPromotion;
    }

    public static UpdateEventPromotionDetail toMsDto(UpdateEventPromotionTemplateDTO inDto) {
        UpdateEventPromotionDetail outDto = new UpdateEventPromotionDetail();
        generalInfo(inDto, outDto);
        if (inDto.getSurcharges() != null) {
            outDto.setIncludeChannelSurcharges(inDto.getSurcharges().getChannelFees());
            outDto.setIncludePromoterSurcharges(inDto.getSurcharges().getPromoter());
        }
        validityPeriod(inDto, outDto);
        limits(inDto, outDto);
        discount(inDto, outDto);
        collective(inDto, outDto);

        return outDto;
    }

    private static PromotionTemplateDTO from(EventPromotionTemplate in, List<Currency> currencies) {
        if (in == null) {
            return null;
        }

        PromotionTemplateDTO out = new PromotionTemplateDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setType(PromotionType.valueOf(in.getType().name()));
        out.setFavorite(in.getFavorite());
        out.setPresale(in.getPresale());
        if (in.getCurrencyId() != null) {
            out.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, in.getCurrencyId()));
        }
        entity(in, out);
        return out;
    }

    private static void entity(EventPromotionTemplate in, PromotionTemplateDTO out) {
        IdNameDTO idName = new IdNameDTO();
        idName.setId(in.getEntityId());
        idName.setName(in.getEntityName());
        out.setEntity(idName);
    }

    private static void entity(PromotionTemplateDetail in, PromotionTemplateDTO out) {
        IdNameDTO idName = new IdNameDTO();
        idName.setId(in.getEntityId());
        idName.setName(in.getEntityName());
        out.setEntity(idName);
    }

    private static void generalInfo(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out) {
        out.setId(in.getId());
        out.setName(in.getName());
        out.setType(PromotionType.valueOf(in.getType().name()));
        out.setCombinable(in.getCombinable());
        out.setShowTicketPriceWithoutDiscount(in.getShowTicketPriceWithoutDiscount());
        out.setShowDiscountNameticket(in.getShowTicketDiscountName());
        out.setAccesControlRestricted(in.getAccesControlRestricted());
        out.setFavorite(in.getFavorite());
        out.setPresale(in.getPresale());
    }

    private static void discount(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out, List<Currency> currencies) {
        if (in.getDiscount() != null) {
            PromotionDiscountConfigDTO targetDiscountConfig = new PromotionDiscountConfigDTO();
            if (in.getDiscount().getCurrencyId() !=  null) {
                targetDiscountConfig.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, in.getDiscount().getCurrencyId()));
            }
            if (in.getDiscount().getRanges() != null) {
                targetDiscountConfig.setRanges(in.getDiscount().getRanges().stream()
                        .map(EventPromotionTemplateConverter::createRangeDTO).collect(Collectors.toList()));
            }
            if (in.getDiscount().getType() != null) {
                targetDiscountConfig.setType(PromotionDiscountType.fromId(in.getDiscount().getType().getId()));
            }
            targetDiscountConfig.setValue(in.getDiscount().getValue());
            out.setDiscount(targetDiscountConfig);
        }
    }

    private static void limits(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out) {
        if (in.getLimits() != null) {
            PromotionLimitsDTO outLimits = new PromotionLimitsDTO();
            PromotionLimits inLimits = in.getLimits();
            outLimits.setPromotionMaxLimit(createPromotionMaxLimit(inLimits.getPromotionMaxLimit()));
            outLimits.setSessionMaxLimit(createPromotionLimit(inLimits.getSessionMaxLimit()));
            if (!PromotionType.AUTOMATIC.equals(out.getType())) {
                outLimits.setPacks(createPromotionLimit(inLimits.getPacks()));
                outLimits.setPurchaseMaxLimit(createPromotionLimit(inLimits.getPurchaseMaxLimit()));
                outLimits.setPurchaseMinLimit(createPromotionLimit(inLimits.getPurchaseMinLimit()));
                outLimits.setEventUserCollectiveMaxLimit(createPromotionLimit(inLimits.getEventUserCollectiveMaxLimit()));
                outLimits.setSessionUserCollectiveMaxLimit(createPromotionLimit(inLimits.getSessionUserCollectiveMaxLimit()));
            }
            out.setLimits(outLimits);
        }
    }

    private static void validityPeriod(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out) {
        if (in.getValidityPeriod() != null) {
            PromotionValidityPeriodDTO outPeriod = new PromotionValidityPeriodDTO();
            outPeriod.setEndDate(in.getValidityPeriod().getEndDate());
            outPeriod.setStartDate(in.getValidityPeriod().getStartDate());
            outPeriod.setType(PromotionValidityType.fromId(in.getValidityPeriod().getType().getId()));
            out.setValidityPeriod(outPeriod);
        }
    }

    private static void surcharges(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out) {
        PromotionSurchargesDTO surcharges = new PromotionSurchargesDTO();
        surcharges.setChannelFees(in.getIncludeChannelSurcharges());
        surcharges.setPromoter(in.getIncludePromoterSurcharges());
        out.setSurcharges(surcharges);
    }

    private static PromotionLimitDTO createPromotionLimit(PromotionLimit in) {
        if (in == null) {
            return null;
        }
        return new PromotionLimitDTO(in.getEnabled(), in.getLimit());
    }

    private static PromotionMaxLimitDTO createPromotionMaxLimit(PromotionMaxLimit in) {
        if (in == null) {
            return null;
        }
        return new PromotionMaxLimitDTO(in.getEnabled(), in.getLimit(), in.getCurrent());
    }

    private static PromotionRangeDTO createRangeDTO(PromotionRange range) {
        return new PromotionRangeDTO(
                range.getFrom(),
                range.getTo(),
                range.getValue());
    }

    private static void collective(PromotionTemplateDetail in, EventPromotionTemplateDetailDTO out) {
        if (in.getCollective() != null) {
            PromotionTemplateCollectiveDTO outCollective = new PromotionTemplateCollectiveDTO();
            outCollective.setId(in.getCollective().getId());
            EventPromotionCollectiveTypeDTO type = in.getCollective().getType() != null
                    ? EventPromotionCollectiveTypeDTO.valueOf(in.getCollective().getType().name())
                    : null;
            outCollective.setType(type);
            outCollective.setBoxOfficeValidation(in.getCollective().getBoxOfficeValidation());
            outCollective.setRestrictiveSale(in.getCollective().getRestrictiveSale());
            out.setCollective(outCollective);
        }
    }

    private static void generalInfo(UpdateEventPromotionTemplateDTO inDto, UpdateEventPromotionDetail outDto) {
        outDto.setCombinable(inDto.getCombinable());
        outDto.setShowTicketDiscountName(inDto.getShowDiscountNameticket());
        outDto.setShowTicketPriceWithouDiscount(inDto.getShowTicketPriceWithoutDiscount());
        outDto.setName(inDto.getName());
        outDto.setFavorite(inDto.getFavorite());
        outDto.setAccesControlRestricted(inDto.getAccesControlRestricted());
        outDto.setPresale(inDto.getPresale());
    }

    private static void validityPeriod(UpdateEventPromotionTemplateDTO inDto, UpdateEventPromotionDetail outDto) {
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

    private static void collective(UpdateEventPromotionTemplateDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getCollective() != null) {
            PromotionTemplateCollectiveDTO inCollective = inDto.getCollective();
            PromotionTemplateCollective outCollective = new PromotionTemplateCollective();
            outCollective.setId(inCollective.getId());
            PromotionCollectiveType type = null;
            if (inCollective.getType() != null) {
                type = PromotionCollectiveType.valueOf(inCollective.getType().name());
            }
            outCollective.setType(type);
            outCollective.setBoxOfficeValidation(inCollective.getBoxOfficeValidation());
            outCollective.setRestrictiveSale(inCollective.getRestrictiveSale());
            outDto.setCollective(outCollective);
        }
    }

    private static void discount(UpdateEventPromotionTemplateDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getDiscount() != null) {
            PromotionDiscountConfigDTO discount = inDto.getDiscount();
            PromotionDiscountConfig outDiscount = new PromotionDiscountConfig();
            outDiscount.setValue(discount.getValue());
            es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType type = null;
            if (discount.getType() != null) {
                type = es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType.valueOf(discount.getType().name());
            }
            outDiscount.setType(type);
            if (es.onebox.mgmt.datasources.ms.promotion.enums.PromotionDiscountType.BASE_PRICE.equals(type) && CollectionUtils.isNotEmpty(discount.getRanges())) {
                outDiscount.setRanges(discount.getRanges().stream()
                        .map(el -> new PromotionRange(el.getFrom(), el.getTo(), el.getValue()))
                        .collect(Collectors.toList()));
            }
            outDto.setDiscount(outDiscount);
        }
    }

    private static void limits(UpdateEventPromotionTemplateDTO inDto, UpdateEventPromotionDetail outDto) {
        if (inDto.getLimits() != null) {
            PromotionLimitsDTO limits = inDto.getLimits();
            PromotionLimits outLimits = new PromotionLimits();
            if (limits.getPacks() != null) {
                outLimits.setPacks(limit(limits.getPacks()));
            }
            if (limits.getPromotionMaxLimit() != null) {
                outLimits.setPromotionMaxLimit(maxLimit(limits.getPromotionMaxLimit()));
            }
            if (limits.getPurchaseMaxLimit() != null) {
                outLimits.setPurchaseMaxLimit(limit(limits.getPurchaseMaxLimit()));
            }
            if (limits.getSessionMaxLimit() != null) {
                outLimits.setSessionMaxLimit(limit(limits.getSessionMaxLimit()));
            }
            if (limits.getPurchaseMinLimit() != null) {
                outLimits.setPurchaseMinLimit(limit(limits.getPurchaseMinLimit()));
            }
            if (limits.getEventUserCollectiveMaxLimit() != null) {
                outLimits.setEventUserCollectiveMaxLimit(limit(limits.getEventUserCollectiveMaxLimit()));
            }
            if (limits.getSessionUserCollectiveMaxLimit() != null) {
                outLimits.setSessionUserCollectiveMaxLimit(limit(limits.getSessionUserCollectiveMaxLimit()));
            }
            outDto.setLimits(outLimits);
        }
    }

    private static PromotionLimit limit(PromotionLimitDTO limit) {
        PromotionLimit inLimit = new PromotionLimit();
        inLimit.setEnabled(limit.getEnabled());
        inLimit.setLimit(limit.getLimit());
        return inLimit;
    }

    public static PromotionMaxLimit maxLimit(PromotionMaxLimitDTO promotionMaxLimit) {
        PromotionMaxLimit inLimit = new PromotionMaxLimit();
        inLimit.setEnabled(promotionMaxLimit.getEnabled());
        inLimit.setLimit(promotionMaxLimit.getLimit());
        inLimit.setCurrent(promotionMaxLimit.getCurrent());
        return inLimit;
    }
}
