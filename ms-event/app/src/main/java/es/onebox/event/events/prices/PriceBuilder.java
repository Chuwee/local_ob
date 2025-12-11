package es.onebox.event.events.prices;

import es.onebox.event.events.prices.enums.PriceBuilderType;

import java.util.List;

public interface PriceBuilder {
    List<EventPriceRecord> getVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> groupRateProductList);
    PriceBuilderType getType();
}
