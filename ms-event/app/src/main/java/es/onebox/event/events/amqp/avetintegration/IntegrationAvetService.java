package es.onebox.event.events.amqp.avetintegration;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationAvetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationAvetService.class);

    private DefaultProducer integrationAvetProducer;

    public IntegrationAvetService(DefaultProducer integrationAvetProducer) {
        this.integrationAvetProducer = integrationAvetProducer;
    }

    public IntegrationAvetService() {

    }

    public void sendMessage(Integer clubCode, Integer seasonCode, Integer capacityId) {
        IntegrationAvetMessage message =
                new IntegrationAvetMessage(clubCode, seasonCode, capacityId);
        try {
            integrationAvetProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.warn(
                    "[AMQP CLIENT] Refresh Data Message could not be send with idClub {}, idTemporada {} e idAforo {}",
                    message.getClubCode(), message.getSeasonCode(), message.getCapacityId(), e);
        }
    }

}
