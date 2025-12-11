package es.onebox.mgmt.channels.promotions;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.dto.ChannelPromotionDetailDTO;
import es.onebox.mgmt.channels.promotions.dto.AlternativeSurchargesDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionAmountDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionCollectiveDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionCollectiveDetailDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDiscountDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionEventDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionLimitsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPacksDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPeriodDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPriceTypeDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionSessionDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsDTO;
import es.onebox.mgmt.channels.promotions.dto.CreateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.SessionDateDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionDiscountType;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionSubtypeDTO;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionType;
import es.onebox.mgmt.common.AmountCurrencyDTO;
import es.onebox.mgmt.common.promotions.converter.PromotionConverter;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.promotion.dto.AmountCurrency;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.AlternativeSurcharges;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionAmount;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionCollective;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionCollectiveDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDiscount;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvent;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionLimits;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPeriod;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceType;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSession;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.CreateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.SessionDate;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionActivationStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelPromotionConverter {

    private ChannelPromotionConverter() {
    }

    public static ChannelPromotionDTO fromMsPromotion(ChannelPromotion channelPromo) {
        ChannelPromotionDTO dto = new ChannelPromotionDTO();
        toChannelPromotionBaseDTO(channelPromo, dto);
        return dto;
    }

    public static ChannelPromotionDetailDTO fromMsPromotionDetail(ChannelPromotionDetail channelPromotionDetail,
                                                                  boolean multicurrencyAllowed, List<Currency> currencies,
                                                                  Currency defaultCurrency) {
        ChannelPromotionDetailDTO dto = new ChannelPromotionDetailDTO();
        toChannelPromotionBaseDTO(channelPromotionDetail, dto);
        dto.setCombinable(channelPromotionDetail.getCombinable());
        toCollectiveDTO(dto, channelPromotionDetail.getCollective());
        toDiscountDTO(dto, channelPromotionDetail.getDiscount(), multicurrencyAllowed, currencies, defaultCurrency);
        toLimitsDTO(dto, channelPromotionDetail.getLimits(), multicurrencyAllowed, currencies, defaultCurrency);
        toPacksDTO(dto, channelPromotionDetail.getPacks());
        return dto;
    }

    private static void toPacksDTO(ChannelPromotionDetailDTO dto, ChannelPromotionPacks packs) {
        if (Objects.nonNull(packs)) {
            dto.setPacks(new ChannelPromotionPacksDTO(packs.getEnabled(), packs.getEvents(), packs.getSessions()));
        }
    }

    public static ChannelPromotionsDTO fromMsPromotions(ChannelPromotions promotions) {
        ChannelPromotionsDTO promotionDTO = new ChannelPromotionsDTO();
        promotionDTO.setData(promotions.getData().stream()
                .map(ChannelPromotionConverter::fromMsPromotion)
                .collect(Collectors.toList()));
        promotionDTO.setMetadata(promotions.getMetadata());
        return promotionDTO;
    }

    public static CreateChannelPromotion toMsCreatePromotion(CreateChannelPromotionDTO source) {
        CreateChannelPromotion target = new CreateChannelPromotion();
        target.setName(source.getName());
        target.setType(es.onebox.mgmt.datasources.ms.promotion.enums.ChannelPromotionType.
                valueOf(source.getType().name()));
        return target;
    }

    public static UpdateChannelPromotion toMsUpdatePromotion(UpdateChannelPromotionDTO source, Boolean multicurrencyAllowed, List<Currency> currencies) {
        UpdateChannelPromotion target = new UpdateChannelPromotion();
        target.setName(source.getName());
        target.setCombinable(source.getCombinable());
        target.setBlockSecondaryMarketSale(source.getBlockSecondaryMarketSale());
        toCollective(target, source.getCollective());
        toValidityPeriod(target, source.getValidityPeriod());
        toDiscount(target, source.getDiscount(), multicurrencyAllowed, currencies);
        if (source.getStatus() != null) {
            target.setStatus(PromotionActivationStatus.fromId(source.getStatus().getId()));
        }
        toLimits(target, source.getLimits(), multicurrencyAllowed, currencies);
        toPacks(target, source.getPacks());
        toAlternativeSurcharges(target, source.getAlternativeSurcharges());

        return target;
    }

    private static void toPacks(UpdateChannelPromotion target, ChannelPromotionPacksDTO packs) {
        if (Objects.nonNull(packs)) {
           target.setPacks(new ChannelPromotionPacks(packs.getEnabled(), packs.getEvents(), packs.getSessions()));
        }
    }

    private static void toAlternativeSurcharges(UpdateChannelPromotion target,
                                                AlternativeSurchargesDTO alternativeSurcharges) {
        if (Objects.nonNull(alternativeSurcharges)) {
            target.setAlternativeSurcharges(new AlternativeSurcharges(alternativeSurcharges.getUseAlternativeSurcharges(),
                    alternativeSurcharges.getUseAlternativePromoterSurcharges()));
        }
    }

    public static ChannelPromotionEventsDTO fromMsPromotionEventScope(ChannelPromotionEvents scopes) {
        ChannelPromotionEventsDTO events = new ChannelPromotionEventsDTO();
        if(!CollectionUtils.isEmpty(scopes.getEvents())) {
            events.setEvents(toChannelPromotionEvents(scopes.getEvents()));
        }
        events.setType(scopes.getType());
        return events;
    }

    public static UpdateChannelPromotionEvents toMsPromotionEventScope(UpdateChannelPromotionEventsDTO request) {
        UpdateChannelPromotionEvents update = new UpdateChannelPromotionEvents();
        update.setEvents(request.getData());
        update.setType(request.getType());
        return update;
    }

    public static ChannelPromotionSessionsDTO fromMsPromotionSessionScope(ChannelPromotionSessions scopes) {
        ChannelPromotionSessionsDTO sessions = new ChannelPromotionSessionsDTO();
        if(!CollectionUtils.isEmpty(scopes.getSessions())) {
            sessions.setSessions(toChannelPromotionSessions(scopes.getSessions()));
        }
        sessions.setType(scopes.getType());
        return sessions;
    }

    public static UpdateChannelPromotionSessions toMsPromotionSessionScope(UpdateChannelPromotionSessionsDTO request) {
        UpdateChannelPromotionSessions update = new UpdateChannelPromotionSessions();
        update.setSessions(request.getData());
        update.setType(request.getType());
        return update;
    }

    private static void toChannelPromotionBaseDTO(ChannelPromotion channelPromo, ChannelPromotionDTO dto) {
        dto.setId(channelPromo.getId().intValue());
        dto.setName(channelPromo.getName());
        dto.setStatus(PromotionStatus.valueOf(channelPromo.getStatus().name()));
        dto.setType(ChannelPromotionType.valueOf(channelPromo.getType().name()));
        dto.setBlockSecondaryMarketSale(channelPromo.getBlockSecondaryMarketSale());
        if (channelPromo.getSubtype() != null) {
            dto.setSubtype(ChannelPromotionSubtypeDTO.valueOf(channelPromo.getSubtype().name()));
        }
        toValidityPeriodDTO(dto, channelPromo.getValidityPeriod());
        toAlternativeSurchargesDTO(dto, channelPromo.getAlternativeSurcharges());
    }

    private static void toAlternativeSurchargesDTO(ChannelPromotionDTO dto, AlternativeSurcharges alternativeSurcharges) {
        if (alternativeSurcharges != null) {
            AlternativeSurchargesDTO alternativeSurchargesDTO = new AlternativeSurchargesDTO();

            alternativeSurchargesDTO.setUseAlternativeSurcharges(alternativeSurcharges.getUseAlternativeSurcharges());
            alternativeSurchargesDTO.setUseAlternativePromoterSurcharges(alternativeSurcharges.getUseAlternativePromoterSurcharges());
            dto.setAlternativeSurcharges(alternativeSurchargesDTO);
        }
    }

    private static void toCollectiveDTO(ChannelPromotionDetailDTO dto, ChannelPromotionCollectiveDetail collective) {
        if(collective != null) {
            ChannelPromotionCollectiveDetailDTO channelPromotionCollectiveDTO = new ChannelPromotionCollectiveDetailDTO();
            channelPromotionCollectiveDTO.setId(collective.getId());
            channelPromotionCollectiveDTO.setName(collective.getName());
            channelPromotionCollectiveDTO.setStatus(collective.getStatus());
            channelPromotionCollectiveDTO.setType(collective.getType());
            channelPromotionCollectiveDTO.setValidationMethod(collective.getValidationMethod());
            dto.setCollective(channelPromotionCollectiveDTO);
        }
    }

    private static void toCollective(UpdateChannelPromotion target, ChannelPromotionCollectiveDTO collectiveDTO) {
        if(collectiveDTO != null) {
            ChannelPromotionCollective channelPromotionCollective = new ChannelPromotionCollective();
            channelPromotionCollective.setId(collectiveDTO.getId());
            target.setCollective(channelPromotionCollective);
        }
    }

    private static void toValidityPeriodDTO(ChannelPromotionDTO dto, ChannelPromotionPeriod validityPeriod) {
        if(dto != null) {
            ChannelPromotionPeriodDTO channelPromotionPeriodDTO = new ChannelPromotionPeriodDTO();
            channelPromotionPeriodDTO.setType(validityPeriod.getType());
            channelPromotionPeriodDTO.setStartDate(validityPeriod.getStartDate());
            channelPromotionPeriodDTO.setEndDate(validityPeriod.getEndDate());
            dto.setValidityPeriod(channelPromotionPeriodDTO);
        }
    }

    private static void toValidityPeriod(UpdateChannelPromotion target, ChannelPromotionPeriodDTO validityPeriodDTO) {
        if(validityPeriodDTO != null) {
            ChannelPromotionPeriod channelPromotionPeriod = new ChannelPromotionPeriod();
            channelPromotionPeriod.setType(validityPeriodDTO.getType());
            channelPromotionPeriod.setStartDate(validityPeriodDTO.getStartDate());
            channelPromotionPeriod.setEndDate(validityPeriodDTO.getEndDate());
            target.setValidityPeriod(channelPromotionPeriod);
        }
    }

    private static void toDiscountDTO(ChannelPromotionDTO target, ChannelPromotionDiscount discount,
                                      boolean multicurrencyAllowed, List<Currency> currencies, Currency defaultCurrency) {
        if(discount != null) {
            ChannelPromotionDiscountDTO channelPromotionDiscountDTO = new ChannelPromotionDiscountDTO();
            channelPromotionDiscountDTO.setType(discount.getType());
            if (multicurrencyAllowed && (discount.getFixedValues()!= null || discount.getPercentageValue() != null)) {
                if (ChannelPromotionDiscountType.FIXED.equals(channelPromotionDiscountDTO.getType())) {
                    channelPromotionDiscountDTO.setFixedValues(toAmountCurrencyDTO(discount.getFixedValues(), currencies, defaultCurrency));
                } else {
                    channelPromotionDiscountDTO.setPercentageValue(discount.getPercentageValue());
                }
            } else {
                channelPromotionDiscountDTO.setValue(discount.getValue());
            }
            target.setDiscount(channelPromotionDiscountDTO);
        }
    }

    private static void toDiscount(UpdateChannelPromotion target, ChannelPromotionDiscountDTO discountDTO,
                                   Boolean multicurrencyAllowed, List<Currency> currencies) {
        if(discountDTO != null) {
            ChannelPromotionDiscount discount = new ChannelPromotionDiscount();
            discount.setType(discountDTO.getType());
            if(BooleanUtils.isTrue(multicurrencyAllowed) &&  ChannelPromotionDiscountType.FIXED.equals(discountDTO.getType())){
                discount.setFixedValues(toAmountCurrency(discountDTO.getFixedValues(),currencies));
            } else if (BooleanUtils.isTrue(multicurrencyAllowed) &&  ChannelPromotionDiscountType.PERCENTAGE.equals(discountDTO.getType()))  {
                discount.setPercentageValue(discountDTO.getPercentageValue());
            } else {
                discount.setValue(discountDTO.getValue());
            }
            target.setDiscount(discount);
        }
    }

    private static void toLimitsDTO(ChannelPromotionDetailDTO target, ChannelPromotionLimits limits, boolean multicurrencyAllowed,
                                    List<Currency> currencies, Currency defaultCurrency) {
        if(limits != null) {
            ChannelPromotionLimitsDTO channelPromotionLimitsDTO = new ChannelPromotionLimitsDTO();
            channelPromotionLimitsDTO.setAmountMinLimit(createAmountLimit(limits.getAmountMinLimit(), multicurrencyAllowed, currencies, defaultCurrency));
            channelPromotionLimitsDTO.setPurchaseMinLimit(PromotionConverter.createPromotionLimit(limits.getPurchaseMinLimit()));
            channelPromotionLimitsDTO.setPromotionMaxLimit(PromotionConverter.createPromotionMaxLimit(limits.getPromotionMaxLimit()));
            target.setLimits(channelPromotionLimitsDTO);
        }
    }

    private static void toLimits(UpdateChannelPromotion target, ChannelPromotionLimitsDTO limitsDTO,
                                 Boolean multicurrencyAllowed, List<Currency> currencies) {
        if(limitsDTO != null) {
            ChannelPromotionLimits limits = new ChannelPromotionLimits();
            if(limitsDTO.getPromotionMaxLimit() != null) {
                limits.setPromotionMaxLimit(PromotionConverter.maxLimit(limitsDTO.getPromotionMaxLimit()));
            }
            if(limitsDTO.getPurchaseMinLimit() != null) {
                limits.setPurchaseMinLimit(PromotionConverter.limit(limitsDTO.getPurchaseMinLimit()));
            }
            if(limitsDTO.getAmountMinLimit() != null) {
                limits.setAmountMinLimit(channelPromotionAmount(limitsDTO.getAmountMinLimit(), multicurrencyAllowed, currencies));
            }
            target.setLimits(limits);
        }
    }

    private static ChannelPromotionAmount channelPromotionAmount(ChannelPromotionAmountDTO amountMin,
                                                                 Boolean multicurrencyAllowed, List<Currency> currencies) {
        ChannelPromotionAmount channelPromotionAmount = new ChannelPromotionAmount();
        channelPromotionAmount.setEnabled(amountMin.getEnabled());
        if(BooleanUtils.isTrue(multicurrencyAllowed)){
            channelPromotionAmount.setValues(toAmountCurrency(amountMin.getValues(), currencies));
        } else {
            channelPromotionAmount.setAmount(amountMin.getAmount());
        }
        return channelPromotionAmount;
    }

    private static ChannelPromotionAmountDTO createAmountLimit(ChannelPromotionAmount amountMin, boolean multicurrencyAllowed,
                                                               List<Currency> currencies, Currency defaultCurrency) {
        if (amountMin == null) {
            return null;
        }
        if(multicurrencyAllowed && amountMin.getValues() != null) {
            ChannelPromotionAmountDTO amountLimit = new ChannelPromotionAmountDTO();
            amountLimit.setEnabled(amountMin.getEnabled());
            amountLimit.setValues(toAmountCurrencyDTO(amountMin.getValues(), currencies, defaultCurrency));
            return amountLimit;
        } else {
            return new ChannelPromotionAmountDTO(amountMin.getEnabled(), amountMin.getAmount());
        }
    }

    public static CreateChannelPromotion toMsClonePromotion(Long promotionId) {
        CreateChannelPromotion cloneChannelPromotion = new CreateChannelPromotion();
        cloneChannelPromotion.setId(promotionId);
        return cloneChannelPromotion;
    }

    private static Set<ChannelPromotionEventDTO> toChannelPromotionEvents(Set<ChannelPromotionEvent> events) {
        return events.stream().map(ChannelPromotionConverter::toChannelPromotionEvent).collect(Collectors.toSet());
    }

    private static ChannelPromotionEventDTO toChannelPromotionEvent(ChannelPromotionEvent channelPromotionEvent) {
        ChannelPromotionEventDTO dto = new ChannelPromotionEventDTO();
        dto.setId(channelPromotionEvent.getId());
        dto.setName(channelPromotionEvent.getName());
        dto.setStartDate(channelPromotionEvent.getStartDate());
        dto.setCatalogSaleRequestId(channelPromotionEvent.getCatalogSaleRequestId());
        return dto;
    }

    private static Set<ChannelPromotionSessionDTO> toChannelPromotionSessions(Set<ChannelPromotionSession> sessions) {
        return sessions.stream().map(ChannelPromotionConverter::toChannelPromotionSession).collect(Collectors.toSet());
    }

    private static ChannelPromotionSessionDTO toChannelPromotionSession(ChannelPromotionSession channelPromotionSession) {
        ChannelPromotionSessionDTO dto = new ChannelPromotionSessionDTO();
        dto.setId(channelPromotionSession.getId());
        dto.setName(channelPromotionSession.getName());
        dto.setDates(toDate(channelPromotionSession.getDate()));
        dto.setCatalogSaleRequestId(channelPromotionSession.getCatalogSaleRequestId());
        dto.setType(channelPromotionSession.getType());
        return dto;
    }

    private static SessionDateDTO toDate(SessionDate date) {
        SessionDateDTO result = new SessionDateDTO();
        result.setStart(date.getStart());
        result.setEnd(date.getEnd());
        return result;
    }

    public static List<ChannelPromotionPriceTypeDTO> convertToChannelPromotionPriceTypesDto(List<ChannelPromotionPriceType> priceTypes) {
        if (CollectionUtils.isNotEmpty(priceTypes)) {
            return priceTypes.stream().map(ChannelPromotionConverter::convertToChannelPromotionPriceTypeDto).collect(Collectors.toList());
        }
        return null;
    }

    private static ChannelPromotionPriceTypeDTO convertToChannelPromotionPriceTypeDto(ChannelPromotionPriceType channelPromotionPriceType) {
        ChannelPromotionPriceTypeDTO priceTypeDto = null;
        if (Objects.nonNull(channelPromotionPriceType)) {
            priceTypeDto = new ChannelPromotionPriceTypeDTO();
            priceTypeDto.setId(channelPromotionPriceType.getId());
            priceTypeDto.setName(channelPromotionPriceType.getName());
            priceTypeDto.setCatalogSaleRequestId(channelPromotionPriceType.getCatalogSaleRequestId());
            priceTypeDto.setVenueConfig(convertToIdNameDto(channelPromotionPriceType.getVenueConfig()));
        }
        return priceTypeDto;
    }

    private static IdNameDTO convertToIdNameDto(IdNameDTO venueConfig) {
        if (Objects.nonNull(venueConfig)) {
            return new IdNameDTO(venueConfig.getId(), venueConfig.getName());
        }
        return null;
    }

    private static List<AmountCurrencyDTO> toAmountCurrencyDTO(List<AmountCurrency> inValues, List<Currency> currencies,
                                                               Currency defaultCurrency) {
        List<AmountCurrencyDTO> values = new ArrayList<>();
        for(AmountCurrency inValue : inValues){
            AmountCurrencyDTO out = new AmountCurrencyDTO();
            out.setAmount(inValue.getAmount());
            // TODO delete currencyId and use inValue.getCurrencyId() for the currency code search when all promotions has been migrated to multicurrency usage
            Long currencyId = inValue.getCurrencyId() != null ?  inValue.getCurrencyId() : defaultCurrency.getId();
            out.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, currencyId));
            values.add(out);
        }
        return values;
    }

    private static List<AmountCurrency> toAmountCurrency(List<AmountCurrencyDTO> inValues, List<Currency> currencies) {
        List<AmountCurrency> values = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(inValues)) {
            for (AmountCurrencyDTO inValue : inValues) {
                AmountCurrency out = new AmountCurrency();
                out.setAmount(inValue.getAmount());
                out.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, inValue.getCurrencyCode()));
                values.add(out);
            }
        }
        return values;
    }
}
