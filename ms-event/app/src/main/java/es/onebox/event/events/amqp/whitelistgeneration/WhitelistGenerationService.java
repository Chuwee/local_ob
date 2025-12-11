package es.onebox.event.events.amqp.whitelistgeneration;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class WhitelistGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistGenerationService.class);

    @Autowired
    @Qualifier("whitelistGenerationProducer")
    private DefaultProducer whitelistGenerationProducer;

    public void generateWhiteList(List<Integer> sessionIds) {
        WhitelistParamsMessage whitelistParamsMessage = new WhitelistParamsMessage();
        whitelistParamsMessage.setSessionIds(sessionIds);
        this.sendMessage(whitelistParamsMessage);
    }

    private void sendMessage(WhitelistParamsMessage whitelistParamsMessage) {
        try {
            whitelistGenerationProducer.sendMessage(whitelistParamsMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] WhitelistGenerationService Message could not be send", e);
        }
    }
}
