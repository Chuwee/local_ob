package es.onebox.event.catalog.service;

import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateProducer;
import es.onebox.event.packs.dao.PackDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class PackRefreshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackRefreshService.class);

    private final PackDao packDao;
    private final CatalogPacksUpdateProducer catalogPacksUpdateProducer;

    public PackRefreshService(PackDao packDao, CatalogPacksUpdateProducer catalogPacksUpdateProducer) {
        this.packDao = packDao;
        this.catalogPacksUpdateProducer = catalogPacksUpdateProducer;
    }

    public void refreshEventRelatedPacks(Long eventId, boolean mustUpdatePacks, String origin) {
        if (!mustUpdatePacks) return;

        Set<Integer> packIds = packDao.getPackIdsRelatedToEventId(eventId.intValue());
        if (packIds.isEmpty()) return;

        LOGGER.warn("[EVENT CATALOG] Refresh packs related to eventId: {} packs: {}", eventId, packIds);

        packIds.forEach(packId -> {
            catalogPacksUpdateProducer.sendMessage(packId.longValue(), origin, null, null);
        });
    }

}
