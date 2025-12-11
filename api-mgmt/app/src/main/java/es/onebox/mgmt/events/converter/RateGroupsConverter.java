package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatesGroup;
import es.onebox.mgmt.events.dto.CreateEventRatesGroupRequestDTO;
import es.onebox.mgmt.events.dto.RateDTO;
import es.onebox.mgmt.events.dto.RateGroupDTO;
import es.onebox.mgmt.events.dto.RateTextsDTO;
import es.onebox.mgmt.events.dto.UpdateRateGroupDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RateGroupsConverter {

    private RateGroupsConverter() {
    }

    public static List<RateGroupDTO> fromMsEvent(RatesGroup rates) {
        List<RateGroupDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(RateGroupsConverter::fromMsEvent)
                    .collect(Collectors.toList());
        }
        return result;
    }

    private static RateGroupDTO fromMsEvent(RateGroup rateGroup) {
        RateGroupDTO result = null;
        if (rateGroup != null) {
            result = new RateGroupDTO();
            result.setId(rateGroup.getId());
            result.setName(rateGroup.getName());
            result.setDefault(rateGroup.isDefaultRate());
            result.setExternalDescription(rateGroup.getExternalDescription());
            result.setPosition(rateGroup.getPosition());
            if (rateGroup.getTranslations() != null) {
                result.setTexts(new RateTextsDTO(rateGroup.getTranslations().entrySet().stream().
                        collect(Collectors.toMap(
                                entry -> ConverterUtils.toLanguageTag(entry.getKey()),
                                Map.Entry::getValue
                        ))));
            }
        }
        return result;
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

    public static RateGroup toMsEvent(CreateEventRatesGroupRequestDTO source) {
        RateGroup target = new RateGroup();
        target.setName(source.getName());
        target.setExternalDescription(source.getExternalDescription());
        if (source.getTexts() != null) {
            target.setTranslations(source.getTexts().getName().entrySet().stream()
                    .filter(e -> Objects.nonNull(e.getValue()))
                    .collect(Collectors.toMap(
                            entry -> ConverterUtils.toLocale(entry.getKey()),
                            Map.Entry::getValue
                    )));
        }
        return target;
    }

    public static RateGroup toMsEvent(UpdateRateGroupDTO source) {
        RateGroup target = new RateGroup();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setExternalDescription(source.getExternalDescription());
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
}
