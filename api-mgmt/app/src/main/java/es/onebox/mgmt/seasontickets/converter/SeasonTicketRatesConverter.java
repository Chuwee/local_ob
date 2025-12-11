package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRates;
import es.onebox.mgmt.events.dto.RateTextsDTO;
import es.onebox.mgmt.seasontickets.dto.rates.CreateSeasonTicketRateRequestDTO;
import es.onebox.mgmt.seasontickets.dto.rates.SeasonTicketRateDTO;
import es.onebox.mgmt.seasontickets.dto.rates.UpdateSeasonTicketRateDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SeasonTicketRatesConverter {

    private SeasonTicketRatesConverter() {
    }

    public static List<SeasonTicketRateDTO> fromMsEvent(SeasonTicketRates rates) {
        List<SeasonTicketRateDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(SeasonTicketRatesConverter::fromMsEvent)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static SeasonTicketRateDTO fromMsEvent(SeasonTicketRate rate) {
        SeasonTicketRateDTO result = null;
        if (rate != null) {
            result = new SeasonTicketRateDTO();
            result.setId(rate.getId());
            result.setName(rate.getName());
            result.setIsDefault(rate.isDefaultRate());
            result.setRestrictiveAccess(rate.isRestrictive());
            result.setEnabled(rate.getEnabled());
            result.setPosition(rate.getPosition());
            if (rate.getTranslations() != null) {
                result.setTexts(new RateTextsDTO(rate.getTranslations().entrySet().stream().
                        collect(Collectors.toMap(
                                entry -> ConverterUtils.toLanguageTag(entry.getKey()),
                                Map.Entry::getValue
                        ))));
            }
            result.setExternalRateType(rate.getExternalRateType());
        }
        return result;
    }

    public static SeasonTicketRate toMsEvent(UpdateSeasonTicketRateDTO source) {
        SeasonTicketRate target = new SeasonTicketRate();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDefaultRate(source.getDefault());
        target.setRestrictive(source.getRestrictiveAccess());
        target.setEnabled(source.getEnabled());
        target.setPosition(source.getPosition());
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        return target;
    }

    public static SeasonTicketRate toMsEvent(CreateSeasonTicketRateRequestDTO source) {
        SeasonTicketRate target = new SeasonTicketRate();
        target.setName(source.getName());
        target.setDefaultRate(CommonUtils.isTrue(source.getDefaultRate()));
        target.setRestrictive(CommonUtils.isTrue(source.getRestrictiveAccess()));
        target.setEnabled(source.getEnabled());
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream().
                    collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        target.setExternalRateType(new IdNameCodeDTO(source.getExternalRateTypeId()));
        return target;
    }
}
