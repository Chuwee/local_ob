package es.onebox.event.datasources.integration.dispatcher.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
import es.onebox.event.datasources.integration.dispatcher.IntDispatcherServiceDatasource;
import es.onebox.event.datasources.integration.dispatcher.dto.ExternalEvent;
import es.onebox.event.datasources.integration.dispatcher.dto.ExternalSession;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class IntDispatcherRepository {

    private final IntDispatcherServiceDatasource intDispatcherServiceDatasource;

    public IntDispatcherRepository(IntDispatcherServiceDatasource intDispatcherServiceDatasource) {
        this.intDispatcherServiceDatasource = intDispatcherServiceDatasource;
    }

    @Cached(key = "externalEvent", expires = 5, timeUnit = TimeUnit.MINUTES)
    public ExternalEvent getExternalEvent(@CachedArg Long entityId, @CachedArg Long eventId) {
        return intDispatcherServiceDatasource.getExternalEvent(entityId, eventId);
    }

    @Cached(key = "externalSession",  expires = 5, timeUnit = TimeUnit.MINUTES)
    public ExternalSession getExternalSession(@CachedArg Long entityId, @CachedArg Long eventId, @CachedArg Long sessionId) {
        return intDispatcherServiceDatasource.getExternalSession(entityId, eventId, sessionId);
    }

    public void publishEvent(Long entityId, Long eventId) {
        intDispatcherServiceDatasource.publishEvent(entityId, eventId);
    }

    @Cached(key = "getExternalPresale", expires = 5, timeUnit = TimeUnit.MINUTES)
    public String getExternalPresale(@SkippedCachedArg Long entityId, @CachedArg Long eventId,
                                     @CachedArg Long sessionId, @CachedArg Long presaleId) {
        return intDispatcherServiceDatasource.getExternalPresale(entityId, eventId, sessionId, presaleId);
    }
}
