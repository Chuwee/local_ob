package es.onebox.event.common.amqp.channelsuggestionscleanup;


import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpMessage.Type.EVENT;
import static es.onebox.event.common.amqp.channelsuggestionscleanup.ChannelSuggestionsCleanUpMessage.Type.SESSION;


public class ChannelSuggestionsCleanUpService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelSuggestionsCleanUpService.class);

    private final DefaultProducer suggestionCleanerProducer;

    @Autowired
    public ChannelSuggestionsCleanUpService(DefaultProducer suggestionCleanerProducer) {
        this.suggestionCleanerProducer = suggestionCleanerProducer;
    }

    public void sendEventSuggestionCleaner(Long id) {
        sendSuggestionCleaner(id, EVENT);
    }

    public void sendSessionSuggestionCleaner(Long id) {
        sendSuggestionCleaner(id, SESSION);
    }

    private void sendSuggestionCleaner(Long id, ChannelSuggestionsCleanUpMessage.Type type) {
        ChannelSuggestionsCleanUpMessage message = new ChannelSuggestionsCleanUpMessage();
        message.setId(id);
        message.setType(type);
        try {
            suggestionCleanerProducer.sendMessage(message);
        } catch (Exception e) {
            LOG.warn("[AMQP CLIENT] SuggestionCleaner Message could not be send for suggestion ID: " + id + " Type: " + type, e);
        }
    }
}
