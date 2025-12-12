package es.onebox.ms.notification.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.ms.notification.datasources.ms.event.MsEventDatasource;
import es.onebox.ms.notification.datasources.ms.event.dto.Event;
import es.onebox.ms.notification.datasources.ms.event.dto.Product;
import es.onebox.ms.notification.datasources.ms.event.dto.Session;
import es.onebox.ms.notification.datasources.ms.event.dto.Sessions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    @Cached(key = "event", expires = 5 * 60)
    public Event getEvent(@CachedArg Long eventId) {
        return msEventDatasource.getEvent(eventId);
    }

    @Cached(key = "session", expires = 5 * 60)
    public Session getSession(@CachedArg Long sessionId) {
        return msEventDatasource.getSession(sessionId);
    }

    public Sessions getSessions(List<Long> sessionIds) {
        return msEventDatasource.getSessions(sessionIds);
    }

    @Cached(key = "product", expires = 5 * 60)
    public Product getProduct(@CachedArg Long productId) {
        return msEventDatasource.getProduct(productId);
    }

}
