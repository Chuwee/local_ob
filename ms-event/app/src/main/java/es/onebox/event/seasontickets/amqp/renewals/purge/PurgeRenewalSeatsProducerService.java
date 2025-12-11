package es.onebox.event.seasontickets.amqp.renewals.purge;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.ZonedDateTime;
import java.util.List;

public class PurgeRenewalSeatsProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeRenewalSeatsProducerService.class);

    @Autowired
    @Qualifier("renewalSeatsPurgeProducer")
    private DefaultProducer renewalSeatsPurgeProducer;

    public void sendMessage(Long seasonTicketId, RenewalSeatsPurgeFilter filter) {
        PurgeRenewalSeatsMessage purgeRenewalSeatsMessage = new PurgeRenewalSeatsMessage(
                seasonTicketId,
                filter.getMappingStatus(),
                filter.getRenewalStatus(),
                filter.getFreeSearch(),
                toMessage(filter.getBirthday()));
        sendMessage(purgeRenewalSeatsMessage);
    }

    private List<String> toMessage(List<FilterWithOperator<ZonedDateTime>> birthday) {
        if(CollectionUtils.isEmpty(birthday)) {
            return null;
        }
        return birthday.stream().map(this::toMessage).toList();
    }

    private String toMessage(FilterWithOperator<ZonedDateTime> birthday) {
        if(birthday == null) {
            return null;
        }
        return birthday.toString();
    }

    private void sendMessage(PurgeRenewalSeatsMessage purgeRenewalSeatsMessage) {
        try {
            renewalSeatsPurgeProducer.sendMessage(purgeRenewalSeatsMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Purge renewal seats Message could not be send", e);
        }
    }
}
