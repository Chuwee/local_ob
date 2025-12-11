package es.onebox.event.sessions.amqp.avetavailability;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AvetAvailabilityMatchScheduleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvetAvailabilityMatchScheduleService.class);

    @Autowired
    @Qualifier("avetAvailabilityMatchScheduleProducer")
    private DefaultProducer avetAvailabilityMatchScheduleProducer;

    public void createAvetAvailabilitySchedule(Integer matchId, Integer sessionId) {
        AvetAvailabilityMatchScheduleMessage avetAvailabilityMatchScheduleMessage =
                new AvetAvailabilityMatchScheduleMessage();
        avetAvailabilityMatchScheduleMessage.setMatchId(matchId);
        avetAvailabilityMatchScheduleMessage.setSessionId(sessionId);
        avetAvailabilityMatchScheduleMessage.setCreate(true);
        try {
            avetAvailabilityMatchScheduleProducer.sendMessage(avetAvailabilityMatchScheduleMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] createAvetAvailabilitySchedule Message could not be send sessionId: {}, matchId: {}",
                    sessionId, matchId, e);
        }
    }

    public void deleteAvetAvailabilitySchedule(Integer matchId, Integer sessionId) {
        AvetAvailabilityMatchScheduleMessage avetAvailabilityMatchScheduleMessage =
                new AvetAvailabilityMatchScheduleMessage();
        avetAvailabilityMatchScheduleMessage.setMatchId(matchId);
        avetAvailabilityMatchScheduleMessage.setSessionId(sessionId);
        avetAvailabilityMatchScheduleMessage.setCreate(false);
        try {
            avetAvailabilityMatchScheduleProducer.sendMessage(avetAvailabilityMatchScheduleMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] deleteAvetAvailabilitySchedule Message could not be send sessionId: {}, matchId: {}",
                    sessionId, matchId, e);
        }
    }

}
