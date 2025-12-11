package es.onebox.event.events.prices;

import es.onebox.event.events.prices.enums.PriceBuilderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultPriceBuilder implements PriceBuilder {

    private final EventPricesDao eventPricesDao;

    public DefaultPriceBuilder(EventPricesDao eventPricesDao) {
        this.eventPricesDao = eventPricesDao;
    }

    @Override
    public List<EventPriceRecord> getVenueTemplatePrices(Integer venueTemplateId, Integer eventId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> groupRateProductList) {
        return eventPricesDao.getVenueTemplatePrices(venueTemplateId, eventId, sessionIdList, rateGroupList);
    }

    @Override
    public PriceBuilderType getType() {
        return PriceBuilderType.DEFAULT;
    }
}
