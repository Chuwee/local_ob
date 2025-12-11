package es.onebox.event.seasontickets.amqp.renewals.elastic;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RenewalsElasticUpdaterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenewalsElasticUpdaterService.class);

    @Autowired
    @Qualifier("renewalsElasticUpdaterProducer")
    private DefaultProducer renewalsElasticUpdaterProducer;

    public void sendMessage(Long seasonTicketId, Long totalRenewals) {
        RenewalsElasticUpdaterMessage renewalsElasticUpdaterMessage = new RenewalsElasticUpdaterMessage();
        renewalsElasticUpdaterMessage.setSeasonTicketId(seasonTicketId);
        renewalsElasticUpdaterMessage.setTotalRenewals(totalRenewals);
        sendMessage(renewalsElasticUpdaterMessage);
    }

    public void sendMessage(String userId, Long seasonTicketId, Long totalRenewals) {
        RenewalsElasticUpdaterMessage renewalsElasticUpdaterMessage = new RenewalsElasticUpdaterMessage();
        renewalsElasticUpdaterMessage.setUserId(userId);
        renewalsElasticUpdaterMessage.setSeasonTicketId(seasonTicketId);
        renewalsElasticUpdaterMessage.setTotalRenewals(totalRenewals);
        sendMessage(renewalsElasticUpdaterMessage);
    }

    public void sendMessage(Long rateId) {
        RenewalsElasticUpdaterMessage renewalsElasticUpdaterMessage = new RenewalsElasticUpdaterMessage();
        renewalsElasticUpdaterMessage.setRateId(rateId);
        sendMessage(renewalsElasticUpdaterMessage);
    }

    private void sendMessage(RenewalsElasticUpdaterMessage renewalsElasticUpdaterMessage) {
        try {
            renewalsElasticUpdaterProducer.sendMessage(renewalsElasticUpdaterMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Refresh Data Message could not be send", e);
        }
    }
}
