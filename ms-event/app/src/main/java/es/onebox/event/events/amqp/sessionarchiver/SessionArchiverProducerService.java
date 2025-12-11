package es.onebox.event.events.amqp.sessionarchiver;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ifernandez
 */
public class SessionArchiverProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionArchiverProducerService.class);

    @Autowired
    private DefaultProducer sessionArchiverProducer;

    public void archiveSession(Long sessionId) {
        SessionArchiverMessage message = new SessionArchiverMessage();
        message.setSessionId(sessionId);
        try {
            this.sessionArchiverProducer.sendMessage(message);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Session Archiver Message could not be send", e);
        }
    }
}
