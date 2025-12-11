package es.onebox.event.events.converter;

import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.prices.EventPriceRecord;

import java.util.List;
import java.util.stream.Collectors;

public class EventTemplatePriceConverter {

    private EventTemplatePriceConverter() {
    }

    public static EventTemplatePriceDTO fromRecord(EventPriceRecord eventPriceRecord) {
        EventTemplatePriceDTO venueTemplatePrice = new EventTemplatePriceDTO();
        venueTemplatePrice.setRateId(eventPriceRecord.getRateId());
        venueTemplatePrice.setPrice(eventPriceRecord.getPrice());
        venueTemplatePrice.setPriceTypeId(eventPriceRecord.getPriceZoneId().longValue());
        venueTemplatePrice.setPriceTypeCode(eventPriceRecord.getPriceZoneCode());
        venueTemplatePrice.setPriceTypeDescription(eventPriceRecord.getPriceZoneDescription());
        venueTemplatePrice.setRateName(eventPriceRecord.getRateName());
        venueTemplatePrice.setPriceType(eventPriceRecord.getPriceType());
        if (eventPriceRecord.getRateGroupId() != null) {
            venueTemplatePrice.setRateGroupId(eventPriceRecord.getRateGroupId());
            venueTemplatePrice.setRateGroupName(eventPriceRecord.getRateGroupName());
        }
        return venueTemplatePrice;
    }

    public static List<EventTemplatePriceDTO> fromRecords(List<EventPriceRecord> eventPriceRecords) {
        return eventPriceRecords.stream()
                .map(EventTemplatePriceConverter::fromRecord)
                .collect(Collectors.toList());
    }
}
