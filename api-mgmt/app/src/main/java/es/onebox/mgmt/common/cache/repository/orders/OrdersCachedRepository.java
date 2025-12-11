package es.onebox.mgmt.common.cache.repository.orders;

import com.hazelcast.map.IMap;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import es.onebox.mgmt.common.cache.enums.OrdersCachedMappingsType;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class OrdersCachedRepository {

    private static final String MAPPING_CACHED_KEY = "CACHED_WITH_SALES";
    private final HazelcastMapService hazelcastMapService;
    private final String mapEventsWithSales;
    private final String mapSessionsWithSales;
    private final String mapProductsWithSales;
    private final Integer timeToLive;

    public OrdersCachedRepository (HazelcastMapService hazelcastMapService, Integer timeToLive,
                                   String mapEventsWithSales, String mapSessionsWithSales, String mapProductsWithSales) {
        this.hazelcastMapService = hazelcastMapService;
        this.timeToLive = timeToLive;
        this.mapSessionsWithSales = mapSessionsWithSales;
        this.mapEventsWithSales = mapEventsWithSales;
        this.mapProductsWithSales = mapProductsWithSales;
    }

    @SuppressWarnings("unchecked")
    public Long get (Long sessionId, OrdersCachedMappingsType cachedMapping) {
        String map = getMapping(cachedMapping);
        IMap<String, Long> sessionsOrdersCachedMap = hazelcastMapService.getMap(map);
        return sessionsOrdersCachedMap.get(compositeKey(sessionId));
    }

    @SuppressWarnings("unchecked")
    public boolean contains (Long sessionId, OrdersCachedMappingsType cachedMapping) {
        String map = getMapping(cachedMapping);
        IMap<String, Long> sessionsOrdersCachedMap = hazelcastMapService.getMap(map);
        final String key = compositeKey(sessionId);
        return sessionsOrdersCachedMap.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public void create (Long sessionId, Long count, OrdersCachedMappingsType cachedMapping) {
        String map = getMapping(cachedMapping);
        IMap<String, Long> sessionsOrdersCachedMap = hazelcastMapService.getMap(map);
        if (sessionsOrdersCachedMap != null) {
            sessionsOrdersCachedMap.put(compositeKey(sessionId), count, timeToLive, TimeUnit.SECONDS);
        }
    }

    @SuppressWarnings("unchecked")
    public void delete (Long id, OrdersCachedMappingsType cachedMapping) {
        String map = getMapping(cachedMapping);
        IMap<String, Long> sessionsOrdersCachedMap = hazelcastMapService.getMap(map);
        String key = compositeKey(id);
        sessionsOrdersCachedMap.delete(key);
    }

    @SuppressWarnings("unchecked")
    public void deleteAll() {
        IMap<String, Long> sessionsOrdersCachedMap = hazelcastMapService.getMap(mapSessionsWithSales);
        IMap<String, Long> eventsOrdersCachedMap = hazelcastMapService.getMap(mapEventsWithSales);
        sessionsOrdersCachedMap.clear();
        eventsOrdersCachedMap.clear();
    }


    private static String compositeKey (Long id) {
        Assert.notNull(id, "id is null");
        return String.format("%s_%s", MAPPING_CACHED_KEY, id);
    }

    private String getMapping (OrdersCachedMappingsType cachedMapping) {
        switch (cachedMapping) {
            case EVENTS_WITH_SALES -> {
                return mapEventsWithSales;
            }
            case SESSIONS_WITH_SALES -> {
                return mapSessionsWithSales;
            }
            case PRODUCTS_WITH_SALES -> {
                return mapProductsWithSales;
            }
            default -> {
                return null;
            }
        }
    }

}
