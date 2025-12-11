package es.onebox.event.catalog.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dao.CatalogChannelEventAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionAgencyCouchDao;
import es.onebox.event.catalog.dao.CatalogChannelSessionCouchDao;
import es.onebox.event.catalog.dao.CatalogEventCouchDao;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ChannelProductsCouchDao;
import es.onebox.event.products.dao.couch.ChannelProductDocument;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.secondarymarket.dto.DatesDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.secondarymarket.service.SecondaryMarketService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ChannelCatalogAdapter extends ChannelCatalogESQueryAdapter implements Adapter {

    protected final CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    protected final CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao;
    protected final CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao;
    protected final CatalogChannelSessionCouchDao catalogChannelSessionCouchDao;
    protected final CatalogEventCouchDao catalogEventCouchDao;
    protected final CatalogSessionCouchDao catalogSessionCouchDao;
    protected final SecondaryMarketService secondaryMarketService;
    protected final EventSecondaryMarketConfigService eventSecondaryMarketConfigService;
    protected final EventPromotionCouchDao eventPromotionCouchDao;
    private final ChannelProductsCouchDao channelProductsCouchDao;

    protected ChannelCatalogAdapter(
            CatalogChannelEventCouchDao catalogChannelEventCouchDao,
            CatalogChannelEventAgencyCouchDao catalogChannelEventAgencyCouchDao,
            CatalogChannelSessionAgencyCouchDao catalogChannelSessionAgencyCouchDao,
            CatalogChannelSessionCouchDao catalogChannelSessionCouchDao,
            CatalogEventCouchDao catalogEventCouchDao,
            CatalogSessionCouchDao catalogSessionCouchDao,
            SecondaryMarketService secondaryMarketService,
            EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
            EventPromotionCouchDao eventPromotionCouchDao, ChannelProductsCouchDao channelProductsCouchDao) {
        this.catalogChannelSessionAgencyCouchDao = catalogChannelSessionAgencyCouchDao;
        this.catalogChannelEventCouchDao = catalogChannelEventCouchDao;
        this.catalogChannelEventAgencyCouchDao = catalogChannelEventAgencyCouchDao;
        this.catalogChannelSessionCouchDao = catalogChannelSessionCouchDao;
        this.catalogEventCouchDao = catalogEventCouchDao;
        this.catalogSessionCouchDao = catalogSessionCouchDao;
        this.secondaryMarketService = secondaryMarketService;
        this.eventSecondaryMarketConfigService = eventSecondaryMarketConfigService;
        this.eventPromotionCouchDao = eventPromotionCouchDao;
        this.channelProductsCouchDao = channelProductsCouchDao;
    }

    public Event getCatalogEvent(Long eventId) {
        Event event = catalogEventCouchDao.get(eventId.toString());
        if (event == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_EVENT_NOT_FOUND).
                    setMessage("Event not found for event: " + eventId).build();
        }
        return event;
    }

    public Session getCatalogSession(Long sessionId) {
        Session session = catalogSessionCouchDao.get(sessionId.toString());
        if (session == null) {
            throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_SESSION_NOT_FOUND).
                    setMessage("Session not found for session: " + sessionId).build();
        }
        return session;
    }

    public <T extends ChannelSession> EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig(Long eventId, T channelSession) {
        if (channelSession == null || channelSession.getSecondaryMarketConfig() == null) {
            return null;
        }
        DatesDTO dates = channelSession.getSecondaryMarketConfig().getDates();

        if (dates != null && Boolean.TRUE.equals(dates.getEnabled())) {
            return eventSecondaryMarketConfigService.getEventSecondaryMarketConfig(eventId);
        }

        return null;
    }

    public List<Long> getChannelProductIds(Long channelId) {
        ChannelProductDocument document = channelProductsCouchDao.get(channelId.toString());
        if(document == null) {
            return new ArrayList<>();
        }
        return document.getProductIds();
    }

    protected Map<Integer, Boolean> getSecondaryMarketStatusByEntity(List<Event> events) {
        List<Integer> entityIds = events.stream().map(Event::getEntityId).toList();
        return secondaryMarketService.getCachedAllowSecondaryMarket(entityIds);
    }

}
