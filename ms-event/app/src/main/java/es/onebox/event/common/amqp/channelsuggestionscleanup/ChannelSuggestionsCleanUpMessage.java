package es.onebox.event.common.amqp.channelsuggestionscleanup;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

public class ChannelSuggestionsCleanUpMessage extends AbstractNotificationMessage {

    private static final long serialVersionUID = 1L;

    public enum Type {
        EVENT,
        SESSION
    }

    private Long id;
    private ChannelSuggestionsCleanUpMessage.Type type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}