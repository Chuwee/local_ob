package es.onebox.mgmt.events.converter;

import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRate;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRateDateRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroupData;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatePriceZoneRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRelationsRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateRateRestrictions;
import es.onebox.mgmt.events.dto.CreateEventRateRequestDTO;
import es.onebox.mgmt.events.dto.EventRateDTO;
import es.onebox.mgmt.events.dto.EventRateDateRestrictionDTO;
import es.onebox.mgmt.events.dto.RateDTO;
import es.onebox.mgmt.events.dto.RateGroupDataDTO;
import es.onebox.mgmt.events.dto.RatePriceTypeRestrictionDTO;
import es.onebox.mgmt.events.dto.RateRelationsRestrictionDTO;
import es.onebox.mgmt.events.dto.RateRestrictedDTO;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RateTextsDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.events.dto.UpdateEventRateDTO;
import es.onebox.mgmt.events.dto.UpdateRateDTO;
import es.onebox.mgmt.sessions.dto.SessionRateDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RateConverter {

    private RateConverter() {
    }

    private static final int MULTIPLIER = 1;

    public static EventRateDTO fromMsEvent(EventRate rate) {
        EventRateDTO result = null;
        if (rate != null) {
            result = new EventRateDTO();
            result.setId(rate.getId());
            result.setName(rate.getName());
            result.setIsDefault(rate.isDefaultRate());
            result.setRestrictiveAccess(rate.isRestrictive());
            result.setRateGroup(toDTO(rate.getRateGroup()));
            result.setPosition(rate.getPosition());
            if (rate.getTranslations() != null) {
                result.setTexts(convertToRateText(rate.getTranslations()));
            }
            result.setExternalRateType(rate.getExternalRateType());
        }
        return result;
    }

    public static es.onebox.mgmt.sessions.dto.RateDTO fromMs(EventRate rate) {
        es.onebox.mgmt.sessions.dto.RateDTO result = null;
        if (rate != null) {
            result = new es.onebox.mgmt.sessions.dto.RateDTO();
            result.setId(rate.getId());
            result.setName(rate.getName());
            result.setDefaultRate(rate.isDefaultRate());
        }
        return result;
    }

    public static SessionRateDTO fromMsSessionRate(EventRate rate) {
        SessionRateDTO result = null;
        if (rate != null) {
            result = new SessionRateDTO();
            result.setId(rate.getId());
            result.setName(rate.getName());
            result.setIsDefault(rate.isDefaultRate());
            result.setRateGroupDataDTO(
                    new RateGroupDataDTO(
                            rate.getRateGroup().getId(),
                            rate.getRateGroup().getName()
                    )
            );
        }
        return result;
    }

    private static RateTextsDTO convertToRateText(Map<String, String> translations) {
        if (MapUtils.isNotEmpty(translations)) {
            return new RateTextsDTO(translations.entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLanguageTag(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        return null;
    }

    public static RateDTO fromMsEvent(Rate rate) {
        RateDTO result = null;
        if (rate != null) {
            result = new RateDTO();
            result.setId(rate.getId());
            result.setName(rate.getName());
            result.setIsDefault(rate.isDefaultRate());
            result.setRestrictiveAccess(rate.isRestrictive());
            if (rate.getTranslations() != null) {
                result.setTexts(new RateTextsDTO(rate.getTranslations().entrySet().stream().
                        collect(Collectors.toMap(
                                entry -> ConverterUtils.toLanguageTag(entry.getKey()),
                                Map.Entry::getValue
                        ))));
            }
        }
        return result;
    }

    public static Rate toMsEvent(CreateEventRateRequestDTO source) {
        Rate target = new Rate();
        target.setName(source.getName());
        target.setDefaultRate(CommonUtils.isTrue(source.getDefaultRate()));
        target.setRestrictive(CommonUtils.isTrue(source.getRestrictiveAccess()));
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        if (source.getExternalRateTypeId() != null) {
            target.setExternalRateType(new IdNameCodeDTO(source.getExternalRateTypeId(), null, null));
        }
        return target;
    }

    public static Rate toMsEvent(UpdateRateDTO source) {
        Rate target = new Rate();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDefaultRate(source.getDefault());
        target.setRestrictive(source.getRestrictiveAccess());
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        if (source.getExternalRateTypeId() != null) {
            target.setExternalRateType(new IdNameCodeDTO(source.getExternalRateTypeId()));
        }
        return target;
    }

    public static Rate toMsEvent(UpdateEventRateDTO source) {
        Rate target = new Rate();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDefaultRate(source.getDefault());
        target.setRestrictive(source.getRestrictiveAccess());
        target.setPosition(source.getPosition());
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        if (source.getExternalRateTypeId() != null) {
            target.setExternalRateType(new IdNameCodeDTO(source.getExternalRateTypeId()));
        }
        return target;
    }

    private static RateGroupDataDTO toDTO(RateGroupData rateGroupData) {
        if(rateGroupData == null) {
            return null;
        }
        RateGroupDataDTO rateGroupDataDTO = new RateGroupDataDTO();
        rateGroupDataDTO.setId(rateGroupData.getId());
        rateGroupDataDTO.setName(rateGroupData.getName());
        return rateGroupDataDTO;
    }

    public static RateRestrictionDTO fromMsEvent(RateRestrictions restriction) {
        RateRestrictionDTO result = new RateRestrictionDTO();
        if (restriction == null) {
            return result;
        }
        result.setDateRestrictionEnabled(restriction.getDateRestrictionEnabled());
        result.setDateRestriction(toDTO(restriction.getDateRestriction()));
        result.setCustomerTypeRestrictionEnabled(restriction.getCustomerTypeRestrictionEnabled());
        if (restriction.getCustomerTypeRestriction() != null) {
            result.setCustomerTypeRestriction(restriction.getCustomerTypeRestriction().stream().map(IdCodeDTO::getId).toList());
        }
        result.setRateRelationsRestriction(toDTO(restriction.getRateRelationsRestriction()));
        result.setRateRelationsRestrictionEnabled(restriction.getRateRelationsRestrictionEnabled());
        result.setPriceTypeRestrictionEnabled(restriction.getPriceZoneRestrictionEnabled());
        result.setPriceTypeRestriction(toDTO(restriction.getRatePriceZoneRestriction()));
        result.setChannelRestrictionEnabled(restriction.getChannelRestrictionEnabled());
        result.setChannelRestriction(restriction.getChannelRestriction());
        result.setPeriodRestrictionEnabled(restriction.getPeriodRestrictionEnabled());
        result.setPeriodRestriction(restriction.getPeriodRestriction());
        result.setMaxItemRestrictionEnabled(restriction.getMaxItemRestrictionEnabled());
        result.setMaxItemRestriction(restriction.getMaxItemRestriction());

        return result;
    }


    private static EventRateDateRestrictionDTO toDTO(EventRateDateRestriction dateRestriction){
        if (dateRestriction == null) {
            return null;
        }

        EventRateDateRestrictionDTO dateRestrictionDTO = new EventRateDateRestrictionDTO();
        dateRestrictionDTO.setFrom(dateRestriction.getFrom());
        dateRestrictionDTO.setTo(dateRestriction.getTo());

        return dateRestrictionDTO;
    }

    private static RateRelationsRestrictionDTO toDTO(RateRelationsRestriction rateRelationsRestriction){
        if(rateRelationsRestriction == null || rateRelationsRestriction.isEmpty()) {
            return null;
        }

        RateRelationsRestrictionDTO rateRelationsRestrictionDTO = new RateRelationsRestrictionDTO();
        rateRelationsRestrictionDTO.setRequiredRateIds(rateRelationsRestriction.getRequiredRates());
        rateRelationsRestrictionDTO.setRestrictedPriceTypeIds(rateRelationsRestriction.getRestrictedPriceZones());
        rateRelationsRestrictionDTO.setPriceZoneCriteria(rateRelationsRestriction.getPriceZoneCriteria());
        rateRelationsRestrictionDTO.setApplyToB2b(rateRelationsRestriction.getApplyToB2b());

        if (rateRelationsRestriction.getMaxItemsMultiplier()!= null && rateRelationsRestriction.getMaxItemsMultiplier() >= 1) {
            rateRelationsRestrictionDTO.setLockedTicketsNumber(rateRelationsRestriction.getMaxItemsMultiplier().intValue());
        } else if(rateRelationsRestriction.getMaxItemsMultiplier() != null) {
            rateRelationsRestrictionDTO.setRequiredTicketsNumber((int)(MULTIPLIER / rateRelationsRestriction.getMaxItemsMultiplier()));
        }

        rateRelationsRestrictionDTO.setUseAllZonePrices(CollectionUtils.isEmpty(rateRelationsRestriction.getRestrictedPriceZones()));

        return rateRelationsRestrictionDTO;
    }

    private static RatePriceTypeRestrictionDTO toDTO(RatePriceZoneRestriction ratePriceZoneRestriction){
        if (ratePriceZoneRestriction == null || ratePriceZoneRestriction.isEmpty()) {
            return null;
        }

        RatePriceTypeRestrictionDTO ratePriceZoneRestrictionDTO = new RatePriceTypeRestrictionDTO();
        ratePriceZoneRestrictionDTO.setApplyToB2B(BooleanUtils.isTrue(ratePriceZoneRestriction.getApplyToB2b()));

        if (CollectionUtils.isNotEmpty(ratePriceZoneRestriction.getRestrictedPriceZoneIds())) {
            ratePriceZoneRestrictionDTO.setRestrictedPriceTypeIds(ratePriceZoneRestriction.getRestrictedPriceZoneIds());
        }

        return ratePriceZoneRestrictionDTO;
    }

    public static RatesRestrictedDTO fromMsEvent(List<RateRestricted> restrictedRates) {
        RatesRestrictedDTO ratesRestrictedDTO = new RatesRestrictedDTO();
        Metadata metadata = new Metadata();
        if(restrictedRates != null) {
            ratesRestrictedDTO.setData(convert(restrictedRates));
            metadata.setTotal((long) restrictedRates.size());
        }
        metadata.setOffset(0L);
        metadata.setLimit(1000L);
        ratesRestrictedDTO.setMetadata(metadata);
        return ratesRestrictedDTO;
    }

    private static List<RateRestrictedDTO> convert(List<RateRestricted> restrictedRates) {
        if (CollectionUtils.isEmpty(restrictedRates)) {
            return Collections.emptyList();
        }
        return restrictedRates.stream().map(RateConverter::convert).collect(Collectors.toList());
    }

    private static RateRestrictedDTO convert(RateRestricted restrictedRates) {
        RateRestrictedDTO rateRestrictedDTO = new RateRestrictedDTO();
        rateRestrictedDTO.setRate(restrictedRates.getRate());
        rateRestrictedDTO.setRestrictions(fromMsEvent(restrictedRates.getRestrictions()));
        return rateRestrictedDTO;
    }

    public static UpdateRateRestrictions toMsEvent(RateRestrictionDTO restrictionDTO) {
        UpdateRateRestrictions result = new UpdateRateRestrictions();
        if (restrictionDTO == null) {
            return result;
        }

        result.setDateRestrictionEnabled(restrictionDTO.getDateRestrictionEnabled());
        if (ObjectUtils.isNotEmpty(restrictionDTO.getDateRestriction())) {
            EventRateDateRestriction dateRestriction = new EventRateDateRestriction();
            dateRestriction.setFrom(restrictionDTO.getDateRestriction().getFrom());
            dateRestriction.setTo(restrictionDTO.getDateRestriction().getTo());
            result.setDateRestriction(dateRestriction);
        }
        result.setCustomerTypeRestrictionEnabled(restrictionDTO.getCustomerTypeRestrictionEnabled());
        result.setCustomerTypeRestriction(restrictionDTO.getCustomerTypeRestriction());
        result.setRateRelationsRestrictionEnabled(restrictionDTO.getRateRelationsRestrictionEnabled());
        result.setRateRelationsRestriction(toMsEvent(restrictionDTO.getRateRelationsRestriction()));
        result.setPriceZoneRestrictionEnabled(restrictionDTO.getPriceTypeRestrictionEnabled());
        result.setPriceZoneRestriction(toMsEvent(restrictionDTO.getPriceTypeRestriction()));
        result.setChannelRestrictionEnabled(restrictionDTO.getChannelRestrictionEnabled());
        result.setChannelRestriction(restrictionDTO.getChannelRestriction());
        result.setPeriodRestrictionEnabled(restrictionDTO.getPeriodRestrictionEnabled());
        result.setPeriodRestriction(restrictionDTO.getPeriodRestriction());
        result.setMaxItemRestrictionEnabled(restrictionDTO.getMaxItemRestrictionEnabled());
        result.setMaxItemRestriction(restrictionDTO.getMaxItemRestriction());
        return result;
    }

    public static RateRelationsRestriction toMsEvent(RateRelationsRestrictionDTO rateRelationsRestrictionDTO){
        if (rateRelationsRestrictionDTO == null) {
            return null;
        }

        RateRelationsRestriction rateRelationsRestriction = new RateRelationsRestriction();
        rateRelationsRestriction.setRequiredRates(rateRelationsRestrictionDTO.getRequiredRateIds());
        if (BooleanUtils.isNotTrue(rateRelationsRestrictionDTO.getUseAllZonePrices())) {
            rateRelationsRestriction.setRestrictedPriceZones(rateRelationsRestrictionDTO.getRestrictedPriceTypeIds());
        }

        if (rateRelationsRestrictionDTO.getRequiredTicketsNumber() != null) {
            rateRelationsRestriction.setMaxItemsMultiplier(MULTIPLIER / rateRelationsRestrictionDTO.getRequiredTicketsNumber().doubleValue());
        } else if (rateRelationsRestrictionDTO.getLockedTicketsNumber() != null) {
            rateRelationsRestriction.setMaxItemsMultiplier(rateRelationsRestrictionDTO.getLockedTicketsNumber().doubleValue());
        }
        rateRelationsRestriction.setPriceZoneCriteria(rateRelationsRestrictionDTO.getPriceZoneCriteria());
        rateRelationsRestriction.setApplyToB2b(rateRelationsRestrictionDTO.getApplyToB2b());

        return rateRelationsRestriction;
    }



    public static RatePriceZoneRestriction toMsEvent(RatePriceTypeRestrictionDTO ratePriceTypeRestrictionDTO){
        if (ratePriceTypeRestrictionDTO == null) {
            return null;
        }

        RatePriceZoneRestriction rateRelationsRestriction = new RatePriceZoneRestriction();
        rateRelationsRestriction.setApplyToB2b(BooleanUtils.isTrue(ratePriceTypeRestrictionDTO.getApplyToB2B()));
        rateRelationsRestriction.setRestrictedPriceZoneIds(ratePriceTypeRestrictionDTO.getRestrictedPriceTypeIds());
        return rateRelationsRestriction;
    }
}
