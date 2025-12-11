package es.onebox.event.catalog.amqp.catalogpacksupdate;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.io.Serial;

public class CatalogPacksUpdateMessage extends AbstractNotificationMessage implements NotificationMessage {

    @Serial
    private static final long serialVersionUID = -7627818524589617557L;

    private Long packId;
    private Long channelId;
    private String origin;
    private EventIndexationType eventIndexationType;

    public Long getPackId() {
        return packId;
    }

    public void setPackId(Long packId) {
        this.packId = packId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public EventIndexationType getEventIndexationType() {
        return eventIndexationType;
    }

    public void setEventIndexationType(EventIndexationType eventIndexationType) {
        this.eventIndexationType = eventIndexationType;
    }
}
