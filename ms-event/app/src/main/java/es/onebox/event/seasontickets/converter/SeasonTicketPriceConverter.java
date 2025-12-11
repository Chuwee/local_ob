package es.onebox.event.seasontickets.converter;

import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.seasontickets.dto.SeasonTicketPriceDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketPriceConverter {

    private SeasonTicketPriceConverter() {
    }

    public static SeasonTicketPriceDTO fromRecord(EventPriceRecord record) {
        SeasonTicketPriceDTO venueTemplatePrice = new SeasonTicketPriceDTO();
        venueTemplatePrice.setRateId(record.getRateId());
        venueTemplatePrice.setPrice(record.getPrice());
        venueTemplatePrice.setPriceTypeId(record.getPriceZoneId().longValue());
        venueTemplatePrice.setPriceTypeCode(record.getPriceZoneCode());
        venueTemplatePrice.setPriceTypeDescription(record.getPriceZoneDescription());
        venueTemplatePrice.setRateName(record.getRateName());
        venueTemplatePrice.setPriceType(record.getPriceType());
        venueTemplatePrice.setPriceTypeColor(record.getPriceZoneColor());
        return venueTemplatePrice;
    }

    public static List<SeasonTicketPriceDTO> fromRecords(List<EventPriceRecord> eventPriceRecords) {
        return eventPriceRecords.stream()
                .map(SeasonTicketPriceConverter::fromRecord)
                .collect(Collectors.toList());
    }
}
