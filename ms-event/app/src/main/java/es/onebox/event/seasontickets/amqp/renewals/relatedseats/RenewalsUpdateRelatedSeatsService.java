package es.onebox.event.seasontickets.amqp.renewals.relatedseats;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class RenewalsUpdateRelatedSeatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenewalsUpdateRelatedSeatsService.class);

    @Autowired
    @Qualifier("renewalsUpdateRelatedSeatsProducer")
    private DefaultProducer renewalsUpdateRelatedSeatsProducer;

    public void updateSeatsAsync(Long renewalSeasonTicketSessionId, List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats) {
        RenewalsUpdateRelatedSeatsMessage message = new RenewalsUpdateRelatedSeatsMessage();
        message.setRenewalSeasonTicketSessionId(renewalSeasonTicketSessionId);
        message.setBlockSeats(blockSeats);
        updateSeatsAsync(message);
    }

    private void updateSeatsAsync(RenewalsUpdateRelatedSeatsMessage renewalsUpdateRelatedSeatsMessage) {
        try {
            renewalsUpdateRelatedSeatsProducer.sendMessage(renewalsUpdateRelatedSeatsMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Update renewals related seats message could not be send", e);
        }
    }
}
