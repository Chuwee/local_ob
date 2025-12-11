package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rates;
import es.onebox.mgmt.events.dto.EventRateDTO;
import es.onebox.mgmt.events.dto.RateDTO;
import es.onebox.mgmt.sessions.dto.SessionRateDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RatesConverter {

    private RatesConverter() {
    }

    public static List<EventRateDTO> fromMsEvent(EventRates rates) {
        List<EventRateDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(RateConverter::fromMsEvent)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<es.onebox.mgmt.sessions.dto.RateDTO> fromMs(EventRates rates) {
        List<es.onebox.mgmt.sessions.dto.RateDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(RateConverter::fromMs)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<SessionRateDTO> fromMsSessionRate(EventRates rates) {
        List<SessionRateDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(RateConverter::fromMsSessionRate)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<RateDTO> fromMsEvent(Rates rates) {
        List<RateDTO> result = new ArrayList<>();
        if (rates != null && rates.getData() != null) {
            result = rates.getData().stream()
                    .map(RateConverter::fromMsEvent)
                    .collect(Collectors.toList());
        }
        return result;
    }

}
