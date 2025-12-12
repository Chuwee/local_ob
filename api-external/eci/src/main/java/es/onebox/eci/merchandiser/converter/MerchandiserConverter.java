package es.onebox.eci.merchandiser.converter;

import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.eci.merchandiser.dto.MerchandiserParam;
import es.onebox.eci.merchandiser.dto.MerchandiserParamBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MerchandiserConverter {

    private MerchandiserConverter() {
    }

    public static Map<String, Long> getAggregatedEvents(List<OrderItem> items) {
        return items.stream().collect(Collectors.groupingBy(MerchandiserConverter::getMerchandiserId, Collectors.counting()));
    }

    private static String getMerchandiserId(OrderItem orderItem) {
        MerchandiserParam merchandiserParam = new MerchandiserParamBuilder()
                .id(orderItem.getTicket().getAllocation().getEvent().getId())
                .eventId(orderItem.getTicket().getAllocation().getEvent().getSupraEvent() ? orderItem.getTicket().getAllocation().getSession().getId() : orderItem.getTicket().getAllocation().getEvent().getId())
                .venueId(orderItem.getTicket().getAllocation().getVenue().getId())
                .build();
        return merchandiserParamToId(merchandiserParam);
    }

    private static String merchandiserParamToId(MerchandiserParam merchandiserParam) {
        StringBuilder sb = new StringBuilder();
        sb.append(merchandiserParam.getId());
        sb.append("_");
        sb.append(merchandiserParam.getEventId());
        sb.append("_");
        sb.append(merchandiserParam.getVenueId());
        return sb.toString();
    }
}
