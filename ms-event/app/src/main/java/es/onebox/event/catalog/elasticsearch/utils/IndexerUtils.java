package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dto.VenueTemplateType;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionPriceZones;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IndexerUtils {

    private IndexerUtils() {
    }

    public static Function<ChannelSessionData, ChannelSessionData> cleanContainerOccupation() {
        return cs -> {
            cs.getChannelSession().setContainerOccupations(null);
            return cs;
        };
    }

    public static Function<ChannelSessionAgencyData, ChannelSessionAgencyData> cleanContainerOccupationForAgency() {
        return cs -> {
            cs.getChannelSessionAgency().setContainerOccupations(null);
            return cs;
        };
    }

    public static List<ChannelSession> toCouchAdapter(List<ChannelSessionData> in) {
        return in.stream().map(ChannelSessionData::getChannelSession).collect(Collectors.toList());
    }

    public static List<ChannelSessionAgency> toCouchAdapterForAgency(List<ChannelSessionAgencyData> in) {
        return in.stream().map(ChannelSessionAgencyData::getChannelSessionAgency).collect(Collectors.toList());
    }

    public static boolean hasSameQuotas(ChannelSessionPriceZones channelSession, SessionWithQuotasDTO sessionWithQuotas) {
        return channelSession.getSessionId().equals(sessionWithQuotas.getSessionId()) &&
                sortAndDistinct(channelSession.getQuotas()).equals(sortAndDistinct(sessionWithQuotas.getQuotas()));
    }

    public static List<Key> buildSessionChannelKeys(List<ChannelSessionData> channelCs) {
        return channelCs.stream()
                .map(s -> new Key(new String[]{
                        s.getChannelSession().getChannelId().toString(),
                        s.getChannelSession().getSessionId().toString()}))
                .collect(Collectors.toList());
    }

    public static EventType getEventTypeByVenueTemplateType(Integer venueTemplateType) {
        VenueTemplateType type = VenueTemplateType.byId(venueTemplateType);
        if (type == null) {
            return null;
        }
        return switch (type) {
            case ACTIVITY -> EventType.ACTIVITY;
            case AVET -> EventType.AVET;
            case DEFAULT -> EventType.NORMAL;
            default -> null;
        };
    }

    public static <T> List<T> sortAndDistinct(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().sorted().distinct().collect(Collectors.toList());
    }

    public static List<CpanelCanalEventoRecord> getChannelEvents(List<CpanelCanalEventoRecord> channelEvents, CpanelEventoRecord eventRecord) {
        for (CpanelCanalEventoRecord channelEvent : channelEvents) {
            if (CommonUtils.isTrue(channelEvent.getUsafechasevento())) {
                channelEvent.setFechapublicacion(eventRecord.getFechapublicacion());
                channelEvent.setFechaventa(eventRecord.getFechaventa());
                channelEvent.setFechafin(eventRecord.getFechafin());
                channelEvent.setFechainicioreserva(eventRecord.getFechainicioreserva());
                channelEvent.setFechafinreserva(eventRecord.getFechafinreserva());
            }
        }
        return channelEvents;
    }

}
