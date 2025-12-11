package es.onebox.event.seasontickets.amqp.renewals.cancel;

import com.hazelcast.core.HazelcastException;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.hazelcast.core.service.HazelcastLockService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelRenewalsProcessor extends DefaultProcessor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CancelRenewalsProcessor.class);

    private final SeasonTicketRenewalsService service;
    private final HazelcastLockService hazelcastLockService;

    @Autowired
    public CancelRenewalsProcessor(SeasonTicketRenewalsService service, HazelcastLockService hazelcastLockService) {
        this.service = service;
        this.hazelcastLockService = hazelcastLockService;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        CancelRenewalsMessage message = exchange.getIn().getBody(CancelRenewalsMessage.class);
        try {
            LOGGER.info("Cancel renewal message received for user: {}", message.getUserId());
            hazelcastLockService.lockedExecution(() -> service.cancelRenewal(message.getUserId(), message.getSeasonTicketId(),
                    message.getRenewalId()), message.getUserId(), 10000, false);
        } catch (HazelcastException e) {
            LOGGER.warn("Error getting lock to update user renewal {}. Message: {}", message.getUserId(), e.getMessage());
        }
    }
}
