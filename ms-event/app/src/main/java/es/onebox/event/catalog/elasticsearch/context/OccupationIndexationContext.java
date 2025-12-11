package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongFunction;

public class OccupationIndexationContext extends BaseIndexationContext<ChannelSessionForOccupationIndexation, ChannelSessionAgencyForOccupationIndexation> {

    private final Long sessionId;
    private final Map<Long, Optional<SessionData>> sessions;
    private EventData eventData;
    private List<ChannelEventData> channelEvents;
    private List<ChannelEventAgencyData> channelEventAgencies;
    private Map<Long, List<Long>> channelsAgencies;

    public OccupationIndexationContext(CpanelEventoRecord event, Long sessionId) {
        super(event);
        this.sessionId = sessionId;
        this.sessions = new HashMap<>();
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setEventData(EventData eventData) {
        this.eventData = eventData;
    }

    public EventData getEventData() {
        return eventData;
    }

    public void setChannelEvents(List<ChannelEventData> channelEvents) {
        this.channelEvents = channelEvents;
    }

    public Optional<ChannelEventData> getChannelEvent(Long channelId) {
        return channelEvents.stream()
                .filter(channelEventData -> channelEventData.getChannelEvent().getChannelId().equals(channelId))
                .findFirst();
    }

    public List<ChannelEventData> getChannelEvents() {
        return channelEvents;
    }

    public Optional<SessionData> getSessionOr(Long sessionId, LongFunction<SessionData> getter) {
        return sessions.computeIfAbsent(sessionId, id -> Optional.ofNullable(getter.apply(id)));
    }

    public Map<Long, List<Long>> getChannelsAgencies() {
        return channelsAgencies;
    }

    public List<Long> getChannelAgencies(Long channelId) {
        if (this.channelsAgencies == null) {
            return null;
        }
        return this.channelsAgencies.get(channelId);
    }

    public void setChannelsWithAgencies(Map<Long, List<Long>> channelAgencies) {
        this.channelsAgencies = channelAgencies;
    }

    public List<ChannelEventAgencyData> getChannelEventAgencies() {
        return channelEventAgencies;
    }

    public void setChannelEventAgencies(List<ChannelEventAgencyData> channelEventAgencies) {
        this.channelEventAgencies = channelEventAgencies;
    }
}
