package es.onebox.event.catalog.amqp.catalogpacksupdate;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelPackIndexer;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static es.onebox.event.packs.utils.PackUtils.PACK_CATALOG_REFRESH;

public class CatalogPacksUpdateProcessor extends DefaultProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogPacksUpdateProcessor.class);

    private static final String EXPIRE_SESSIONS_HEADER = "EXPIRE_SESSIONS";
    public static final String PACK_COMM_ELEMENTS_UPDATE_HEADER = "PACK_COMM_ELEMENTS_UPDATE";

    @Autowired
    private ChannelPackIndexer channelPackIndexer;

    @Override
    public void execute(Exchange exchange) throws Exception {
        long initTime = System.currentTimeMillis();

        CatalogPacksUpdateMessage message = exchange.getIn().getBody(CatalogPacksUpdateMessage.class);
        boolean isFullUpsert = isFullUpsert(exchange);
        try {
            if (message.getPackId() != null) {
                LOG.info("{} Begin process with params packId: {} , channelId: {}, origin: {}, isFullUpsert: {}", PACK_CATALOG_REFRESH, message.getPackId(), message.getChannelId(), message.getOrigin(), isFullUpsert);
                EventIndexationType eventIndexationType = message.getEventIndexationType() == null ? EventIndexationType.PARTIAL_BASIC : message.getEventIndexationType();
                channelPackIndexer.indexChannelPacks(message.getChannelId(), message.getPackId(), mustUpdateEvent(exchange), eventIndexationType, isFullUpsert);
                LOG.info("{} Process finished with params packId: {} , channelId: {} in {} ms", PACK_CATALOG_REFRESH, message.getPackId(), message.getChannelId(),
                        (System.currentTimeMillis() - initTime));
            } else {
                LOG.error("{} Update process could not be performed because id or type cannot be null. Received message -> packId: {} , channelId: {}", PACK_CATALOG_REFRESH, message.getPackId(), message.getChannelId());
            }
        } catch (Exception e) {
            LOG.error("{} Update process could not be performed due to the following error: {}. Received message -> packId: {} , channelId: {}", PACK_CATALOG_REFRESH, e.getMessage(), message.getPackId(), message.getChannelId(), e);
        }
    }

    private boolean isFullUpsert(Exchange exchange) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        if (headers.containsKey(EXPIRE_SESSIONS_HEADER)) {
            return !(Boolean) headers.get(EXPIRE_SESSIONS_HEADER);
        }
        if (headers.containsKey(PACK_COMM_ELEMENTS_UPDATE_HEADER)) {
            return !(Boolean) headers.get(PACK_COMM_ELEMENTS_UPDATE_HEADER);
        }
        return true;
    }

    private static boolean mustUpdateEvent(Exchange exchange) {
        Object header = exchange.getIn().getHeader(CatalogPacksUpdateConfiguration.REFRESH_PACK_RELATED_EVENTS_HEADER);
        return header != null && BooleanUtils.isTrue((Boolean) header);
    }


}

