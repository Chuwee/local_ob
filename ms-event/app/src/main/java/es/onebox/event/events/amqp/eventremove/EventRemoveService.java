package es.onebox.event.events.amqp.eventremove;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EventRemoveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRemoveService.class);

    @Autowired
    @Qualifier("eventRemoveProducer")
    private DefaultProducer eventRemoveProducer;

    public void removeSeats(Integer eventId) {
        EventRemoveMessage eventRemoveMessage = new EventRemoveMessage();
        eventRemoveMessage.setEventId(eventId);
        this.sendMessage(eventRemoveMessage);
    }

    private void sendMessage(EventRemoveMessage eventRemoveMessage) {
        try {
            eventRemoveProducer.sendMessage(eventRemoveMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Refresh Data Message could not be send", e);
        }
    }

}
