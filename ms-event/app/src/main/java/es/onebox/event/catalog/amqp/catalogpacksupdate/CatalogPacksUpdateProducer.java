package es.onebox.event.catalog.amqp.catalogpacksupdate;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CatalogPacksUpdateProducer extends DefaultProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogPacksUpdateProducer.class);

    @Autowired
    public CatalogPacksUpdateProducer(@Value("${amqp.catalog-packs-update.name}") final String queueName) {
        super(queueName);
    }

    public void sendMessage(Long packId, String origin) {
        sendMessage(packId, origin, EventIndexationType.PARTIAL_BASIC);
    }

    public void sendMessage(Long packId, String origin, EventIndexationType eventIndexationType) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(CatalogPacksUpdateConfiguration.REFRESH_PACK_RELATED_EVENTS_HEADER, true);
        sendMessage(packId, origin, eventIndexationType, headers);
    }

    public void sendMessage(Long packId, String origin, String header) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(CatalogPacksUpdateConfiguration.REFRESH_PACK_RELATED_EVENTS_HEADER, true);
        headers.put(header, true);
        sendMessage(packId, origin, EventIndexationType.PARTIAL_BASIC, headers);
    }

    public void sendMessage(Long packId, String origin, EventIndexationType eventIndexationType, Map<String, Object> headers) {
        CatalogPacksUpdateMessage message = new CatalogPacksUpdateMessage();
        message.setPackId(packId);
        message.setOrigin(origin);
        message.setEventIndexationType(eventIndexationType);

        try {
            super.sendMessage(message, headers);
        } catch (Exception e) {
            LOGGER.error("Error sending message to AMQP", e);
            throw OneboxRestException.builder(CoreErrorCode.GENERIC_ERROR).build();
        }
    }

}
