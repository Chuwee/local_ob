package es.onebox.event.catalog.amqp.catalogupdate;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatalogUpdateQueueProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogUpdateQueueProducer.class);

    @Autowired
    public CatalogUpdateQueueProducer(@Value("${amqp.catalog-update.name}") final String queueName) {
        super(queueName);
    }

    public void sendMessage(final List<Long> channelIds, final Long eventId) {
        CatalogUpdateMessage message = new CatalogUpdateMessage();
        message.setChannelIds(channelIds);
        message.setEventId(eventId);
        try {
            super.sendMessage(message);
        } catch (Exception e) {
            LOG.error("Error sending message to AMQP", e);
            throw OneboxRestException.builder(MsEventErrorCode.AMQP_PUSH_EXCEPTION).build();
        }
    }

}
