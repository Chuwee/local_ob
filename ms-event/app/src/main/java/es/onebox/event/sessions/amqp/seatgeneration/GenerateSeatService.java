package es.onebox.event.sessions.amqp.seatgeneration;

import es.onebox.event.events.enums.EventType;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.stream.Collectors;

public class GenerateSeatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateSeatService.class);

    @Autowired
    @Qualifier("generateSeatProducer")
    private DefaultProducer generateSeatProducer;

    public void generateSeats(Long sessionId, boolean isSessionPack, Map<Integer, Integer> blockingActions, boolean isAvetSocket) {
        generateSeats(sessionId, isSessionPack, blockingActions, null, isAvetSocket);
    }

    public void generateSBSeats(Long sessionId) {
        generateSeats(sessionId, false, null, EventType.ACTIVITY, false);
    }

    public void generateSeats(Long sessionId, boolean isSessionPack, Map<Integer, Integer> blockingActions, EventType type, boolean isAvetSocket) {
        PreGenerateSeatMessage message = new PreGenerateSeatMessage();
        message.setSessionId(sessionId);
        message.setSeasonPass(isSessionPack);
        message.setCreation(true);
        if(isAvetSocket) {
            message.setDelayedCreationSeconds(10);
        }

        if (type != null) {
            message.setEventType(type.getId());
        }
        if (blockingActions != null) {
            message.setBlockingReasons(blockingActions.entrySet().stream().
                    map(e -> new BlockingReason(e.getKey(), e.getValue())).collect(Collectors.toList()));
        }

        sendMessage(message);
    }

    private void sendMessage(PreGenerateSeatMessage generateSeatMessage) {
        try {
            generateSeatProducer.sendMessage(generateSeatMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] GenerateSeat Message could not be send", e);
        }
    }

}
