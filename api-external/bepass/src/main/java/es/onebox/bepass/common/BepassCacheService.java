package es.onebox.bepass.common;

import es.onebox.hazelcast.core.service.HazelcastMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BepassCacheService {

    private static final String PREFIX = "bepass_event_mapping_";

    private final HazelcastMapService hazelcastMapService;
    private final String map;

    public BepassCacheService(HazelcastMapService hazelcastMapService, @Value("${spring.application.name}") String app) {
        this.hazelcastMapService = hazelcastMapService;
        this.map = app;
    }

    public String getExternalEventId(Long sessionId) {
        return this.hazelcastMapService.getObjectFromMap(map, PREFIX + sessionId.toString());
    }

    public void mapExternalEventId(String externalId, Long sessionId) {
        this.hazelcastMapService.putIntoMapWithTTL(map, PREFIX + sessionId.toString(), externalId, 1, TimeUnit.HOURS);
    }
}
