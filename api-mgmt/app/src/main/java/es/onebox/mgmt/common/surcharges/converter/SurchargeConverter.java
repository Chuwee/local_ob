package es.onebox.mgmt.common.surcharges.converter;

import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.RangeValueDTO;
import es.onebox.mgmt.common.surcharges.dto.CommonSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.EventSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SeasonTicketSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeLimitDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.common.dto.Range;
import es.onebox.mgmt.datasources.common.dto.RangeValue;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurchargeLimit;
import es.onebox.mgmt.datasources.ms.event.dto.event.SeasonTicketSurcharge;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSurchargeDTO;
import org.apache.commons.collections.CollectionUtils;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SurchargeConverter {

    private SurchargeConverter() {
    }

    public static Surcharge fromDTO(SurchargeDTO source, List<Currency> currencies, Currency defaultCurrency) {
        return fromBaseDTO(source, new Surcharge(), currencies, defaultCurrency);
    }

    public static <T extends Surcharge> T fromBaseDTO(SurchargeDTO source, T target, List<Currency> currencies, Currency defaultCurrency) {
        target.setType(SurchargeType.valueOf(source.getType().name()));
        target.setEnabledRanges(source.getEnabledRanges());
        List<Range> targetRanges = source.getRanges().stream()
                .map(r -> fromDTO(r, currencies, defaultCurrency))
                .collect(Collectors.toList());

        target.setRanges(targetRanges);

        return target;
    }

    public static EventSurcharge fromDTO(EventSurchargeDTO source, List<Currency> currencies, Currency defaultCurrency) {
        EventSurcharge target = fromBaseDTO(source, new EventSurcharge(), currencies, defaultCurrency);

        target.setLimit(new EventSurchargeLimit());
        if (source.getLimit() != null) {
            target.getLimit().setEnabled(source.getLimit().getEnabled());
            target.getLimit().setMin(source.getLimit().getMin());
            target.getLimit().setMax(source.getLimit().getMax());
        }
        target.setAllowChannelUseAlternativeCharges(source.getAllowChannelUseAlternativeCharges());

        return target;
    }

    public static EventSurcharge fromDTO(CommonSurchargeDTO source, List<Currency> currencies, Currency defaultCurrency) {
        EventSurcharge target = fromBaseDTO(source, new EventSurcharge(), currencies, defaultCurrency);

        target.setLimit(new EventSurchargeLimit());
        if (source.getLimit() != null) {
            target.getLimit().setEnabled(source.getLimit().getEnabled());
            target.getLimit().setMin(source.getLimit().getMin());
            target.getLimit().setMax(source.getLimit().getMax());
        }
        return target;
    }

    public static Range fromDTO(RangeDTO source, List<Currency> currencies, Currency defaultCurrency) {
        Range target = new Range();

        target.setFrom(source.getFrom());

        if (target.getValues() == null) {
            target.setValues(new RangeValue());
        }

        target.getValues().setPercentage(source.getValues().getPercentage());
        target.getValues().setMin(source.getValues().getMin());
        target.getValues().setMax(source.getValues().getMax());
        target.getValues().setFixed(source.getValues().getFixed());

        Currency rangeCurrency = currencies.stream()
                .filter(cc -> cc.getCode().equals(source.getCurrency()))
                .findFirst().orElse(null);
        if (rangeCurrency != null) {
            target.setCurrencyId(rangeCurrency.getId());
        } else if (defaultCurrency != null) {
            target.setCurrencyId(defaultCurrency.getId());
        }
        //TODO Add this after adding currencies to season ticket
        /*else {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }*/
        return target;
    }

    public static List<SurchargeDTO> toSurchargeDTO(List<Surcharge> msResponse, List<Currency> currencies, Currency defaultCurrency) {
        return msResponse.stream()
                .map(mr -> toRangeDTO(mr, currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    public static List<SaleRequestSurchargeDTO> toSaleRequestSurchargeDTO(List<MsSaleRequestSurchargesDTO> msResponse,
                                                                          List<Currency> currencies, Currency defaultCurrency) {
        return msResponse.stream()
                .map(mr -> toRangeDTO(mr, currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    public static List<EventSurchargeDTO> toEventSurchargeDTO(List<EventSurcharge> msResponse,
                                                              List<Currency> currencies, Currency defaultCurrency) {
        return msResponse.stream()
                .map(es -> toRangeDTO(es, new EventSurchargeDTO(), currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    public static List<SeasonTicketSurchargeDTO> toSeasonTicketSurchargeDTO(List<SeasonTicketSurcharge> msResponse,
                                                                            List<Currency> currencies, Currency defaultCurrency) {
        return msResponse.stream()
                .map(es -> toRangeDTO(es, new SeasonTicketSurchargeDTO(), currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    public static List<SeasonTicketChannelSurchargeDTO> toSeasonTicketChannelSurchargeDTO(List<EventSurcharge> msResponse,
                                                                                          List<Currency> currencies, Currency defaultCurrency) {
        return msResponse.stream()
                .map(es -> toRangeDTO(es, new SeasonTicketChannelSurchargeDTO(), currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    private static SurchargeDTO toRangeDTO(Surcharge surcharge, List<Currency> currencies, Currency defaultCurrency) {
        return toBaseSurchargeDTO(surcharge, new SurchargeDTO(), currencies, defaultCurrency);
    }

    private static <T extends CommonSurchargeDTO> T toRangeDTO(EventSurcharge eventSurcharge, T target,
                                                               List<Currency> currencies, Currency defaultCurrency) {
        T mainSurchargeDTO = toBaseSurchargeDTO(eventSurcharge, target, currencies, defaultCurrency);
        if (eventSurcharge.getLimit() != null) {
            mainSurchargeDTO.setLimit(new SurchargeLimitDTO());
            mainSurchargeDTO.getLimit().setEnabled(eventSurcharge.getLimit().getEnabled());
            mainSurchargeDTO.getLimit().setMin(eventSurcharge.getLimit().getMin());
            mainSurchargeDTO.getLimit().setMax(eventSurcharge.getLimit().getMax());
        }
        return mainSurchargeDTO;
    }

    private static <T extends EventSurchargeDTO> T toRangeDTO(EventSurcharge eventSurcharge, T target,
                                                              List<Currency> currencies, Currency defaultCurrency) {
        T eventSurchargeDTO = toBaseSurchargeDTO(eventSurcharge, target, currencies, defaultCurrency);
        if (eventSurcharge.getLimit() != null) {
            eventSurchargeDTO.setLimit(new SurchargeLimitDTO());
            eventSurchargeDTO.getLimit().setEnabled(eventSurcharge.getLimit().getEnabled());
            eventSurchargeDTO.getLimit().setMin(eventSurcharge.getLimit().getMin());
            eventSurchargeDTO.getLimit().setMax(eventSurcharge.getLimit().getMax());
        }
        eventSurchargeDTO.setAllowChannelUseAlternativeCharges(eventSurcharge.getAllowChannelUseAlternativeCharges());
        return eventSurchargeDTO;
    }

    private static <T extends SeasonTicketSurchargeDTO> T toRangeDTO(SeasonTicketSurcharge seasonTicketSurcharge, T target,
                                                                     List<Currency> currencies, Currency defaultCurrency) {
        T eventSurchargeDTO = toBaseSurchargeDTO(seasonTicketSurcharge, target, currencies, defaultCurrency);
        if (seasonTicketSurcharge.getLimit() != null) {
            eventSurchargeDTO.setLimit(new SurchargeLimitDTO());
            eventSurchargeDTO.getLimit().setEnabled(seasonTicketSurcharge.getLimit().getEnabled());
            eventSurchargeDTO.getLimit().setMin(seasonTicketSurcharge.getLimit().getMin());
            eventSurchargeDTO.getLimit().setMax(seasonTicketSurcharge.getLimit().getMax());
        }
        return eventSurchargeDTO;
    }

    private static SaleRequestSurchargeDTO toRangeDTO(MsSaleRequestSurchargesDTO saleRequestSurcharge,
                                                      List<Currency> currencies, Currency defaultCurrency) {
        SaleRequestSurchargeDTO surcharge = toBaseSurchargeDTO(saleRequestSurcharge, new SaleRequestSurchargeDTO(), currencies, defaultCurrency);
        if (Objects.nonNull(saleRequestSurcharge.getLimitProducer())) {
            surcharge.setProducerLimit(new SurchargeLimitDTO());
            surcharge.getProducerLimit().setEnabled(saleRequestSurcharge.getLimitProducer().getEnabled());
            surcharge.getProducerLimit().setMin(saleRequestSurcharge.getLimitProducer().getMin());
            surcharge.getProducerLimit().setMax(saleRequestSurcharge.getLimitProducer().getMax());
        }
        return surcharge;
    }

    private static <T extends SurchargeDTO> T toBaseSurchargeDTO(Surcharge msChannelSurchage, T target,
                                                                 List<Currency> currencies, Currency defaultCurrency) {
        List<RangeDTO> ranges = msChannelSurchage.getRanges().stream()
                .map(mscr -> toSurchargeRangeDTO(mscr, currencies, defaultCurrency))
                .collect(Collectors.toList());

        target.setRanges(ranges);
        target.setType(SurchargeTypeDTO.valueOf(msChannelSurchage.getType().name()));
        target.setEnabledRanges(msChannelSurchage.getEnabledRanges());

        return target;
    }

    public static RangeDTO toSurchargeRangeDTO(Range msChannelRange, List<Currency> currencies, Currency defaultCurrency) {
        RangeDTO range = new RangeDTO();
        RangeValueDTO values = new RangeValueDTO();
        values.setFixed(msChannelRange.getValues().getFixed());
        values.setMax(msChannelRange.getValues().getMax());
        values.setMin(msChannelRange.getValues().getMin());
        values.setPercentage(msChannelRange.getValues().getPercentage());
        range.setFrom(msChannelRange.getFrom());
        range.setTo(msChannelRange.getTo());
        range.setValues(values);
        Currency rangeCurrency = currencies.stream()
                .filter(cc -> cc.getId().equals(msChannelRange.getCurrencyId()))
                .findFirst().orElse(null);
        if (rangeCurrency != null) {
            range.setCurrency(rangeCurrency.getCode());
        } else if (defaultCurrency != null) {
            range.setCurrency(defaultCurrency.getCode());
        }
        //TODO Add this after adding currencies to season ticket
        /*else {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }*/
        return range;
    }

    public static List<SurchargeType> toSurchargeTypes(List<SurchargeTypeDTO> types) {
        if (CollectionUtils.isNotEmpty(types)) {
            return types.stream().map(item -> SurchargeType.valueOf(item.name())).collect(Collectors.toList());
        }
        return null;
    }

}
