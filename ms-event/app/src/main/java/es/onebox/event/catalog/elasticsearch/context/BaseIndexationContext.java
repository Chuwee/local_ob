package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseIndexationContext<C extends ChannelSessionPriceZones, T extends ChannelSessionPriceZones> {

    private final Long eventId;
    private final CpanelEventoRecord event;
    private final EventType eventType;
    private final Map<Class<? extends BaseEventData>, List<BaseEventData>> documentsIndexed;
    private List<EventPromotion> eventPromotions;
    private Map<Long, ChannelInfo> channels;
    private Map<Long, List<Long>> quotasByChannel;
    private List<SecondaryMarketSearch> secondaryMarketForSale;
    private Map<Long, SBSession> sbBySession;
    private List<C> channelSessionsToIndex;
    private List<T> channelAgencySessionsToIndex;

    protected BaseIndexationContext(CpanelEventoRecord event) {
        this.event = event;
        this.eventId = event.getIdevento().longValue();
        this.eventType = EventType.byId(event.getTipoevento());
        this.documentsIndexed = new HashMap<>();
        this.channelSessionsToIndex = new ArrayList<>();
        this.channelAgencySessionsToIndex = new ArrayList<>();
    }

    public Long getEventId() {
        return eventId;
    }

    public CpanelEventoRecord getEvent() {
        return event;
    }

    public EventType getEventType() {
        return eventType;
    }

    public List<EventPromotion> getEventPromotions() {
        return eventPromotions;
    }

    public void setEventPromotions(List<EventPromotion> eventPromotions) {
        this.eventPromotions = eventPromotions;
    }

    public void setQuotasByChannel(Map<Long, List<Long>> quotasByChannel) {
        this.quotasByChannel = quotasByChannel;
    }

    public List<Long> getQuotasByChannel(Long channelId) {
        return quotasByChannel.get(channelId);
    }

    public void addDocumentIndexed(BaseEventData document) {
        documentsIndexed
                .computeIfAbsent(document.getClass(), k -> new ArrayList<>())
                .add(document);
    }

    public void addDocumentsIndexed(List<? extends BaseEventData> documents) {
        documents.forEach(this::addDocumentIndexed);
    }

    public int getNumDocumentsIndexed() {
        return documentsIndexed.values().stream().mapToInt(List::size).sum();
    }

    public <E extends BaseEventData> List<E> getDocumentsIndexed(Class<E> type) {
        List<BaseEventData> data = documentsIndexed.get(type);
        if (CollectionUtils.isNotEmpty(data)) {
            return (List<E>) data;
        }
        return new ArrayList<>();
    }

    public List<C> getChannelSessionsToIndex() {
        return channelSessionsToIndex;
    }

    public void setChannelSessionsToIndex(List<C> channelSessionsToIndex) {
        this.channelSessionsToIndex = channelSessionsToIndex;
    }

    public List<T> getChannelAgencySessionsToIndex() {
        return channelAgencySessionsToIndex;
    }

    public void setChannelAgencySessionsToIndex(List<T> channelAgencySessionsToIndex) {
        this.channelAgencySessionsToIndex = channelAgencySessionsToIndex;
    }

    public List<SecondaryMarketSearch> getSecondaryMarketForSale() {
        return secondaryMarketForSale;
    }

    public void setSecondaryMarketForSale(List<SecondaryMarketSearch> secondaryMarketForSale) {
        this.secondaryMarketForSale = secondaryMarketForSale;
    }

    public Map<Long, SBSession> getSbBySession() {
        return sbBySession;
    }

    public void setSbBySession(Map<Long, SBSession> sbBySession) {
        this.sbBySession = sbBySession;
    }

    public Map<Long, ChannelInfo> getChannels() {
        return channels;
    }

    public ChannelInfo getChannelInfo(Long channelId) {
        if (this.channels == null) {
            return null;
        }
        return this.channels.get(channelId);
    }

    public void setChannels(Map<Long, ChannelInfo> channels) {
        this.channels = channels;
    }

}
