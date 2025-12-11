package es.onebox.event.catalog.utils;

import es.onebox.event.priceengine.simulation.record.EventChannelForCatalogRecord;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventContextUtils {

    private EventContextUtils() {
    }

    public static VenueDescriptor getVenueDescriptorBySessionId(EventIndexationContext ctx, Long sessionId) {
        Long venueTemplateId = ctx.getVenueTemplatesBySession().get(sessionId);
        if (venueTemplateId == null) {
            return null;
        }
        return ctx.getVenueDescriptor().get(venueTemplateId.intValue());
    }

    public static List<EventChannelForCatalogRecord> filterDuplicates(List<EventChannelForCatalogRecord> eventChannels) {
        return eventChannels.stream().filter(distinctByKey(c -> c.getIdcanal() + "_" + c.getIdevento())).collect(Collectors.toList());
    }

    public static List<Integer> filterChannels(List<EventChannelForCatalogRecord> eventChannels) {
        return eventChannels.stream().map(CpanelEventoCanalRecord::getIdcanal).distinct().toList();
    }

    public static List<Long> filterChannelsByChannelEvent(List<ChannelEventData> channelEvents) {
        return channelEvents.stream().map(ChannelEventData::getChannelEvent).map(ChannelEvent::getChannelId).distinct().toList();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
